package com.example.party_player

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.PlayerApi
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Empty
import com.spotify.protocol.types.Track
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private val clientId = "ee5c409ead93465fa2f1e60bd7e2aaa1"
    private val redirectUri = "com.example.party-player://callback"
    lateinit var spotifyAppRemote: SpotifyAppRemote


    //need to fix lambda problem
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupViews()
        setupListeners()
        CoroutineScope(IO).launch {
            getResult()
        }
    }

    private fun setupViews () {
        SpotifyService.playingState {
            when(it) {
                PlayingState.PLAYING -> showPauseButton()
                PlayingState.STOPPED -> showPlayButton()
                PlayingState.PAUSED -> showResumeButton()
            }
        }
    }

    private suspend fun getResult() {
        val test = SpotifyScrape()
        delay(1000)
    }

    private fun showPlayButton() {
        playButton.visibility = View.VISIBLE
    }

    private fun showPauseButton() {
        pauseButton.visibility = View.VISIBLE
    }

    private fun showResumeButton() {
        resumeButton.visibility = View.VISIBLE
    }



    private fun setupListeners() {


        playButton.setOnClickListener {
            val connectionParams = ConnectionParams.Builder(clientId)
                .setRedirectUri(redirectUri)
                .showAuthView(true)
                .build()

            SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
                override fun onConnected(appRemote: SpotifyAppRemote) {
                    spotifyAppRemote = appRemote
                    Log.d("MainActivity", "Connected! Yay!")
                    // Now you can start interacting with App Remote
                    connected()
                }

                override fun onFailure(throwable: Throwable) {
                    Log.e("MainActivity", throwable.message, throwable)
                    // Something went wrong when attempting to connect! Handle errors here
                }
            })
            SpotifyService.play("spotify:track:1lNRVjK8MukRZpeurYssIx")
            showPauseButton()
        }

        pauseButton.setOnClickListener {
            spotifyAppRemote.playerApi.pause()
            showResumeButton()
        }

        resumeButton.setOnClickListener {
            spotifyAppRemote.playerApi.resume()
            showPauseButton()
        }

        nextSongButton.setOnClickListener {
            spotifyAppRemote.playerApi.skipNext()
        }

        previousSongButton.setOnClickListener {
            spotifyAppRemote.playerApi.skipPrevious()
        }
    }

    private fun connected() {
        spotifyAppRemote.let {
            // Play a playlist
            val playlistURI = "spotify:track:1lNRVjK8MukRZpeurYssIx"
            it.playerApi.play(playlistURI)
            // Subscribe to PlayerState
            it.playerApi.subscribeToPlayerState().setEventCallback {
                val track: Track = it.track
                Log.d("MainActivity", track.name + " by " + track.artist.name)
            }
        }

    }

    override fun onStop() {
        super.onStop()
        spotifyAppRemote.let {
            SpotifyAppRemote.disconnect(it)
        }

    }
}