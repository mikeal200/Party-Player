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

class Song(name: String) {

    lateinit var TRACK_URI: String
    var songName = name
    var mAccessToken: String? = null

    fun getURI(): String {
        println("Attempting to fetch json")

        val url = "https://api.spotify.com/v1/search?q=${songName}&type=track%2Cartist&market=US&limit=1"

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $mAccessToken")
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response): String {
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
                return TRACK_URI
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute")
            }
        })
    }
}