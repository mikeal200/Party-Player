package com.example.party_player

import SpotifyRequests
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
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

class Playlist(seed1: String, seed1Type: String, songLimit: Int) {

    var endpoint_url = "https://api.spotify.com/v1/recommendations?"
    val market="US"

    var limit: Int
    var genre: String
    var mAccessToken: String? = null

    init {
        limit = songLimit;
        genre = seed1Type;
    }

    fun generateRecommendation(seed1: String) {
        val url = "${endpoint_url}limit=${limit}&market=${market}&seed_genres=${genre}'"

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $mAccessToken")
            .build()

        val client = OkHttpClient()

    }

    fun create() {

    }
}