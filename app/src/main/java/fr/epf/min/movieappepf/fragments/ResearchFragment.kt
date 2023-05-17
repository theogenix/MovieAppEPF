package fr.epf.min.movieappepf.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.epf.min.movieappepf.MainActivity
import fr.epf.min.movieappepf.R
import androidx.appcompat.widget.SearchView
import fr.epf.min.movieappepf.SearchResult
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call

class ResearchFragment : Fragment() {

    private lateinit var searchView: SearchView
    private lateinit var movieApiService: MovieApiService

    // déclaration d'interface de service pour communiquer avec l'API
    interface MovieApiService {
        @GET("search/movie")
        fun searchMovies(
            @Query("api_key") apiKey: String,
            @Query("query") query: String
        ): retrofit2.Call<SearchResult>
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_research, container, false)
        searchView = view.findViewById<SearchView>(R.id.searchView)

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
                val apiKey = "VOTRE_CLE_API"
                val call = movieApiService.searchMovies(apiKey, query)

                // Exécuter la requête de recherche de films
                call.enqueue(object : Callback<SearchResult> {
                    override fun onResponse(call: Call<SearchResult>, response: Response<SearchResult>) {
                        if (response.isSuccessful) {
                            val searchResult = response.body()
                            // Traiter les résultats de la recherche ici
                        } else {
                            // Gérer les erreurs de réponse de l'API ici
                        }
                    }

                    override fun onFailure(call: Call<SearchResult>, t: Throwable) {
                        // Gérer les erreurs de connexion ou d'exécution de la requête ici
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
}
