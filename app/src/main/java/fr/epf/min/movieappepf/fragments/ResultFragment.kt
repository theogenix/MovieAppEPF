package fr.epf.min.movieappepf.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import fr.epf.min.movieappepf.R

class ResultFragment : Fragment() {

    private lateinit var titleTextView: TextView
    private lateinit var posterImageView: ImageView
    private lateinit var overviewTextView: TextView
    private lateinit var releaseDateTextView: TextView
    //private lateinit var genresTextView: TextView
    private lateinit var popularityTextView: TextView
    private lateinit var voteCountTextView: TextView
    private lateinit var voteAverageTextView: TextView

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
        //genresTextView = view.findViewById(R.id.genresTextView)
        popularityTextView = view.findViewById(R.id.popularityTextView)
        voteCountTextView = view.findViewById(R.id.voteCountTextView)
        voteAverageTextView = view.findViewById(R.id.voteAverageTextView)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Récupérez les données transmises depuis le Bundle
        val title = arguments?.getString("title")
        val posterPath = arguments?.getString("poster_path")
        val overview = arguments?.getString("overview")
        val releaseDate = arguments?.getString("release_date")
        //val genres =arguments?.getIntArray("genre_ids")
        val popularity = arguments?.getDouble("popularity")
        val voteCount = arguments?.getInt("vote_count")
        val voteAverage = arguments?.getDouble("vote_average")

        // Affichez les données dans les TextView correspondants
        titleTextView.text = title
        overviewTextView.text = overview
        releaseDateTextView.text = releaseDate
        //genresTextView.text = genres
        popularityTextView.text = popularity.toString()
        voteCountTextView.text = voteCount.toString()
        voteAverageTextView.text = voteAverage.toString()

        // Utilisez une bibliothèque comme Picasso ou Glide pour charger l'image à partir de l'URL du posterPath
        if (!posterPath.isNullOrEmpty()) {
            val fullPosterPath = "https://image.tmdb.org/t/p/original$posterPath"
            Glide.with(requireContext())
                .load(fullPosterPath)
                //.transform(FitCenter())
                .override(500, 500)
                .centerInside()
                .into(posterImageView)
        }
    }
}
