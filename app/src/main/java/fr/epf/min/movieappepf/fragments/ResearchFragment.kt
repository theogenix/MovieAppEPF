package fr.epf.min.movieappepf.fragments


import android.os.Bundle
import android.telecom.Call
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.epf.min.movieappepf.MainActivity
import fr.epf.min.movieappepf.R
import androidx.appcompat.widget.SearchView
import fr.epf.min.movieappepf.SearchResult
import retrofit2.http.GET
import retrofit2.http.Query





class ResearchFragment : Fragment() {

    private lateinit var searchView: SearchView

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


        // Configurer le SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
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



/*
class ResearchFragment (
    private val context: MainActivity
        ) : Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater?.inflate(R.layout.fragment_research,container,false)

        // recuperer search view

        val searchView = view?.findViewById<SearchView>(R.id.searchView)
        return view
    }
}
*/
