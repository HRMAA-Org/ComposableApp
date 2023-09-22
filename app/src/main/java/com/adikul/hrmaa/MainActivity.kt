package com.adikul.hrmaa

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.lifecycleScope
import com.adikul.hrmaa.ui.theme.HRMAATheme
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val googleAuthClient by lazy {
        GoogleAuthClient(
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HRMAATheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    if (googleAuthClient.getSignedInUser() != null) {
                        val intent = Intent(this@MainActivity, HomeActivity::class.java)
                        startActivity(intent)
                    }
                    val launcher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartIntentSenderForResult(),
                        onResult = { result ->
                            if (result.resultCode == RESULT_OK) {
                                lifecycleScope.launch {
                                    val signInResult = googleAuthClient.signInWithIntent(
                                        intent = result.data ?: return@launch
                                    )
                                    val intent = Intent(this@MainActivity, HomeActivity::class.java)
                                    startActivity(intent)
                                }
                            }
                        }
                    )
                    Welcome(
                        onSignInClick = {
                            lifecycleScope.launch {
                                val signInIntentSender = googleAuthClient.signIn()
                                launcher.launch(
                                    IntentSenderRequest.Builder(
                                        signInIntentSender ?: return@launch
                                    ).build()
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Welcome(onSignInClick: () -> Unit) {
    // Screen content
    Column(
        Modifier
            .padding(horizontal = 36.dp, vertical = 120.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.Start
    ) {
        Image(
            painterResource(id = R.mipmap.main_logo),
            "Logo",
            Modifier.size(320.dp)
        )
        Text(
            text = "Welcome to \nHRMAA!",
            fontFamily = FontFamily(Font(R.font.dmsans_bold)),
            fontSize = 36.sp,
            color = Color(0xFF061428)
        )
        Spacer(modifier = Modifier.size(20.dp))
        Button(
            shape = RoundedCornerShape(12.dp),
            onClick = onSignInClick,
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
        ) {
            Image(
                painterResource(id = R.drawable.google_logo),
                contentDescription = "Google Sign in",
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                    .size(size = 16.dp)
            )
            Text(
                text = "Sign in with Google",
                fontFamily = FontFamily(Font(R.font.jost_bold)),
                fontSize = 13.sp,
                color = Color(0xFF061428),
                modifier = Modifier.padding(start = 10.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4_XL, showSystemUi = true)
@Composable
fun DefaultPreview() {
    HRMAATheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Welcome(onSignInClick = {})
        }
    }
}