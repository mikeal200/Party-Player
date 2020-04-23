package com.example.party_player

import android.text.Editable
import android.view.Gravity
import android.widget.Toast
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType

import org.json.JSONObject
import java.io.IOException

class Playlist(seed1: Editable?, seed1Type: String, songLimit: Int, mAccessToken: String?) {

    var endpoint_url = "https://api.spotify.com/v1/recommendations?"
    val market="US"

    var limit = songLimit
    var mAccessToken = mAccessToken
    var seedType = seed1Type
    val seed = seed1

    fun generateRecommendation(): MutableList<String>{
        val root: MutableList<String> = ArrayList()
        var url = "$endpoint_url&limit=$limit&market=$market&"
        var finalUrl = ""


        if(seedType == "Genre") {
            finalUrl = url + "seed_genres=${seed.toString()}"
        }
        else if(seedType == "Artist") {
            val artistSeed = Artist(seed, mAccessToken).getURI()
            finalUrl = url + "seed_artists=$artistSeed"
        }
        else if(seedType == "Song") {
            var songSeed = Song(seed.toString(), mAccessToken).getURI()
            finalUrl = url + "seed_tracks=$songSeed"
        }
        else {
            println("seed was not found")
        }

        val request = Request.Builder()
            .url(finalUrl)
            .addHeader("Authorization", "Bearer $mAccessToken")
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                try {
                    for(x in 0 until limit - 1) {
                        root.add(
                            x,
                            JSONObject(body).getJSONArray("tracks").getJSONObject(x).get("uri").toString()
                        )
                    }
                }
                catch (e: IOException) {
                    println("not a valid entry")
                }

            }
            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute")
            }
        })
        Thread.sleep(1_000)
        return root
    }

    fun populateList() {
        var playlistId = createPlaylist()
        var songUris = generateRecommendation()
        var updatedUri: String?
        var finalUri = ""

        for(x in 0 until songUris.size) {
            updatedUri = songUris[x].replace(":", "%3A")
            updatedUri += "%2C"
            songUris[x] = updatedUri
        }

        var lastUri = songUris[songUris.size - 1].replace("%2C", "")
        songUris[songUris.size - 1] = lastUri

        for(x in 0 until songUris.size) {
            finalUri += songUris[x]
        }

        val body = FormBody.Builder()
            .build()

        val request = Request.Builder()
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .addHeader("Content-Length", "0")
            .addHeader("Authorization", "Bearer $mAccessToken")
            .url("https://api.spotify.com/v1/playlists/$playlistId/tracks?uris=$finalUri")
            .post(body)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Failed to fetch data: $e")
            }

            override fun onResponse(call: Call, response: Response) {
            }
        })

        println(finalUri)
    }

    fun createPlaylist(): String {
        var userUri = getUserUri()
        var x = SpotifyUserIdRequest("PartyPlayer Playlist", "Made by partyplayer", "true")
        var playlistId: String = ""
        var test = Gson().toJson(x)

        val JSON = "application/json".toMediaType()
        var body = RequestBody.create(JSON, test)

        val request = Request.Builder()
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $mAccessToken")
            .url("https://api.spotify.com/v1/users/$userUri/playlists")
            .post(body)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                println(response.toString())
                println(response.body.toString())
                val spResponseObj: SpotifyPlaylistReponseObject = Gson().fromJson(response.body?.string(), SpotifyPlaylistReponseObject::class.java)
                println(spResponseObj.id)
                println(spResponseObj.name)
                playlistId = spResponseObj.id
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute15")
            }


        })
        Thread.sleep(1_000)
        return playlistId
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
                println(userUri)
            }
            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute")
            }
        })
        Thread.sleep(1_000)
        return userUri
    }
}


