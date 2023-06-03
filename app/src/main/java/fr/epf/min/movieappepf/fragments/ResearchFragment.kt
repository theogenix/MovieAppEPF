package fr.epf.min.movieappepf.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import fr.epf.min.movieappepf.Movie
import fr.epf.min.movieappepf.R
import fr.epf.min.movieappepf.SearchResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

class ResearchFragment : Fragment() {

    private lateinit var searchView: SearchView
    private lateinit var movieApiService: MovieApiService
    private var movies: List<Movie> = emptyList()
    private var onCreateViewCount = 0
    interface MovieApiService {
        @GET("search/movie")
        fun searchMovies(
            @Query("api_key") apiKey: String,
            @Query("query") query: String
        ): Call<SearchResult>

        @GET("movie/{id}/recommendations")
        fun getRecommendations(
            @Path("id") movieId: Int,
            @Query("api_key") apiKey: String
        ): Call<SearchResult>
        @GET("movie/{id}")
        fun getMovieDetails(
            @Path("id") movieId: Int,
            @Query("api_key") apiKey: String
        ): Call<Movie>
        @GET("discover/movie")
        fun getMoviesByGenre(
            @Query("api_key") apiKey: String,
            @Query("with_genres") genreId: Int
        ): Call<SearchResult>
        @GET("trending/movie/day")
        fun getTrendingMovies(
            @Query("api_key") apiKey: String,
            @Query("language") language: String = "en-US"
        ): Call<SearchResult>

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        onCreateViewCount++
        val view = inflater.inflate(R.layout.fragment_research, container, false)
        searchView = view.findViewById(R.id.searchView)
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        movieApiService = retrofit.create(MovieApiService::class.java)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val apiKey = "6b20b9e496710f84a435a42ec1086350"
                val call = movieApiService.searchMovies(apiKey, query)
                call.enqueue(object : Callback<SearchResult> {
                    override fun onResponse(call: Call<SearchResult>, response: Response<SearchResult>) {
                        if (response.isSuccessful) {
                            val searchResult = response.body()
                            movies = searchResult?.results ?: emptyList()
                            if (movies.isNotEmpty()) {
                                showMovieDetails(movies)
                            }
                        } else {
                            Log.e("SearchFragment", "Erreur de réponse de l'API: ${response.code()}")
                        }
                    }
                    override fun onFailure(call: Call<SearchResult>, t: Throwable) {
                        Log.e("SearchFragment", "Erreur lors de l'exécution de la requête de recherche: ${t.message}")
                    }
                })
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        })

        return view
    }
    private fun showMovieDetails(movieList: List<Movie>) {
        val stackFragment = StackFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, stackFragment)
            .commit()
        for (movie in movieList) {
            val newResultFragment = ResultFragment()
            val bundle = createMovieBundle(movie)
            newResultFragment.arguments = bundle
            parentFragmentManager.beginTransaction()
                .add(R.id.stackContainer, newResultFragment)
                .addToBackStack(null)
                .commit()
            stackFragment.incrementFragmentCount()
        }
    }

    private fun createMovieBundle(movie: Movie): Bundle {
        val bundle = Bundle()
        bundle.putString("title", movie.title)
        bundle.putString("poster_path", movie.poster_path)
        bundle.putString("overview", movie.overview)
        bundle.putString("release_date", movie.release_date)
        bundle.putString("original_language",movie.original_language)
        bundle.putString("id", movie.id.toString())
        bundle.putDouble("popularity", movie.popularity)
        bundle.putInt("vote_count", movie.vote_count)
        bundle.putDouble("vote_average", movie.vote_average)
        return bundle
    }
}
