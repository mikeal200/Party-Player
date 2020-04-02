import androidx.lifecycle.MutableLiveData
import com.example.party_player.*
import com.example.party_player.paks.Tracks
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/*class SpotifyService {
    val main = MainActivity()

    fun fetchTracks(artistName: String, songName: String, accessToken: String): MutableLiveData<ArrayList<Tracks>> {
        var _tracks = MutableLiveData<ArrayList<Tracks>>()
        val service = RetrofitClientInstance.retrofitInstance?.create(SpotifyApi::class.java)
        val call = service?.searchTracks("herbie hancock", "Watermelon man", main.mAccessToken)
        call?.enqueue(object: Callback<ArrayList<Tracks>> {
            /**
             * Invoked when a network exception occurred talking to the server or when an unexpected
             * exception occurred creating the request or processing the response.
             */
            override fun onFailure(call: Call<ArrayList<Tracks>>, t: Throwable) {
                val j = 1 + 1
                val i = 1 + 1
            }

            /**
             * Invoked for a received HTTP response.
             *
             *
             * Note: An HTTP response may still indicate an application-level failure such as a 404 or 500.
             * Call [Response.isSuccessful] to determine if the response indicates success.
             */
            override fun onResponse(
                call: Call<ArrayList<Tracks>>,
                response: Response<ArrayList<Tracks>>
            ) {
                _tracks.value = response.body()
            }


        })
        return _tracks
    }
}*/