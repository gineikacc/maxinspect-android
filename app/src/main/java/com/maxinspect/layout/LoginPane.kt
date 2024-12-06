package com.maxinspect.layout

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn.getClient
import com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.maxinspect.Globals
import com.maxinspect.R
import com.maxinspect.R.id.logoutButton
import com.maxinspect.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class LoginPane : ComponentActivity() {
    private val uiScope = CoroutineScope(Dispatchers.Main)
    lateinit var infoText: TextView
//On login button click launch function
    private val signInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // Handle the result of the sign-in activity here
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val task: Task<GoogleSignInAccount> =
                    getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    // Signed in successfully, show authenticated UI
                    updateUI(account)
                    infoText.text = "Prisijungta"
                } catch (e: ApiException) {
                    // Sign in failed, handle exception
                    Log.e("LoginPane", "FAIL >> signInResult:failed code=" + e.statusCode)
                    updateUI(null)
                    infoText.text = "Prisijungimas nepavyko"

                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_pane)

        infoText = findViewById<TextView>(R.id.LoginEmailText)
        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(
                Scope("https://www.googleapis.com/auth/gmail.readonly"),
            )
            .requestProfile()
            .build()


        findViewById<Button>(R.id.OAuthButton).setOnClickListener {
            infoText.text = "Palaukite..."
            val SIClient = getClient(this, gso)
            SIClient.revokeAccess().addOnCompleteListener {
                val SIIntent = getClient(this, gso).signInIntent
                signInLauncher.launch(SIIntent)
            }
        }
        findViewById<Button>(logoutButton).setOnClickListener {
            infoText.text = "Atsijungta"
            Globals.products.clear()
            Globals.receipts.clear()
            Globals.purchases.clear()
            Globals.syncQueue.clear()
            Globals.userID = ""
        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {
            val gmailService = Util.getGmailService(account, this)
            Util.getAllEmailReceipts(gmailService, account.email.toString(), uiScope, this)
        }
    }
}
