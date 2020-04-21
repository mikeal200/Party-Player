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

<<<<<<< Updated upstream
    var limit: Int
    var genre: String
    var mAccessToken: String? = null

    init {
        limit = songLimit;
        genre = seed1Type;
    }
=======
    var limit = songLimit
    var mAccessToken = mAccessToken
    var genre = seed1

    var client = OkHttpClient()
    //var request = OkHttpRequest(client)
>>>>>>> Stashed changes

    fun generateRecommendation(seed1: String) {
        val url = "${endpoint_url}limit=${limit}&market=${market}&seed_genres=${genre}'"

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $mAccessToken")
            .build()

        val client = OkHttpClient()

    }

<<<<<<< Updated upstream
    fun create() {

=======
    fun createPlaylist() {
        //getUserUri()
        val json = "{\n" +
                "  \"name\": \"New Playlist\",\n" +
                "  \"description\": \"New playlist description\",\n" +
                "  \"public\": false\n" +
                "}"
        val JSON = "application/json;charset=utf-8".toMediaTypeOrNull()
        val userUri = getUserUri()
        var mCall: Call? = null

        var body = FormBody.Builder()
            .build()

        val request = Request.Builder()
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .addHeader("Content-Length", "0")
            .addHeader("Authorization", "Bearer $mAccessToken")
            .url("https://api.spotify.com/v1/users/$userUri/playlists")
            .post(body)
            .build()

        val call = client.newCall(request)

        call.enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                println(request)
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute")
            }


        })
    }

    fun getUserUri(): String{
        val url = "https://api.spotify.com/v1/me"

        var userUri: String = ""

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $mAccessToken")
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                userUri = JSONObject(body).get("id").toString()
            }
            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute")
            }
        })
        return userUri
>>>>>>> Stashed changes
    }
}