package com.example.party_player


import SpotifyRequests
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import com.spotify.sdk.android.auth.BuildConfig.VERSION_NAME
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {

    private val CLIENT_ID = "3d04daf658674a5c97e487250a7c49ef"
    private val redirectUri = "com.example.party-player://callback"
    val AUTH_TOKEN_REQUEST_CODE = 0x10
    val AUTH_CODE_REQUEST_CODE = 0x11
    lateinit var spotifyAppRemote: SpotifyAppRemote
    lateinit var TRACK_URI: String
    lateinit var DEVICE_ID: String

    val mOkHttpClient: OkHttpClient = OkHttpClient()
    var mAccessToken: String? = null
    private var mAccessCode: String? = null
    var mCall: Call? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupListeners()
        if (onRequestTokenClicked()) {
            //onRequestCodeClicked()
        }
        supportActionBar?.title = String.format(Locale.US, "Spotify Auth Sample %s", VERSION_NAME)
    }

    fun fetchJson() {
        println("Attempting to fetch json")

        val url = "https://api.spotify.com/v1/search?q=herbie%20hancock%2C%20watermelon%20man&type=track%2Cartist&market=US&limit=1"

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $mAccessToken")
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val root = JSONObject(body)
                val ja = root.getJSONObject("tracks").getJSONArray("items")
                ja.getString(0)
                for (i in 0 until ja.length()) {
                    TRACK_URI = ja.getJSONObject(i).getString("uri")
                    //val uri = c.getString("href")
                }
            println(body)

                val gson = GsonBuilder().create()

                val songs = body
                //val search: Tracks? = gson.fromJson(body, Tracks::class.java)
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute")
            }
        })
    }

    override fun onDestroy() {
        cancelCall()
        super.onDestroy()
    }

    fun onGetUserProfileClicked(view: View) {
        if (mAccessToken == null) {
            showRequestAccessTokenSnackbar()
        } else {
            requestUserProfile()
        }
    }

    private fun requestUserProfile() {
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/me")
            .addHeader("Authorization", "Bearer $mAccessToken")
            .build()

        cancelCall()

        mCall = mOkHttpClient.newCall(request)

        mCall?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                setResponse("Failed to fetch data: $e")
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                try {
                    val jsonObject = JSONObject(response.body!!.string())
                    setResponse(jsonObject.toString(3))
                } catch (e: JSONException) {
                    setResponse("Failed to parse data: $e")
                }
            }
        })
    }

    private fun requestDevices() {
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/me/player/devices")
            .addHeader("Authorization", "Bearer $mAccessToken")
            .build()

        cancelCall()

        mCall = mOkHttpClient.newCall(request)

        mCall?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                setResponse("Failed to fetch data: $e")
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                try {
                    val jsonObject = JSONObject(response.body?.string())
                    DEVICE_ID = jsonObject
                        .getJSONArray("devices")
                        .getJSONObject(0)
                        .getString("id")
                } catch (e: JSONException) {
                    setResponse("Failed to parse data: $e")
                }
            }
        })
    }

    private fun showRequestAccessTokenSnackbar() {
        val snackbar = Snackbar.make(findViewById(R.id.activity_main), R.string.warning_need_token, Snackbar.LENGTH_SHORT)
        snackbar.view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
        snackbar.show()
    }

    fun onRequestCodeClicked() {
        val request = getAuthenticationRequest(AuthorizationResponse.Type.CODE)
        AuthorizationClient.openLoginActivity(this, AUTH_CODE_REQUEST_CODE, request)
        //fetchJson()
        requestDevices()
    }

    private fun setupListeners() {

        playButton.setOnClickListener {
            playSong()
        }

        pauseButton.setOnClickListener {
            pauseSong()
        }
    }

    private fun queueSong() {

    }

    fun playSong() {
        val body = FormBody.Builder()
            .build()

        val request = Request.Builder()
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .addHeader("Content-Length", "0")
            .addHeader("Authorization", "Bearer $mAccessToken")
            .url("https://api.spotify.com/v1/me/player/play")
            .put(body)
            .build()

            cancelCall()

        mCall = mOkHttpClient.newCall(request)

        mCall?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                setResponse("Failed to fetch data: $e")
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                try {
                    println("--------------------------------------------------------------------$response")

                } catch (e: JSONException) {
                    setResponse("Failed to parse data: $e")
                }
            }
        })
    }

    private fun pauseSong() {
        val body = FormBody.Builder()
            .build()

        val request = Request.Builder()
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $mAccessToken")
            .url("https://api.spotify.com/v1/me/player/pause")
            .put(body)
            .build()

        cancelCall()

        mCall = mOkHttpClient.newCall(request)

        mCall?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                setResponse("Failed to fetch data: $e")
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                try {
                    println("--------------------------------------------------------------------$response")

                } catch (e: JSONException) {
                    setResponse("Failed to parse data: $e")
                }
            }
        })
    }


    fun onRequestTokenClicked(): Boolean {
        val request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN)
        AuthorizationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, request)
        return true
    }

    private fun getAuthenticationRequest(type: AuthorizationResponse.Type): AuthorizationRequest {
        return AuthorizationRequest.Builder(CLIENT_ID, type, redirectUri.toString())
            .setShowDialog(false)
            .setScopes(arrayOf("user-modify-playback-state"))
            .setCampaign("your-campaign-token")
            .build()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val response = AuthorizationClient.getResponse(resultCode, data)

        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.accessToken
            updateTokenView()
        } else if (AUTH_CODE_REQUEST_CODE == requestCode) {
            mAccessCode = response.code
            updateCodeView()
        }
    }

    fun setResponse(text: String) {
        runOnUiThread {
            findViewById<TextView>(R.id.response_text_view)?.text = text
        }
    }

    private fun updateTokenView() {
        findViewById<TextView>(R.id.token_text_view)?.text = getString(R.string.token, mAccessToken)
    }

    private fun updateCodeView() {
        findViewById<TextView>(R.id.code_text_view)?.text = getString(R.string.code, mAccessCode)
    }

    fun cancelCall() {
        mCall?.cancel()
    }


    private fun getRedirectedUri(): android.net.Uri {
        return Uri.parse(redirectUri)
    }

}
