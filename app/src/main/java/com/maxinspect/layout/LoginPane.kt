package com.maxinspect.layout

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn.getClient
import com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory.getDefaultInstance
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.model.Message
import com.maxinspect.R
import com.maxinspect.models.EmailCheck
import com.maxinspect.models.EmailProduct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Base64

class LoginPane : ComponentActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val signInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // Handle the result of the sign-in activity here
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val task: Task<GoogleSignInAccount> =
                    getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    val gmailService = getGmailService(account)
                    // Signed in successfully, show authenticated UI
                    updateUI(account)
                } catch (e: ApiException) {
                    // Sign in failed, handle exception
                    Log.w("LoginPane", "FAIL >> signInResult:failed code=" + e.statusCode)
                    updateUI(null)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_pane)

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(
                Scope("https://www.googleapis.com/auth/gmail.readonly"),
                Scope("https://www.googleapis.com/auth/userinfo.email")  // People API scope
            )
            .requestProfile()
            .build()

        googleSignInClient = getClient(this, gso)



        // Set up sign-in button
        findViewById<Button>(R.id.OAuthButton).setOnClickListener {
            signIn()
        }
    }

    private fun signIn(){
        val signInIntent = getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signInIntent
        signInLauncher.launch(signInIntent)
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {
            // User is signed in, display user information or proceed to the next activity
            Log.w("LoginPane", "Signed in SUCCessfully")
            Log.w("LoginPane", "Name: ${account.displayName.toString()}")
            Log.w("LoginPane", "Email: ${account.email.toString()}")
            Log.w("LoginPane", "ID: ${account.id}")
            Log.w("LoginPane", "ACCESS TOKEN (??) : ${account.idToken}")
            Log.w("LoginPane", "Photo: ${account.photoUrl}")
            Log.w("LoginPane", "Given name: ${account.givenName}")
            Log.w("LoginPane", "Fam name: ${account.familyName}")
            Log.w("LoginPane", "R Scopes: ${account.requestedScopes}")
            Log.w("LoginPane", "G Scopes: ${account.grantedScopes}")
            Log.w("LoginPane", "Auth code: ${account.serverAuthCode}")
            Log.w("LoginPane", "1")
            val gmailService = getGmailService(account)

            listEmails(gmailService)
            googleSignInClient.signOut()
        } else {
            Log.w("LoginPane", "Signed in FAILED")
            // Sign-in failed, show error message
        }
    }

    // Create a Gmail service instance
    private fun getGmailService(account: GoogleSignInAccount): Gmail {
        val accountName = account.displayName
        if (accountName.isNullOrEmpty()) {
            Log.e("LoginPane", "Account name is empty!")
        } else {

            Log.e("LoginPane", "Account name : $accountName")
            Log.e("LoginPane", "Account memes : ${account.account?.name}")
        }

        Log.w("LoginPane", "2")
        val credential = GoogleAccountCredential.usingOAuth2(
            this, listOf("https://www.googleapis.com/auth/gmail.readonly")
        )
        account.account?.let {
            credential.selectedAccount = it
        } ?: Log.e("LoginPane", "Account selection failed; account data is missing.")


        Log.w("LoginPane", "3")
        val transport: HttpTransport = NetHttpTransport()
        val jsonFactory: JsonFactory = getDefaultInstance()
        Log.w("LoginPane", "4")
        return Gmail.Builder(transport, jsonFactory, credential)
            .setApplicationName("MaxInspect")
            .build()
    }

    // Example function to list the emails from the Gmail account
    private fun listEmails(gmailService: Gmail) {
        uiScope.launch {
            try {
                val request = gmailService.users().messages().list("me")
                    .setQ("from:noreply.code.provider@maxima.lt subject:\"Jūsų apsipirkimo MAXIMOJE kvitas\"")
                Log.w("LoginPane", "USER ID")
                Log.w("LoginPane", request.userId)
                val response = withContext(Dispatchers.IO) { request.execute() }
                for (message in response.messages) {
                    // Process each email message
                    val email: Message = withContext(Dispatchers.IO) {
                        gmailService.users().messages().get("me", message.id).execute()
                    }

                    var check = parseEmail(email)
                    Log.e("LoginPane", Json.encodeToString(check))
                    //IMPORTANT We got the check now do the magic with Node

                    break;
                }
            } catch (e: Exception) {
                Log.e("LoginPane", "Something went wrong")
                Log.e("LoginPane", e.message.toString())
            }
        }
    }

    fun parseEmail(email:Message) : EmailCheck {
        val b64Content = email.payload.parts[0].body.data .replace('-', '+').replace('_', '/')
        val decodedBytes = Base64.getDecoder().decode(b64Content.toByteArray())
        var content = String(decodedBytes)
        content = content.replace("\r\n", " ").replace(Regex(" {2,}"), " ")
        content = content.slice(content.indexOf("<table") until content.indexOf("</table"))
        content = content.substring(content.lastIndexOf("<pre>"))
// Cashier check (different format)
        val startWords = content.slice(0 until 100).split(" ")
        val hasCashierID = startWords[2] == "Kasininkas"

        val checkID = if (hasCashierID) startWords[1] else startWords[2]
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


        val check = EmailCheck(checkID, finalProds.toTypedArray())
        return check
    }

}