package com.adikul.hrmaa

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.adikul.hrmaa.ui.theme.HRMAATheme
import com.google.android.gms.auth.api.identity.Identity

class HomeActivity : ComponentActivity() {
    private val googleAuthClient by lazy {
        GoogleAuthClient(
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var name = googleAuthClient.getSignedInUser()?.username
            name = name?.substring(0, name.indexOf(' '))
            Home(name = name ?: "User")
        }
    }
}

@Composable
fun prevSeshCard(date: String, min: Int){
    Card(
        backgroundColor = Color.White,
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clip(shape = RoundedCornerShape(12.dp))
            .fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(all = 16.dp), horizontalArrangement = Arrangement.SpaceBetween){
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Image(
                    painterResource(id = R.drawable.calendar),
                    contentDescription = "Calendar",
                    modifier = Modifier
                        .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                        .size(width = 14.dp, height = 16.dp)
                )
                Column() {
                    Text(
                        text = date,
                        fontFamily = FontFamily(Font(R.font.dmsans_bold)),
                        fontSize = 14.sp,
                        color = Color(0xFF848484)
                    )
                    Text(
                        text = "$min min",
                        fontFamily = FontFamily(Font(R.font.dmsans_bold)),
                        fontSize = 20.sp,
                        color = Color(0xFF848484)
                    )
                }
            }
            Image(
                painterResource(id = R.drawable.sesh_right),
                contentDescription = "Goto session",
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                    .size(size = 24.dp)
            )
        }
    }
}

@Composable
fun Home(name: String) {
    val context = LocalContext.current
    ConstraintLayout(
        modifier = Modifier
            .padding(vertical = 64.dp, horizontal = 36.dp)
            .fillMaxHeight()
    ) {
        val (hello, recCard, prev, button) = createRefs()
        Column(modifier = Modifier.constrainAs(hello) {
            top.linkTo(parent.top)
        }) {
            Text(
                text = "Hello,",
                fontFamily = FontFamily(Font(R.font.dmsans_bold)),
                fontSize = 30.sp,
                color = Color(0xFF061428)
            )
            Text(
                text = "$name!",
                fontFamily = FontFamily(Font(R.font.dmsans_bold)),
                fontSize = 36.sp,
                color = Color(0xFF061428)
            )
        }
        Card(
            backgroundColor = Color.White,
            modifier = Modifier
                .clip(shape = RoundedCornerShape(12.dp))
                .fillMaxWidth()
                .constrainAs(recCard) {
                    top.linkTo(hello.bottom, margin = 48.dp)
                }
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .clip(shape = RoundedCornerShape(12.dp))
                        .size(54.dp)
                        .background(color = Color(0xFF98BBF7)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "89%",
                        fontFamily = FontFamily(Font(R.font.dmsans_bold)),
                        fontSize = 16.sp,
                        color = Color.White,
                        modifier = Modifier
                            .background(color = Color(0xFF98BBF7))
                    )
                }
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.padding(start = 12.dp)
                ) {
                    Text(
                        text = "Recovery Rate",
                        fontFamily = FontFamily(Font(R.font.dmsans_bold)),
                        fontSize = 16.sp,
                        color = Color(0xFF3D4966)
                    )
                    Text(
                        text = "Based on previous sessions.",
                        fontFamily = FontFamily(Font(R.font.dmsans_bold)),
                        fontSize = 12.sp,
                        color = Color(0xFF3D4966)
                    )
                }
            }
        }
        Column(verticalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.constrainAs(prev){
            top.linkTo(recCard.bottom, margin = 48.dp)
        }) {
            Text(
                text = "Previous Sessions",
                fontFamily = FontFamily(Font(R.font.dmsans_bold)),
                fontSize = 18.sp,
                color = Color(0xFF061428)
            )
            prevSeshCard(date = "Tue, 22 Aug 2023", min = 99)
            prevSeshCard(date = "Tue, 22 Aug 2023", min = 110)
            prevSeshCard(date = "Tue, 22 Aug 2023", min = 89)
        }
        Button(
            modifier = Modifier.constrainAs(button) {
                bottom.linkTo(parent.bottom)
                centerHorizontallyTo(parent)
            },
            shape = RoundedCornerShape(12.dp),
            onClick = {
                val intent = Intent(context, RestActivity::class.java)
                context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0F67FE))
        ) {
            Text(
                text = "Start Test",
                fontFamily = FontFamily(Font(R.font.dmsans_bold)),
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier.padding(start = 10.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
            )
            Image(
                painterResource(id = R.drawable.next),
                contentDescription = "Next",
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                    .size(size = 24.dp)
            )
        }
    }
}

//Color(0xFF98BBF7)
@Preview(showBackground = true, device = Devices.PIXEL_4_XL, showSystemUi = true)
@Composable
fun DefaultPreview2() {
    HRMAATheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Home("User")
        }
    }
}