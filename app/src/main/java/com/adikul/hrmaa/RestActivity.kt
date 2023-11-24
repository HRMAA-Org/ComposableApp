package com.adikul.hrmaa

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Paint.Align
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.google.type.DateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.math.RoundingMode
import java.net.Socket
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date
import java.util.Locale

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
val startTime = System.currentTimeMillis()/1000.0
@Composable
fun Rest() {

    val activity = (LocalContext.current as? Activity)
    val time = 60
    val ip_address = "10.70.44.140"
    val context : Context = LocalContext.current
    var timeLeft by remember { mutableStateOf(0.000) }
    var isPaused by remember { mutableStateOf(false) }
    var heartRate by remember { mutableStateOf(0) }
//    var py : Python;
//    var module : PyObject;
    val coroutineScope = rememberCoroutineScope()

    val folder =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val fileName1 = "HR_${
        SimpleDateFormat(" yyyy-MM-dd - HH_mm_ss", Locale.US)
            .format(System.currentTimeMillis())
    } rest ${SessionSharedPref.getSessionNum(context)}.txt"
    val file = File(folder, fileName1 )
    val fileBuffWrit =  FileOutputStream(file, true).bufferedWriter()

    LaunchedEffect( Unit ){
        coroutineScope.launch(Dispatchers.IO){
            var clientSocket : Socket = Socket(ip_address, 80) //10.70.100.133:80

            if(isNetworkAvailable(context)) {
                try {
                    //this automatically binds to the server, no need to send separate connect request.
                    /*
                IMP all write and read calls in TCP sockets are blocking
                see: https://stackoverflow.com/questions/10574596/is-an-outputstream-in-java-blocking-sockets
                 */
                    val buffRead = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
                    val buffWrit =
                        BufferedWriter(OutputStreamWriter(clientSocket.getOutputStream()))

                    val jsonObj =
                        getCommandJSON("send_for_time", time, "One off", Date().time.toString())
                    Log.d("JSON string", jsonObj.toString())
                    buffWrit.write(jsonObj.toString())
                    /*
                    Must flush
                    While you are trying to write data to a Stream using the BufferedWriter object, after invoking the write() method the data will be buffered initially, nothing will be printed. The flush() method is used to push the contents of the buffer to the underlying Stream.
                     */
                    buffWrit.flush()

                    receiveForTime(context, buffRead, fileBuffWrit )

                    buffWrit.write(
                        getCommandJSON(
                            "stop",
                            12,
                            "One off",
                            Date().time.toString()
                        ).toString()
                    )
                    buffWrit.flush()

                    buffRead.close()
                    buffWrit.close()
                    //Don't close client socket, pass it on instead
                    fileBuffWrit.close()
                    Log.d("readSuccessful", "Read success")
                    SessionSharedPref.setSessionNum(context, SessionSharedPref.getSessionNum(context)+1 )
                    activity?.finish()

                } catch (e: IOException) {

                    Log.e("Error", e.toString());
                }
            }
        }
    }

    LaunchedEffect(key1 = timeLeft, key2 = isPaused) {

        coroutineScope.launch(Dispatchers.Default) {
            while (timeLeft < time  && !isPaused) {
                delay(1L)
                timeLeft+=0.001
            }
        }
    }

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
                                text = timeLeft.toString(),
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
fun RestPreview() {
    HRMAATheme {
        Rest()
    }
}