package com.adikul.hrmaa

import android.content.Context
import android.graphics.Paint.Align
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.core.content.getSystemService
//import androidx.core.content.ContextCompat.getSystemService
import com.adikul.hrmaa.ui.theme.HRMAATheme
import kotlinx.coroutines.delay
import java.security.Permissions
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.Button
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.*
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RunActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HRMAATheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Run()
                }
            }
        }
    }
}

private val EOF = "</HRMAA>"

private var dist = 0.02
@Composable
fun Run() {
    var timeLeft by remember { mutableStateOf(0.000) }
    var isPaused by remember { mutableStateOf(false) }
    var py : Python;
    var module : PyObject;
    val coroutineScope = rememberCoroutineScope()

    // TODO: Pass client Socket from previous activity
    var clientSocket :Socket
    val context : Context = LocalContext.current

    val folder =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val fileName1 = "HR_${
        SimpleDateFormat(" yyyy-MM-dd - HH_mm_ss runing", Locale.US)
            .format(System.currentTimeMillis())
    }.txt"
    val file1 = File(folder, fileName1)
    val fileBuffWrit =  FileOutputStream(file1, true).bufferedWriter()

    //Yeh blocking he kya?  for the rest of the code?
    LaunchedEffect(key1 = timeLeft, key2 = isPaused) {

        coroutineScope.launch(Dispatchers.Default) {
            py = Python.getInstance()
//            module = py.getModule("heartpy_script")
        }
    }
    Button(
        content = null,
        onClick = {
            coroutineScope.launch(Dispatchers.Default) {
                py = Python.getInstance()
//            module = py.getModule("heartpy_script")
            }
        }
    )


    val i_wont_change = 0;
    LaunchedEffect( i_wont_change) {

        coroutineScope.launch(Dispatchers.Default) {
            while (timeLeft < 60 && !isPaused) {
                delay(1L)
                timeLeft+=0.001
            }
        }

        try{
            //this automatically binds to the server, no need to send separate connect request.
            /*
            IMP all write and read calls in TCP sockets are blocking
            see: https://stackoverflow.com/questions/10574596/is-an-outputstream-in-java-blocking-sockets
             */
            val buffRead = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
            val buffWrit = BufferedWriter(OutputStreamWriter(clientSocket.getOutputStream()))

            val jsonObj = getCommandJSON("send_for_time", 12, "One off", Date().time.toString())
            Log.d("JSON string",jsonObj.toString() )
            buffWrit.write(jsonObj.toString())
            /*
            Must flush
            While you are trying to write data to a Stream using the BufferedWriter object, after invoking the write() method the data will be buffered initially, nothing will be printed. The flush() method is used to push the contents of the buffer to the underlying Stream.
             */
            buffWrit.flush()

            receiveForTime( context ,buffRead, fileBuffWrit, 12 ,"One off", "All in smoke" )

            buffWrit.write(getCommandJSON("stop", 12, "One off", Date().time.toString()).toString())
            buffWrit.flush()

            buffRead.close()
            buffWrit.close()
            //Don't close client socket, pass it on instead
            fileBuffWrit.close()
            Log.d("readSuccessful","Read success")
        }
        catch (e : IOException){

            Log.e("Error", e.toString());
        }

    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxHeight()
    ) {
        val (bg, shiny, RunText, distCard) = createRefs()
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
            text = "Run!",
            fontFamily = FontFamily(Font(R.font.dmsans_bold)),
            fontSize = 20.sp,
            color = Color(0xFF3D4966),
            modifier = Modifier
                .padding(all = 16.dp)
                .constrainAs(RunText) {
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
fun RunPreview() {
    HRMAATheme {
        Run()
    }
}

private fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

private val PERMISSIONS_ABOVE_Q = arrayOf(
    android.Manifest.permission.INTERNET,
    android.Manifest.permission.ACCESS_NETWORK_STATE,
    // Add other permissions as needed for Android 10 or higher
)

private val PERMISSIONS_BELOW_Q =
    arrayOf(
        android.Manifest.permission.INTERNET,
        android.Manifest.permission.ACCESS_NETWORK_STATE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,

        )

//private val requestPermissionLauncher =
//    registerForActivityResult(
//        ActivityResultContracts.RequestMultiplePermissions()
//    )
//    {
//        Log.d("Permissions", it.toString())
//        for( perm in PERMISSIONS_ABOVE_Q){
//            if( it[perm] == false){
//                Toast.makeText(this,"Please provide required permissions", Toast.LENGTH_SHORT).show()
//                finish()
//            }
//        }
//        Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
//    }

private fun receiveForTime(context: Context, buffRead: BufferedReader, fileBuffWriter: BufferedWriter, time_s : Int, header : String, footer : String) : Int{
    var fileDat : String = ""
    var currStr : String? = ""

    try{
        while ( true) {

            if( currStr == EOF ){
                break
            }
            Log.d("CurrStr", currStr.toString())
            fileDat = currStr + "\n"
            fileBuffWriter.write(fileDat)
            fileBuffWriter.flush()
            /*
            Null is there when no string is received before any of the termination condition.
            The readLine() method is designed to block until one of the following conditions is met:
            It reads a line of text (terminated by a newline character \n or carriage return/line feed \r\n).
            It reaches the end of the stream (the sender closes the connection or sends an EOF).
            It encounters an exception, such as an IOException.
             */
            currStr = buffRead.readLine()
        }
        return 0
    }
    catch ( e: IOException){
        Log.e("BuffRead error", e.message.toString())
        context.run {
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
        }

        return -1
    }
}

private fun getCommandJSON( cmd : String, time : Int, header: String, footer: String) : JSONObject{
    var m : MutableMap< Any?, Any?> = mutableMapOf( Pair("cmd", cmd), Pair("time",time), Pair("header",header), Pair("footer",footer))
    return JSONObject( m)
}