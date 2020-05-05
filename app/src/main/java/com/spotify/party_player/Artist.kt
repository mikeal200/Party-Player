package com.spotify.party_player

import android.text.Editable
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class Artist(name: Editable?, mAccessToken: String?) {

    var artistUri = ""
    var finalUri = ""
    var artistName = name.toString()
    var mAccessToken = mAccessToken

    //get request to return artist uri
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

        val url = "${baseURL}search?q=$artistName&type=artist&market=us"

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
                artistUri = jsonItems.getJSONObject(0).getString("uri")
                println(artistUri)
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute")
            }
        })
        Thread.sleep(1_000)
        finalUri = artistUri.replace("spotify:artist:", "")
        return finalUri
    }
}