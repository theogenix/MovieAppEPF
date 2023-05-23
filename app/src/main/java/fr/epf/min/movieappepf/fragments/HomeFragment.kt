package fr.epf.min.movieappepf.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import fr.epf.min.movieappepf.MainActivity
import fr.epf.min.movieappepf.MovieModel
import fr.epf.min.movieappepf.R
import fr.epf.min.movieappepf.adapter.MovieAdapter
import fr.epf.min.movieappepf.adapter.MovieItemDecoration

class HomeFragment(private val context: MainActivity) : Fragment() {
    private val movieList = arrayListOf<MovieModel>()
    private lateinit var horizontalRecyclerView: RecyclerView
    private lateinit var verticalRecyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Liste des films à ajouter au tt début
        val newMovies = listOf(
            MovieModel(
                "Star Wars",
                "bcp de jedis",
                "https://fr.web.img6.acsta.net/pictures/16/10/19/14/33/069648.jpg",
                false,
                "20 janvier",
                "en",
                5.25
            ),
            MovieModel(
                "your name",
                "anime",
                "https://fr.web.img2.acsta.net/pictures/16/12/12/13/49/295774.jpg",
                false,
                "20 janvier",
                "en",
                5.25
            ),
            MovieModel(
                "Harry Potter",
                "magie",
                "https://fr.web.img2.acsta.net/pictures/18/07/02/17/25/3643090.jpg",
                true,
                "20 janvier",
                "en",
                5.25
            ),
            MovieModel(
                "avengers",
                "héros",
                "https://fr.web.img3.acsta.net/medias/nmedia/18/85/31/58/20042068.jpg",
                false,
                "20 janvier",
                "en",
                5.25
            ),
            MovieModel(
                "Ice Age",
                "Anime",
                "https://static.fnac-static.com/multimedia/Images/A8/A8/B1/47/4698536-1505-1505-1/tsp20180314141735/L-Age-De-Glace-4-AFFICHE-CINEMA-ORIGINALE.jpg",
                false,
                "20 janvier",
                "en",
                5.25
            )
        )

        // Ajouter uniquement les films qui ne sont pas déjà présents dans la liste
        newMovies.forEach { movie ->
            val isMovieAlreadyAdded = movieList.any { it.name == movie.name }
            if (!isMovieAlreadyAdded) {
                movieList.add(movie)
            }
        }

        horizontalRecyclerView = view.findViewById(R.id.horizontal_recycler_view)
        horizontalRecyclerView.adapter = MovieAdapter(context, movieList, R.layout.item_horizontal_movie)

        verticalRecyclerView = view.findViewById(R.id.vertical_recycler_view)
        verticalRecyclerView.adapter = MovieAdapter(context, movieList, R.layout.item_vertical_movie)
        verticalRecyclerView.addItemDecoration(MovieItemDecoration())

        return view
    }


    //update les recyclersviews après avoir rajouté des films dans la liste movieList
    fun updateMovieList(newMovieList: List<MovieModel>) {
        for (newMovie in newMovieList) {
            val existingMovie = movieList.find { it.name == newMovie.name }
            if (existingMovie == null) {
                movieList.add(newMovie)
            } else {
            }
        }

        horizontalRecyclerView.adapter?.notifyDataSetChanged()
        verticalRecyclerView.adapter?.notifyDataSetChanged()
        println("updateMovieList")
    }

}