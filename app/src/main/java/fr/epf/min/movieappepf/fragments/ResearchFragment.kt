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
import retrofit2.http.Query

class ResearchFragment : Fragment() {

    private lateinit var searchView: SearchView
    private lateinit var movieApiService: MovieApiService
    private var movies: List<Movie> = emptyList()

    // Déclaration de l'interface de service pour communiquer avec l'API
    interface MovieApiService {
        @GET("search/movie")
        fun searchMovies(
            @Query("api_key") apiKey: String,
            @Query("query") query: String
        ): Call<SearchResult>
    }

    private fun mapToMovie(searchResult: SearchResult): Movie? {
        val movieData = searchResult.results.firstOrNull()
        return movieData?.let {
            Movie(
                poster_path = it.poster_path,
                adult = it.adult,
                overview = it.overview,
                release_date = it.release_date,
                //genre_ids = it.genre_ids,
                id = it.id,
                original_title = it.original_title,
                original_language = it.original_language,
                title = it.title,
                backdrop_path = it.backdrop_path,
                popularity = it.popularity,
                vote_count = it.vote_count,
                video = it.video,
                vote_average = it.vote_average
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_research, container, false)
        searchView = view.findViewById(R.id.searchView)

        // Configurer Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Créer une instance de MovieApiService
        movieApiService = retrofit.create(MovieApiService::class.java)

        // Configurer le SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // Appeler la méthode de recherche de films avec la clé d'API et la requête de recherche
                val apiKey = "6b20b9e496710f84a435a42ec1086350"
                val call = movieApiService.searchMovies(apiKey, query)

                // Exécuter la requête de recherche de films
                call.enqueue(object : Callback<SearchResult> {
                    override fun onResponse(call: Call<SearchResult>, response: Response<SearchResult>) {
                        if (response.isSuccessful) {
                            val searchResult = response.body()
                            movies = searchResult?.results ?: emptyList()
                            Log.d("SearchFragment", "Requête de recherche réussie")
                            Log.d("SearchFragment", "Résultats: $movies")

                            // Vérifier si des films ont été trouvés
                            if (movies.isNotEmpty()) {
                                // Afficher le fragment ResultFragment avec les données du premier film
                                showMovieDetails(movies.first())
                            }
                        } else {
                            // Gérer les erreurs de réponse de l'API ici
                            Log.e("SearchFragment", "Erreur de réponse de l'API: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<SearchResult>, t: Throwable) {
                        // Gérer les erreurs de connexion ou d'exécution de la requête ici
                        Log.e("SearchFragment", "Erreur lors de l'exécution de la requête de recherche: ${t.message}")
                    }
                })

                // Logique à exécuter lorsque l'utilisateur soumet la requête de recherche
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // Logique à exécuter lorsque le texte de recherche change
                return true
            }
        })

        return view
    }

    // Méthode pour afficher les résultats dans ResultFragment
    private fun showMovieDetails(movie: Movie) {
        val resultFragment = parentFragmentManager.findFragmentById(R.id.fragment_container) as? ResultFragment

        if (resultFragment != null) {
            // Le fragment ResultFragment est déjà attaché, mettez à jour ses données
            val bundle = Bundle()
            bundle.putString("title", movie.title)
            bundle.putString("poster_path", movie.poster_path)
            bundle.putString("overview", movie.overview)
            bundle.putString("release_date", movie.release_date)
            //bundle.putIntArray("genre_ids", movie.genre_ids.toIntArray())
            bundle.putDouble("popularity", movie.popularity)
            bundle.putInt("vote_count", movie.vote_count)
            bundle.putDouble("vote_average", movie.vote_average)

            resultFragment.arguments = bundle
        } else {
            // Le fragment ResultFragment n'est pas encore attaché, créez une nouvelle instance
            val newResultFragment = ResultFragment()

            val bundle = Bundle()
            bundle.putString("title", movie.title)
            bundle.putString("poster_path", movie.poster_path)
            bundle.putString("overview", movie.overview)
            bundle.putString("release_date", movie.release_date)
            //bundle.putIntArray("genre_ids", movie.genre_ids.toIntArray())
            bundle.putDouble("popularity", movie.popularity)
            bundle.putInt("vote_count", movie.vote_count)
            bundle.putDouble("vote_average", movie.vote_average)

            newResultFragment.arguments = bundle

            // Remplacez le fragment actuel par le nouveau ResultFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, newResultFragment)
                .commit()
        }
    }
}
