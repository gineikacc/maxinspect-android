package com.maxinspect

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory.getDefaultInstance
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.model.Message
import com.maxinspect.models.EmailProduct
import com.maxinspect.models.EmailReceipt
import com.maxinspect.models.Product
import com.maxinspect.models.Receipt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.Base64

class Util {


    companion object {



        fun getGmailService(account: GoogleSignInAccount, ctx: Context): Gmail {
            val accountName = account.displayName
                    if (accountName.isNullOrEmpty()) {
                        Log.e("LoginPane", "Account name is empty!")
                    } else {

                        Log.e("LoginPane", "Account name : $accountName")
                        Log.e("LoginPane", "Account memes : ${account.account?.name}")
                    }

            Log.w("LoginPane", "2")
            val credential = GoogleAccountCredential.usingOAuth2(
                ctx, listOf("https://www.googleapis.com/auth/gmail.readonly")
            )

            account.account?.let {
                credential.selectedAccount = it
            } ?: Log.e("LoginPane", "Account selection failed; account data is missing.")

            val transport: HttpTransport = NetHttpTransport()
            val jsonFactory: JsonFactory = getDefaultInstance()
            return Gmail.Builder(transport, jsonFactory, credential)
                .setApplicationName("MaxInspect")
                .build()
        }

        fun parseEmail(email: Message) : EmailReceipt {
            val totalCost = (email.snippet.split(" ")[2].toDouble() * 100).toInt()
            val b64Content = email.payload.parts[0].body.data .replace('-', '+').replace('_', '/')
            val decodedBytes = Base64.getDecoder().decode(b64Content.toByteArray())
            var content = String(decodedBytes)
            content = content.replace("\r\n", " ").replace(Regex(" {2,}"), " ")
            //time of purchase 2024-11-28 20:03:41
            val dateRegex = Regex("""\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}""")
            val date = dateRegex.find(content)?.groupValues?.get(0)

            content = content.slice(content.indexOf("<table") until content.indexOf("</table"))
            content = content.substring(content.lastIndexOf("<pre>"))
// Cashier check (different format)
            val startWords = content.slice(0 until 100).split(" ")
            val hasCashierID = startWords[2] == "Kasininkas"

            var checkID = if (hasCashierID) startWords[1] else startWords[2]
            checkID = checkID.drop(1)
// if cashier length of first 10 words, if not length of first 3. (include WC for missing spaces

            var trimLength = 0
            for (i in 0 until if (hasCashierID) 10 else 3) {
                trimLength += startWords[i].length + 1
            }

            content = content.slice(trimLength until content.indexOf("</pre>"))
            content = content.slice(0 until content.indexOf("====="))
            content = content.trim()

            val lines = content.split(Regex(" [ABN] "))


            var products = lines.map { it.trim() }.map {
                if (Regex("A$").containsMatchIn(it)) it.slice(0 until it.length - 2) else it
            }

            products = products.filter { it.isNotEmpty() }
            //products = products.filter { !it.startsWith("Nuolaida") }
            products = products.filter { !it.startsWith("-----") }
            val finalProds = mutableListOf<EmailProduct>()
            Log.i("LoginPane", "Products vvv ")
            var i = 0
            while (i < products.size) {
                val p = products[i]
                val tokens = p.split(" ")

                val amountRegex = Regex("""\d,\d\d X (\d(,\d\d\d)?) (vnt\.|kg)""")
                val amountData = amountRegex.find(p)
                val priceIndex: Int
                val titleEndIndex: Int
                if (amountData != null) {
                    priceIndex = amountData.range.last + 1
                    titleEndIndex = amountData.range.first - 1
                } else {
                    priceIndex = p.length - tokens.last().length
                    titleEndIndex = priceIndex - 1
                }

                val title = p.slice(0 until titleEndIndex)
                val price = Math.round(tokens.last().replace(",", ".").toDouble() * 100).toInt()
                var amount = 1.0
                if (!hasCashierID) {
                    amount = (amountData?.groupValues?.get(1)?.replace(",", "."))?.toDoubleOrNull() ?: 1.0
                }
                // If we got the cashier, need to check for amount in next line
                // Peek next to see if it's a discount line
                var discount = 0
                var peekLine = i + 1
                if (peekLine < products.size) {
                    // Amount check
                    if (hasCashierID) {
                        val peekAmountData = amountRegex.find(products[peekLine])
                        amount = (peekAmountData?.groupValues?.get(1)?.replace(",", "."))?.toDoubleOrNull() ?: 1.0
                    }
                    // Discount
                    if (peekLine < products.size && products[peekLine].contains("Nuolaida")) {
                        val discountTokens = products[peekLine].split(" ")
                        if (discountTokens.last() == "A") discountTokens.dropLast(1)
                        val discountTemp = discountTokens.last().replace(',', '.').toDouble()
                        discount = Math.round(discountTemp * 100).toInt()
                        i++
                    }
                    // Check if Refunded --> Bail on product entry
                    if (peekLine < products.size && products[peekLine].contains("TAISYMAS")) {
                        Log.i("LoginPane", "L : Refund detected")
                        Log.i("LoginPane", products[peekLine])
                        continue
                    }
                }

                var product = EmailProduct(title, price+discount, amount)
                finalProds.add(product)
                Log.i("LoginPane", product.toString())
                i++
            }

            val check = EmailReceipt(owner="_", checkID, finalProds.toTypedArray(), date ?: "NONE", totalCost)
            return check
        }


        fun dbUploadCSVFile(uri : Uri, ctx: Context) {
            val contentResolver = ctx.contentResolver
            val inputStream = contentResolver.openInputStream(uri)
            val file = File(ctx.cacheDir, "uploaded_file.csv")

            // Copy the file from URI to a temporary file
            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            // Prepare the request body for the file
            val requestBody = file.asRequestBody("text/csv".toMediaTypeOrNull())

            // Create multipart body part
            val multipartBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.name, requestBody) // Add the file part
                .build()

            // Create the request
            val request = Request.Builder()
                .url("http://gineika.cc/uploadcsv")
                .post(multipartBody) // Send the entire multipart body
                .build()

            // Initialize OkHttpClient
            val client = OkHttpClient()

            // Execute the request asynchronously
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        Log.d("LoginPane", "File uploaded successfully")
                    } else {
                        Log.e("LoginPane", "Failed to upload: ${response.code}")
                    }
                } catch (e: Exception) {
                    Log.e("LoginPane", "Error: ${e.localizedMessage}")
                }
            }
        }

        fun dbCreateReceipt(receipt : EmailReceipt) {
            val client = OkHttpClient()

            // Build the request
            val request = Request.Builder()
                .url("http://gineika.cc/createreceipt")
                .post(RequestBody.create("application/json".toMediaType(), Json.encodeToString(receipt)))
                .build()

            // Use a coroutine to handle the network call off the main thread
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Execute the request
                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) {
                            Log.e("LoginPane", "Unexpected code $response")
                        } else {
                            // Print the response body
                            Log.w("LoginPane", response.body?.string() ?: "noinfo")
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }


        // Example function to list the emails from the Gmail account
        fun getAllEmailReceipts(gmailService: Gmail, owner: String, uiScope: CoroutineScope) {
            uiScope.launch {
                try {
                    val request = gmailService.users().messages().list("me")
                        .setQ("from:noreply.code.provider@maxima.lt subject:\"Jūsų apsipirkimo MAXIMOJE kvitas\"")
                    val response = withContext(Dispatchers.IO) { request.execute() }
                    for (message in response.messages) {
                        // Process each email message
                        val email: Message = withContext(Dispatchers.IO) {
                            gmailService.users().messages().get("me", message.id).execute()
                        }

                        var receipt = parseEmail(email)
                        receipt.owner = owner
                        Log.e("LoginPane", Json.encodeToString(receipt))
                        dbCreateReceipt(receipt)
                        Globals.receipts.add(Receipt(receipt.cost, receipt.dateIssued))
                        var products = dbGetProducts(receipt.checkID.toInt(), owner)
                        products.forEach { Globals.products.add(it) }
                    }
                } catch (e: Exception) {
                    Log.e("LoginPane", "AAAAAAAA")
                    Log.e("LoginPane", e.message.toString())
                }
            }
        }

        fun dbGetProducts(checkID: Int, owner: String) : Array<Product>{
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("gineika.cc/getreceiptdetails?owner=$owner&id=$checkID")
                .build()
            lateinit var prods : Array<Product>
            CoroutineScope(Dispatchers.IO).launch {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    prods = Json.decodeFromString<Array<Product>>(response.body?.string().toString())
                    Log.d("LoginPane", "File uploaded successfully")
                } else {
                    Log.e("LoginPane", "Failed to upload: ${response.code}")
                }
            }

            return prods
        }

    }
}