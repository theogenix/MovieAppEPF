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

class HomeFragment(
    private val context:MainActivity
    ) : Fragment(){


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view=inflater?.inflate(R.layout.fragment_home,container,false)

// creer une liste qui va stocker les films

        val movieList= arrayListOf<MovieModel>()

        //enregistrer un premier filml ds la liste

        movieList.add(
            MovieModel(
            "Star Wars",
            "bcp de jedis",
                "https://fr.web.img6.acsta.net/pictures/16/10/19/14/33/069648.jpg",
                false
        )
        )
        movieList.add(
            MovieModel(
            "your name",
            "anime",
                "https://fr.web.img2.acsta.net/pictures/16/12/12/13/49/295774.jpg",
                false
        )
        )
        movieList.add(
            MovieModel(
            "Harry Potter",
            "magie",
                "https://fr.web.img2.acsta.net/pictures/18/07/02/17/25/3643090.jpg",
                true
        )
        )
        movieList.add(
            MovieModel(
            "avengers",
            "héros",
                "https://fr.web.img3.acsta.net/medias/nmedia/18/85/31/58/20042068.jpg",
                false
        )
        )


        // recuperer le recycler view
        val horizontalRecyclerView= view!!.findViewById<RecyclerView>(R.id.horizontal_recycler_view)
        horizontalRecyclerView.adapter=MovieAdapter(context,movieList,R.layout.item_horizontal_movie)

        // recuperer le 2eme recycler view

        val verticalRecyclerView=view.findViewById<RecyclerView>(R.id.vertical_recycler_view)
        verticalRecyclerView.adapter=MovieAdapter(context,movieList,R.layout.item_vertical_movie)
        verticalRecyclerView.addItemDecoration(MovieItemDecoration())


        return view
    }



}