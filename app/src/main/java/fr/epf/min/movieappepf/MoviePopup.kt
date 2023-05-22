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
            //fermer le popup
            dismiss()
        }
    }


    private fun setupComponents() {
        //actualiser l'image du film
        val movieImage=findViewById<ImageView>(R.id.popup_image)
        Glide.with(adapter.context).load(Uri.parse(currentMovie.imageUrl)).into(movieImage)
        //actualiser le nom du film
        findViewById<TextView>(R.id.popup_movie_name).text=currentMovie.name
        //actualiser la description du film
        findViewById<TextView>(R.id.popup_movie_subtitle).text=currentMovie.description
    }
}