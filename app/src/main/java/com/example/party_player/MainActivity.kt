package com.example.party_player

import android.content.Intent

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.util.*

const val baseURL = "https://api.spotify.com/v1/"

class MainActivity : AppCompatActivity() {

    private val CLIENT_ID = "3d04daf658674a5c97e487250a7c49ef"
    private val redirectUri = "com.example.party-player://callback"
    val AUTH_TOKEN_REQUEST_CODE = 0x10
    val AUTH_CODE_REQUEST_CODE = 0x11

    var mAccessToken: String? = null
    private var mAccessCode: String? = null

    //automatically runs on start
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupListeners()
        onRequestTokenClicked()
        supportActionBar?.title = String.format(Locale.US, "Party-Player")
    }

    //sets up listener for button and gets spinner info and textbox text, then displays a message when the playlist is created
    private fun setupListeners() {
        searchButton.setOnClickListener {
            val mySpinner = findViewById<View>(R.id.seedOne) as Spinner
            val text = mySpinner.selectedItem.toString()
            var playlist = Playlist(seedOneTB.text, text, 50, mAccessToken)
            playlist.populateList()
            val myToast = Toast.makeText(applicationContext,"Playlist Created! Check Spotify.",Toast.LENGTH_SHORT)
            myToast.setGravity(Gravity.CENTER, 0, 0)
            myToast.show()
        }
    }

    //gets auth token
    fun onRequestTokenClicked(): Boolean {
        val request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN)
        AuthorizationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, request)
        return true
    }

    //sets scopes and makes auth request
    private fun getAuthenticationRequest(type: AuthorizationResponse.Type): AuthorizationRequest {
        return AuthorizationRequest.Builder(CLIENT_ID, type, redirectUri.toString())
            .setShowDialog(false)
            .setScopes(arrayOf("playlist-modify-public", "playlist-modify-private","user-read-email","user-read-private"))
            .setCampaign("your-campaign-token")
            .build()
    }

    //if request is completed access token and code are defined
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val response = AuthorizationClient.getResponse(resultCode, data)

        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.accessToken
        } else if (AUTH_CODE_REQUEST_CODE == requestCode) {
            mAccessCode = response.code
        }
    }
}
