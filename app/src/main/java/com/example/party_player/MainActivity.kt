package com.example.party_player


import android.content.Intent
import android.os.Bundle

import android.net.Uri
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import com.spotify.sdk.android.auth.BuildConfig.VERSION_NAME

import java.util.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private val CLIENT_ID = "3d04daf658674a5c97e487250a7c49ef"
    private val redirectUri = "com.example.party-player://callback"
    val AUTH_TOKEN_REQUEST_CODE = 0x10
    val AUTH_CODE_REQUEST_CODE = 0x11
    lateinit var spotifyAppRemote: SpotifyAppRemote

    private val mOkHttpClient: OkHttpClient = OkHttpClient()
    private var mAccessToken: String? = null
    private var mAccessCode: String? = null
    private var mCall: Call? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.title = String.format(Locale.US, "Spotify Auth Sample %s", VERSION_NAME)

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

    private fun showRequestAccessTokenSnackbar() {
        val snackbar = Snackbar.make(findViewById(R.id.activity_main), R.string.warning_need_token, Snackbar.LENGTH_SHORT)
        snackbar.view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
        snackbar.show()
    }

    fun onRequestCodeClicked(view: View) {
        val request = getAuthenticationRequest(AuthorizationResponse.Type.CODE)
        AuthorizationClient.openLoginActivity(this, AUTH_CODE_REQUEST_CODE, request)
    }

    fun onRequestTokenClicked(view: View) {
        val request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN)
        AuthorizationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, request)
    }

    private fun getAuthenticationRequest(type: AuthorizationResponse.Type): AuthorizationRequest {
        return AuthorizationRequest.Builder(CLIENT_ID, type, redirectUri.toString())
            .setShowDialog(false)
            .setScopes(arrayOf("user-read-email"))
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

    private fun setResponse(text: String) {
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

    private fun cancelCall() {
        mCall?.cancel()
    }


    private fun getRedirectedUri(): android.net.Uri {
        return Uri.parse(redirectUri)
    }

}