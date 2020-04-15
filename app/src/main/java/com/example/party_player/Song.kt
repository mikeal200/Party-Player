package com.example.party_player

import com.google.gson.GsonBuilder
import okhttp3.*
import org.json.JSONObject
import java.io.IOException


class Song(name: String, mAccessToken: String?) {

    var TRACK_URI: String = ""
    var songName = name
    var mAccessToken = mAccessToken

    fun getURI(): String {

        val url = "https://api.spotify.com/v1/search?q=${songName}&type=track%2Cartist&market=US&limit=1"

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
        return TRACK_URI
    }
}