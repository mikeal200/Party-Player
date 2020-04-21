package com.example.party_player

import android.text.Editable
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull

import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class Playlist(seed1: Editable?, seed1Type: String, songLimit: Int, mAccessToken: String?,
               seed2: String = "", seed2Type: String = "",
               seed3: String = "", seed3Type: String = "",
               seed4: String = "", seed4Type: String = "",
               seed5: String = "", seed5Type: String = ""
               ) {

    var endpoint_url = "https://api.spotify.com/v1/recommendations?"
    val market="US"

    var limit = songLimit
    var mAccessToken = mAccessToken
    var genre = seed1

    var userUri: String? = null

    var client = OkHttpClient()
    //var request = OkHttpRequest(client)

    fun generateRecommendation(): MutableList<String>{
        val root: MutableList<String> = ArrayList()
        val url = "$endpoint_url&limit=$limit&market=$market&seed_genres=$genre"

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $mAccessToken")
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                for(x in 0 until limit - 1) {
                    root.add(
                        x,
                        JSONObject(body).getJSONArray("tracks").getJSONObject(x).get("uri").toString().replace(
                            "spotify:track:",
                            ""
                        )
                    )
                }
            }
            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute")
            }
        })
        return root
    }

    fun populateList() {
        createPlaylist()
    }

    fun createPlaylist() {
        //getUserUri()
        val json = "{\n" +
                "  \"name\": \"New Playlist\",\n" +
                "  \"description\": \"New playlist description\",\n" +
                "  \"public\": false\n" +
                "}"
        val JSON = "application/json;charset=utf-8".toMediaTypeOrNull()
        var body = RequestBody.create(JSON, json)
        val userUri = getUserUri()
        var mCall: Call? = null

        body = FormBody.Builder()
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

    fun getUserUri(): Boolean{
        val url = "https://api.spotify.com/v1/me"

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $mAccessToken")
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                userUri = JSONObject(body).get("id").toString()
                println(userUri)
            }
            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute")
            }
        })
        return true
    }
}


