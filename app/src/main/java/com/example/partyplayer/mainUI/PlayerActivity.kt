package com.example.partyplayer.mainUI

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.partyplayer.R
import com.example.partyplayer.Service.PlayingState
import com.example.partyplayer.Service.SpotifyService
import kotlinx.android.synthetic.main.activity_player.*

class PlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        setupViews()
        setupListeners()
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
            SpotifyService.play("potify:playlist:4CFWluyHMClrtcRd9FtAYG")
            showPauseButton()
        }

        pauseButton.setOnClickListener {
            SpotifyService.pause()
            showResumeButton()
        }

        resumeButton.setOnClickListener {
            SpotifyService.resume()
            showPauseButton()
        }
    }


    override fun onStop() {
        super.onStop()
        SpotifyService.disconnect()
    }
}