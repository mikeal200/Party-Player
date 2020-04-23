package com.example.party_player

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.GsonBuilder
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
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
        onRequestTokenClicked()
        supportActionBar?.title = String.format(Locale.US, "Party-Player")
    }





    fun getGenre(genreTextInput: TextInputEditText) {
        // get text out of the TextInputEditText
        var genre = genreTextInput.text
    }



    override fun onDestroy() {
        cancelCall()
        super.onDestroy()
    }


    fun onRequestCodeClicked() {
        val request = getAuthenticationRequest(AuthorizationResponse.Type.CODE)
        AuthorizationClient.openLoginActivity(this, AUTH_CODE_REQUEST_CODE, request)
    }

    private fun setupListeners() {
        searchButton.setOnClickListener {
            val mySpinner = findViewById<View>(R.id.seedOne) as Spinner
            val text = mySpinner.selectedItem.toString()
            var playlist = Playlist(seedOneTB.text, text, 10, mAccessToken)
            playlist.createPlaylist()
        }

        /*searchButton.setOnClickListener {
            //user input song, artist is taken in as a string
            var songArtist = seedOneTB.text
            //the string is then split into a string array [0] = song name [1] = artist
            var songArtistArr = songArtist?.split(",")
            //song = songArtistArr [0] + URL query additions
            var songURL = songArtistArr?.get(0)?.replace(" ", "%20")
            //artist = songArtistArr[1] + URL query additions
            var artistURL = songArtistArr?.get(1)?.replace(" ", "%20")
            //songArtistURL concatenates songUrl and artistURL except inbetween the two there are query params
            var songArtistURL = "$songURL%2C%20$artistURL"

            //gets songs uri from spotify
            //songSearch(songArtistURL)

            var song: Song = Song(songArtistURL, mAccessToken)

            println(song.getURI())

            /*if(::TRACK_URI.isInitialized) {
                //queue the song
                queueSong()
            }*/
        }*/
    }




    fun onRequestTokenClicked(): Boolean {
        val request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN)
        AuthorizationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, request)
        return true
    }

    private fun getAuthenticationRequest(type: AuthorizationResponse.Type): AuthorizationRequest {
        return AuthorizationRequest.Builder(CLIENT_ID, type, redirectUri.toString())
            .setShowDialog(false)
            .setScopes(arrayOf("playlist-modify-public", "playlist-modify-private"))
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
