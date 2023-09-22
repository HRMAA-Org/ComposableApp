package com.adikul.hrmaa

import android.graphics.Paint.Align
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.adikul.hrmaa.ui.theme.HRMAATheme

class RestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HRMAATheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Rest()
                }
            }
        }
    }
}
private var dist = 0.02
@Composable
fun Rest() {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxHeight()
    ) {
        val (bg, shiny, restText, distCard) = createRefs()
        Box(
            modifier = Modifier.constrainAs(bg) {
                top.linkTo(parent.top)
            }
        ) {
            Image(
                painterResource(id = R.mipmap.bg1),
                "Logo",
                Modifier
                    .size(400.dp)
                    .offset(x = (-40).dp)
            )
        }
        Image(
            painterResource(id = R.mipmap.shiny_rest),
            "Logo",
            Modifier
                .padding(top = 80.dp)
                .size(height = 200.dp, width = 300.dp)
                .constrainAs(shiny) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    centerHorizontallyTo(parent)
                }
        )
        Text(
            text = "Rest!",
            fontFamily = FontFamily(Font(R.font.dmsans_bold)),
            fontSize = 20.sp,
            color = Color(0xFF3D4966),
            modifier = Modifier
                .padding(all = 16.dp)
                .constrainAs(restText) {
                    top.linkTo(shiny.bottom)
                    centerHorizontallyTo(parent)
                }
        )
        Column(
            modifier = Modifier.fillMaxWidth()
                .constrainAs(distCard) {
                    top.linkTo(bg.bottom)
                    centerHorizontallyTo(parent)
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Card(
                    backgroundColor = Color(0xFFF2F5F9),
                    modifier = Modifier
                        .padding(all = 16.dp)
                        .clip(shape = RoundedCornerShape(12.dp))
                ) {
                    Column {
                        Row(
                            modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
                        ) {
                            Text(
                                text = "Distance",
                                fontFamily = FontFamily(Font(R.font.dmsans_bold)),
                                fontSize = 16.sp,
                                color = Color(0xFF3D4966),
                                modifier = Modifier.padding(all = 8.dp)
                            )
                            Image(
                                painterResource(id = R.drawable.dist),
                                contentDescription = "Distance",
                                modifier = Modifier
                                    .padding(all = 8.dp)
                                    .size(width = 14.dp, height = 16.dp)
                            )
                        }
                        Row(
                            modifier = Modifier.padding(bottom = 8.dp, start = 8.dp, end = 8.dp),
                            verticalAlignment = Alignment.Bottom
                        ){
                            Text(
                                text = dist.toString(),
                                fontFamily = FontFamily(Font(R.font.dmsans_bold)),
                                fontSize = 20.sp,
                                color = Color(0xFF000000),
                                modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)
                            )
                            Text(
                                text = "km",
                                fontFamily = FontFamily(Font(R.font.dmsans_bold)),
                                fontSize = 14.sp,
                                color = Color(0xFF848484),
                                modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)
                            )
                        }

                    }
                }
                Card(
                    backgroundColor = Color(0xFFF2F5F9),
                    modifier = Modifier
                        .padding(all = 16.dp)
                        .clip(shape = RoundedCornerShape(12.dp))
                ) {
                    Column {
                        Row(
                            modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)
                        ) {
                            Text(
                                text = "Calories",
                                fontFamily = FontFamily(Font(R.font.dmsans_bold)),
                                fontSize = 16.sp,
                                color = Color(0xFF3D4966),
                                modifier = Modifier.padding(all = 8.dp)
                            )
                            Image(
                                painterResource(id = R.drawable.cals),
                                contentDescription = "Calories",
                                modifier = Modifier
                                    .padding(all = 8.dp)
                                    .size(width = 14.dp, height = 16.dp)
                            )
                        }
                        Row(
                            modifier = Modifier.padding(all = 8.dp),
                            verticalAlignment = Alignment.Bottom
                        ){
                            Text(
                                text = dist.toString(),
                                fontFamily = FontFamily(Font(R.font.dmsans_bold)),
                                fontSize = 20.sp,
                                color = Color(0xFF000000),
                                modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)
                            )
                            Text(
                                text = "kcal",
                                fontFamily = FontFamily(Font(R.font.dmsans_bold)),
                                fontSize = 14.sp,
                                color = Color(0xFF848484),
                                modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)
                            )
                        }

                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Card(
                    backgroundColor = Color(0xFFF2F5F9),
                    modifier = Modifier
                        .padding(all = 16.dp)
                        .clip(shape = RoundedCornerShape(12.dp))
                ) {
                    Column {
                        Row(
                            modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
                        ) {
                            Text(
                                text = "Duration",
                                fontFamily = FontFamily(Font(R.font.dmsans_bold)),
                                fontSize = 16.sp,
                                color = Color(0xFF3D4966),
                                modifier = Modifier.padding(all = 8.dp)
                            )
                            Image(
                                painterResource(id = R.drawable.clock),
                                contentDescription = "Distance",
                                modifier = Modifier
                                    .padding(all = 8.dp)
                                    .size(width = 14.dp, height = 16.dp)
                            )
                        }
                        Row(
                            modifier = Modifier.padding(bottom = 8.dp, start = 8.dp, end = 8.dp),
                            verticalAlignment = Alignment.Bottom
                        ){
                            Text(
                                text = dist.toString(),
                                fontFamily = FontFamily(Font(R.font.dmsans_bold)),
                                fontSize = 20.sp,
                                color = Color(0xFF000000),
                                modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)
                            )
                        }

                    }
                }
                Card(
                    backgroundColor = Color(0xFFF2F5F9),
                    modifier = Modifier
                        .padding(all = 16.dp)
                        .clip(shape = RoundedCornerShape(12.dp))
                ) {
                    Column {
                        Row(
                            modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)
                        ) {
                            Text(
                                text = "Heart Rate",
                                fontFamily = FontFamily(Font(R.font.dmsans_bold)),
                                fontSize = 16.sp,
                                color = Color(0xFF3D4966),
                                modifier = Modifier.padding(all = 8.dp)
                            )
                            Image(
                                painterResource(id = R.drawable.heart),
                                contentDescription = "Distance",
                                modifier = Modifier
                                    .padding(all = 8.dp)
                                    .size(width = 14.dp, height = 16.dp)
                            )
                        }
                        Row(
                            modifier = Modifier.padding(all = 8.dp),
                            verticalAlignment = Alignment.Bottom
                        ){
                            Text(
                                text = dist.toString(),
                                fontFamily = FontFamily(Font(R.font.dmsans_bold)),
                                fontSize = 20.sp,
                                color = Color(0xFF000000),
                                modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)
                            )
                            Text(
                                text = "bpm",
                                fontFamily = FontFamily(Font(R.font.dmsans_bold)),
                                fontSize = 14.sp,
                                color = Color(0xFF848484),
                                modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)
                            )
                        }

                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4_XL, showSystemUi = true)
@Composable
fun GreetingPreview() {
    HRMAATheme {
        Rest()
    }
}