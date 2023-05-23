package fr.epf.min.movieappepf.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import androidx.fragment.app.FragmentActivity
import fr.epf.min.movieappepf.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ResultFragment : Fragment() {

    private lateinit var titleTextView: TextView
    private lateinit var posterImageView: ImageView
    private lateinit var overviewTextView: TextView
    private lateinit var releaseDateTextView: TextView
    private lateinit var originalLanguageTextView: TextView
    private lateinit var idTextView: TextView

    //private lateinit var genresTextView: TextView
    private lateinit var popularityTextView: TextView
    private lateinit var voteCountTextView: TextView
    private lateinit var voteAverageTextView: TextView
    //creation de la vue du fragment à partir du xml fragment_results
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_results, container, false)
        titleTextView = view.findViewById(R.id.titleTextView)
        posterImageView = view.findViewById(R.id.posterImageView)
        overviewTextView = view.findViewById(R.id.overviewTextView)
        releaseDateTextView = view.findViewById(R.id.releaseDateTextView)
        originalLanguageTextView = view.findViewById(R.id.originalLanguageTextView)
        idTextView = view.findViewById(R.id.idTextView)
        //genresTextView = view.findViewById(R.id.genresTextView)
        popularityTextView = view.findViewById(R.id.popularityTextView)
        voteCountTextView = view.findViewById(R.id.voteCountTextView)
        voteAverageTextView = view.findViewById(R.id.voteAverageTextView)
        return view
    }

    //configuration des données du fragment après que la vue a été créée
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Récupération des données transmises depuis le Bundle
        val title = arguments?.getString("title")
        var posterPath = arguments?.getString("poster_path")
        val overview = arguments?.getString("overview")
        val releaseDate = arguments?.getString("release_date")
        val originalLanguage = arguments?.getString("original_language")
        val id = arguments?.getString("id")
        //val genres =arguments?.getIntArray("genre_ids")
        val popularity = arguments?.getDouble("popularity")
        val voteCount = arguments?.getInt("vote_count")
        val voteAverage = arguments?.getDouble("vote_average")


        // Affichage des données dans les TextView correspondants
        titleTextView.text = title
        overviewTextView.text = overview
        releaseDateTextView.text = releaseDate
        originalLanguageTextView.text = originalLanguage
        idTextView.text = id
        //genresTextView.text = genres.toString()
        popularityTextView.text = popularity.toString()
        voteCountTextView.text = voteCount.toString()
        voteAverageTextView.text = voteAverage.toString()
        //println(genres?.joinToString(" "))
        //println(genres?.size)

        // Chargement de l'image à partir de l'URL du posterPath
        if (!posterPath.isNullOrEmpty()) {
            val fullPosterPath = "https://image.tmdb.org/t/p/original$posterPath"
            Glide.with(requireContext())
                .load(fullPosterPath)
                //.transform(FitCenter())
                .override(500, 500)
                .centerInside()
                .into(posterImageView)
        }
        // Ajout d'un OnClickListener au bouton fav here
        val button = view.findViewById<Button>(R.id.StarButton)
        button.setOnClickListener {
            //
            if (!title.isNullOrEmpty() && !overview.isNullOrEmpty() && !posterPath.isNullOrEmpty()) {
                posterPath = "https://image.tmdb.org/t/p/original$posterPath"
                val movie = MovieModel(
                    title!!,
                    overview!!,
                    posterPath!!,
                    true,
                    releaseDate!!,
                    originalLanguage!!,
                    voteAverage!!
                )
                MainActivity.movieList.add(movie)
                println("Nom : $title")
                println("Description : $overview")
                println("URL de l'image : $posterPath")
                println("release date : $releaseDate")
                println("original Language : $originalLanguage")
                println("vote average : $voteAverage")

                Log.d("MainActivity", "movieList: ${MainActivity.movieList}")
            }
        }
        // Ajout d'un OnClickListener au bouton suggest here
        val suggestButton = view.findViewById<Button>(R.id.suggestionButton)
        suggestButton.setOnClickListener {
            val movieId = idTextView.text.toString().toInt()
            fetchMovieRecommendations(movieId)
        }
    }

    private fun fetchMovieRecommendations(movieId: Int) {
        val apiKey = "6b20b9e496710f84a435a42ec1086350"

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val movieService = retrofit.create(ResearchFragment.MovieApiService::class.java)
        val call = movieService.getRecommendations(movieId, apiKey)
        call.enqueue(object : Callback<SearchResult> {
            override fun onResponse(call: Call<SearchResult>, response: Response<SearchResult>) {
                if (response.isSuccessful) {
                    val searchResult = response.body()
                    val recommendedMovies = searchResult?.results ?: emptyList()
                    Log.d("SearchFragment", "Requête de recherche réussie")
                    Log.d("SearchFragment", "Résultats: $recommendedMovies")
                    showSuggestionsMovieDetails(recommendedMovies)
                } else {
                    // Gérer les erreurs de réponse de l'API
                }
            }
            override fun onFailure(call: Call<SearchResult>, t: Throwable) {
                // Gérer les erreurs de connexion ou d'exécution de la requête
            }
        })
    }
    private fun showSuggestionsMovieDetails(movieList: List<Movie>) {
        // Créer un nouveau fragment StackSuggestionFragment
        val stackSuggestionFragment = StackSuggestionFragment()

        // Ajouter le StackSuggestionFragment dans le fragment_container
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, stackSuggestionFragment)
            .commit()

        // Ajouter chaque ResultFragment empilé dans le StackSuggestionFragment
        for (movie in movieList) {
            val newResultFragment = ResultFragment()
            val bundle = createMovieBundle(movie)
            newResultFragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .add(R.id.stackSuggestionContainer, newResultFragment)
                // Ajouter à la pile arrière
                .addToBackStack(null)
                .commit()
        }
    }

    private fun createMovieBundle(movie: Movie): Bundle {
        val bundle = Bundle()
        bundle.putString("title", movie.title)
        bundle.putString("poster_path", movie.poster_path)
        bundle.putString("overview", movie.overview)
        bundle.putString("release_date", movie.release_date)
        bundle.putString("original_language", movie.original_language)
        bundle.putString("id", movie.id.toString())
        bundle.putDouble("popularity", movie.popularity)
        bundle.putInt("vote_count", movie.vote_count)
        bundle.putDouble("vote_average", movie.vote_average)
        return bundle
    }
}
