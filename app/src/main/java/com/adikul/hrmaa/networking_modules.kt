package com.adikul.hrmaa

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException


fun isNetworkAvailable(context: Context): Boolean {
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

fun receiveForTime(context: Context, buffRead: BufferedReader, fileBuffWriter: BufferedWriter) : Int{
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

fun getCommandJSON( cmd : String, time : Int, header: String, footer: String) : JSONObject {
    var m : MutableMap< Any?, Any?> = mutableMapOf( Pair("cmd", cmd), Pair("time",time), Pair("header",header), Pair("footer",footer))
    return JSONObject( m)
}