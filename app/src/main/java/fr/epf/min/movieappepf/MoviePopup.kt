package fr.epf.min.movieappepf

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import fr.epf.min.movieappepf.adapter.MovieAdapter

class MoviePopup (
    private val adapter: MovieAdapter,
    private val currentMovie:MovieModel
) :Dialog(adapter.context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.popup_movie_details)
        setupComponents()
        setupClosebutton()
    }

    private fun setupClosebutton(){
        findViewById<ImageView>(R.id.popup_close_button).setOnClickListener {
            dismiss()
        }
    }


    private fun setupComponents() {
        val movieImage=findViewById<ImageView>(R.id.popup_image)
        Glide.with(adapter.context).load(Uri.parse(currentMovie.imageUrl)).into(movieImage)
        findViewById<TextView>(R.id.popup_movie_name).text=currentMovie.name
        findViewById<TextView>(R.id.popup_movie_subtitle).text=currentMovie.description
        findViewById<TextView>(R.id.popup_movie_release_date).text=currentMovie.releaseDate
        findViewById<TextView>(R.id.popup_movie_orginal_language).text=currentMovie.originalLanguage
        findViewById<TextView>(R.id.popup_movie_vote_average).text= currentMovie.voteAverage.toString()
    }
}