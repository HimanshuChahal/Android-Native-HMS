package com.hms

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import live.hms.video.error.HMSException
import live.hms.video.media.tracks.HMSTrack
import live.hms.video.sdk.HMSSDK
import live.hms.video.sdk.HMSUpdateListener
import live.hms.video.sdk.models.*
import live.hms.video.sdk.models.enums.HMSPeerUpdate
import live.hms.video.sdk.models.enums.HMSRoomUpdate
import live.hms.video.sdk.models.enums.HMSTrackUpdate
import live.hms.video.sdk.models.trackchangerequest.HMSChangeTrackStateRequest
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

class BaseActivity : AppCompatActivity(), APICallback {

    lateinit var hmsSDK: HMSSDK
    lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        val leaveBtn = findViewById<Button>(R.id.leaveBtn)

        hmsSDK = HMSSDK.Builder(application).build()

        leaveBtn.setOnClickListener {
            Log.i("Meeting", "Leave")
            hmsSDK.leave()
        }

        checkPermissions()

    }

    fun checkPermissions() {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO), 0)
            }
        } else
        {
            getToken()
        }

    }

    fun joinRoom(config : HMSConfig, hmsUpdateListener : HMSUpdateListener) {

        hmsSDK.join(config, hmsUpdateListener)

    }

    private fun getToken() {

        val apiCall = APICall(this, this)
        apiCall.execute("https://prod-in.100ms.live/hmsapi/hyperfit.app.100ms.live/api/token")

    }

    override fun onDataReceived(response: Any) {
        token = (response as JSONObject)["token"] as String

        val hmsConfig = HMSConfig("Android user", token)

        val hmsUpdateListener = HMSListener()

        joinRoom(hmsConfig, hmsUpdateListener)
    }

    class APICall(context: Context, apiCallback: APICallback): AsyncTask<String, Void, String?>() {

        var context: Context = context
        var apiCallback: APICallback = apiCallback

        override fun doInBackground(vararg p0: String?): String? {

            var params = HashMap<String, String>()
            params["user_id"] = UUID.randomUUID().toString()
            params["room_id"] = "61e6999ca8fdf1a00ecc9ab3"
            params["role"] = "trainer"

            val obj = JSONObject(params as Map<*, *>)

            val request = JsonObjectRequest(Request.Method.POST, p0[0], obj, { response ->
                Log.i("Token Response", response.toString())
                apiCallback.onDataReceived(response)

            }, {
                Log.i("Error", it.message.toString())
            })

            request.retryPolicy = DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

            Volley.newRequestQueue(context).add(request)

            return null
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) &&
                (grantResults.size > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) &&
                (grantResults.size > 2 && grantResults[2] == PackageManager.PERMISSION_GRANTED)) {
            getToken()
        } else
        {
            Toast.makeText(this, "Request not granted", Toast.LENGTH_SHORT).show()
        }

//        when(requestCode)
//        {
//            0 -> {
//                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                    getToken()
//                } else {
//                    Toast.makeText(this, "Request not granted", Toast.LENGTH_SHORT).show()
//                }
//            }
//            1 -> {
//                if ((grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
//                    getToken()
//                } else {
//                    Toast.makeText(this, "Request not granted", Toast.LENGTH_SHORT).show()
//                }
//            }
//            2 -> {
//                if ((grantResults.isNotEmpty() && grantResults[2] == PackageManager.PERMISSION_GRANTED)) {
//                    getToken()
//                } else {
//                    Toast.makeText(this, "Request not granted", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }

    }

    class HMSListener: HMSUpdateListener {
        override fun onChangeTrackStateRequest(details: HMSChangeTrackStateRequest) {

        }

        override fun onError(error: HMSException) {

        }

        override fun onJoin(room: HMSRoom) {

            Log.i("onJoin", room.localPeer.toString())

        }

        override fun onMessageReceived(message: HMSMessage) {

        }

        override fun onPeerUpdate(type: HMSPeerUpdate, peer: HMSPeer) {

        }

        override fun onRoleChangeRequest(request: HMSRoleChangeRequest) {

        }

        override fun onRoomUpdate(type: HMSRoomUpdate, hmsRoom: HMSRoom) {

        }

        override fun onTrackUpdate(type: HMSTrackUpdate, track: HMSTrack, peer: HMSPeer) {

        }

    }
}