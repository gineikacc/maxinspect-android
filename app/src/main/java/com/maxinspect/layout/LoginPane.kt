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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

        Log.w("LoginPane", "5")
        val request = gmailService.users().messages().list("me")
            .setQ("from:noreply.code.provider@maxima.lt subject:\"Jūsų apsipirkimo MAXIMOJE kvitas\"")
        Log.w("LoginPane", request.userId)
        Log.w("LoginPane", "aa")
        val response = withContext(Dispatchers.IO) { request.execute() }
        Log.w("LoginPane", "6")

        for (message in response.messages) {
            // Process each email message
            val email: Message = withContext(Dispatchers.IO) {
                gmailService.users().messages().get("me", message.id).execute()
            }

            Log.d("LoginPane", "Subject: ${email.payload.headers.find { it.name == "Subject" }?.value}")
            Log.d("LoginPane", "aa")
            break;
        }
        Log.w("LoginPane", "7")
        } catch (e: Exception) {
            Log.e("LoginPane", "BAD MEMES")
            Log.e("LoginPane", e.message.toString())
        }
        }
    }

    fun parseEmail(email:Message) {
        val b64Content = email.payload.parts[0].body.data
        var content = String(Base64.getDecoder().decode(b64Content))
        content = content
            .replace("\r\n", " ")
            .replace(Regex(" {2,}"), " ")

        val tableStartIndex = content.indexOf("<table")
        val tableEndIndex = content.indexOf("</table>")
        content = content.slice(tableStartIndex..tableEndIndex)
        val preTagIndex = content.lastIndexOf("<pre>")
        content = content.slice(preTagIndex until content.length)
        val startWords = content.slice(0..100).split(" ")
        val hasCashierID = startWords[2] == "Kasininkas"
        var checkID = ""
        if (hasCashierID) {
            checkID = startWords[1]
        } else {
            checkID = startWords[2]
        }








    }

}