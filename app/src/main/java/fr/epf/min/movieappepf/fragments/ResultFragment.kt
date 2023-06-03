package fr.epf.min.movieappepf.fragments

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
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
    private lateinit var popularityTextView: TextView
    private lateinit var voteCountTextView: TextView
    private lateinit var voteAverageTextView: TextView
    private var isButtonClicked = false
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
        popularityTextView = view.findViewById(R.id.popularityTextView)
        voteCountTextView = view.findViewById(R.id.voteCountTextView)
        voteAverageTextView = view.findViewById(R.id.voteAverageTextView)
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val title = arguments?.getString("title")
        var posterPath = arguments?.getString("poster_path")
        val overview = arguments?.getString("overview")
        val releaseDate = arguments?.getString("release_date")
        val originalLanguage = arguments?.getString("original_language")
        val id = arguments?.getString("id")
        val popularity = arguments?.getDouble("popularity")
        val voteCount = arguments?.getInt("vote_count")
        val voteAverage = arguments?.getDouble("vote_average")
        titleTextView.text = title
        overviewTextView.text = overview
        releaseDateTextView.text = releaseDate
        originalLanguageTextView.text = originalLanguage
        idTextView.text = id
        popularityTextView.text = popularity.toString()
        voteCountTextView.text = voteCount.toString()
        voteAverageTextView.text = voteAverage.toString()

        if (!posterPath.isNullOrEmpty()) {
            val fullPosterPath = "https://image.tmdb.org/t/p/original$posterPath"
            Glide.with(requireContext())
                .load(fullPosterPath)
                .override(500, 500)
                .centerInside()
                .into(posterImageView)
        }
        val button = view.findViewById<Button>(R.id.StarButton)
        val defaultIcon: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.ic_unstar)
        val clickedIcon: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.ic_star)
        val iconStateList = StateListDrawable()
        iconStateList.addState(intArrayOf(android.R.attr.state_pressed), clickedIcon)
        iconStateList.addState(intArrayOf(), defaultIcon)
        button.setCompoundDrawablesWithIntrinsicBounds(iconStateList, null, null, null)
        button.setOnClickListener {
            isButtonClicked = !isButtonClicked
            if (isButtonClicked) {
                button.setCompoundDrawablesWithIntrinsicBounds(clickedIcon, null, null, null)
            } else {
                button.setCompoundDrawablesWithIntrinsicBounds(defaultIcon, null, null, null)
            }
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
                button.isEnabled = false
            }
        }
        val suggestButton = view.findViewById<Button>(R.id.suggestionButton)
        val suggestIcon: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.ic_recommendation)
        suggestButton.setCompoundDrawablesWithIntrinsicBounds(suggestIcon, null, null, null)
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
                    showSuggestionsMovieDetails(recommendedMovies)
                } else {
                }
            }
            override fun onFailure(call: Call<SearchResult>, t: Throwable) {
            }
        })
    }
    private fun showSuggestionsMovieDetails(movieList: List<Movie>) {
        val stackSuggestionFragment = StackSuggestionFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, stackSuggestionFragment)
            .commit()
        for (movie in movieList) {
            val newResultFragment = ResultFragment()
            val bundle = createMovieBundle(movie)
            newResultFragment.arguments = bundle
            parentFragmentManager.beginTransaction()
                .add(R.id.stackSuggestionContainer, newResultFragment)
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
