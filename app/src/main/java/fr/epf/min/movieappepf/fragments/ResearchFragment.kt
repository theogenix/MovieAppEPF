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

    // Déclaration de l'interface de service pour communiquer avec l'API
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
    }

    //méthode pour créer la vue du fragment grace au xml fragment_research
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        onCreateViewCount++
        val view = inflater.inflate(R.layout.fragment_research, container, false)
        searchView = view.findViewById(R.id.searchView)
        Log.d("SearchFragment", "onCreateView appelé $onCreateViewCount fois")


        // Configurer Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Créer une instance de MovieApiService . permet de définir la méthode searchMovies() pour appeler l'API de recherche de film
        movieApiService = retrofit.create(MovieApiService::class.java)

        // Configurer le SearchView  représente le composant de la barre de recherhce
        // setOnQueryTextLister pour écouter ce que l'user tape dans la barre de recherche
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            //onQueryTextSubmit pour qd l'user submit la requete
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
                                showMovieDetails(movies)
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
    private fun showMovieDetails(movieList: List<Movie>) {
        // Créer un nouveau fragment StackFragment
        val stackFragment = StackFragment()

        // Ajouter le StackFragment dans le fragment_container
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, stackFragment)
            .commit()

        // Ajouter chaque ResultFragment empilé dans le StackFragment
        for (movie in movieList) {
            val newResultFragment = ResultFragment()
            val bundle = createMovieBundle(movie)
            newResultFragment.arguments = bundle
            parentFragmentManager.beginTransaction()
                .add(R.id.stackContainer, newResultFragment)
                .addToBackStack(null)  // Ajouter à la pile arrière
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
        //bundle.putIntArray("genre_ids", movie.genre_ids.toIntArray())
        bundle.putDouble("popularity", movie.popularity)
        bundle.putInt("vote_count", movie.vote_count)
        bundle.putDouble("vote_average", movie.vote_average)
        return bundle
    }
}
