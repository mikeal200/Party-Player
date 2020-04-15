package com.example.party_player

import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse

class OAuth {

    private val CLIENT_ID = "3d04daf658674a5c97e487250a7c49ef"
    private val redirectUri = "com.example.party-player://callback"
    val AUTH_TOKEN_REQUEST_CODE = 0x10

    fun onRequestTokenClicked(): Boolean {
        val request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN)
        AuthorizationClient.openLoginActivity(MainActivity(), AUTH_TOKEN_REQUEST_CODE, request)
        return true
    }

    private fun getAuthenticationRequest(type: AuthorizationResponse.Type): AuthorizationRequest {
        return AuthorizationRequest.Builder(CLIENT_ID, type, redirectUri.toString())
            .setShowDialog(false)
            .setScopes(arrayOf("user-modify-playback-state"))
            .setCampaign("your-campaign-token")
            .build()
    }
}