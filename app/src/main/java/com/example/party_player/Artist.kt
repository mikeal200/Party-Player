package com.example.party_player

import android.text.Editable
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class Artist(name: Editable?, mAccessToken: String?) {

    var ARTIST_URI: String = ""
    var artistName = name.toString()
    var mAccessToken = mAccessToken

    fun getURI(): String {
        //the string is then split into a string array
        var songArtistArr = artistName?.split(" ")
        if (songArtistArr != null) {
            for(item in songArtistArr.indices) {
                if(songArtistArr.size > 1) {
                    artistName = songArtistArr.get(item).replace(" ", "%20")
                }
                else {
                    artistName = songArtistArr.get(item)
                }
            }
        }

        val url = "https://api.spotify.com/v1/search?q=$artistName&type=artist&market=us"

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $mAccessToken")
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val root = JSONObject(body)

                val jsonItems = root.getJSONObject("artists").getJSONArray("items")
                ARTIST_URI = jsonItems.getJSONObject(0).getString("uri")
                println(ARTIST_URI)
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute")
            }
        })
        return ARTIST_URI
    }
}