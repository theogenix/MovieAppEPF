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
import fr.epf.min.movieappepf.MainActivity
import fr.epf.min.movieappepf.MovieModel
import fr.epf.min.movieappepf.R
import androidx.fragment.app.FragmentActivity


class ResultFragment : Fragment() {

    private lateinit var titleTextView: TextView
    private lateinit var posterImageView: ImageView
    private lateinit var overviewTextView: TextView
    private lateinit var releaseDateTextView: TextView
    private lateinit var originalLanguageTextView: TextView
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
        originalLanguageTextView=view.findViewById(R.id.originalLanguageTextView)
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
        val originalLanguage=arguments?.getString("original_language")
        //val genres =arguments?.getIntArray("genre_ids")
        val popularity = arguments?.getDouble("popularity")
        val voteCount = arguments?.getInt("vote_count")
        val voteAverage = arguments?.getDouble("vote_average")

        // Affichage des données dans les TextView correspondants
        titleTextView.text = title
        overviewTextView.text = overview
        releaseDateTextView.text = releaseDate
        originalLanguageTextView.text = originalLanguage
        //genresTextView.text = genres
        popularityTextView.text = popularity.toString()
        voteCountTextView.text = voteCount.toString()
        voteAverageTextView.text = voteAverage.toString()

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
        // Ajout d'un OnClickListener au bouton
        val button = view.findViewById<Button>(R.id.StarButton)
        button.setOnClickListener {
            //
            if (!title.isNullOrEmpty() && !overview.isNullOrEmpty() && !posterPath.isNullOrEmpty()) {
                posterPath="https://image.tmdb.org/t/p/original$posterPath"
                val movie = MovieModel(title!!, overview!!, posterPath!!, true,releaseDate!!,originalLanguage!!,voteAverage!!)
                MainActivity.movieList.add(movie)
                println("Nom : $title")
                println("Description : $overview")
                println("URL de l'image : $posterPath")
                println("Nom : $releaseDate")
                println("Description : $originalLanguage")
                println("URL de l'image : $voteAverage")

                Log.d("MainActivity", "movieList: ${MainActivity.movieList}")
            }
        }
    }
}
