package fr.epf.min.movieappepf.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import fr.epf.min.movieappepf.R

class MovieAdapter :RecyclerView.Adapter<MovieAdapter.ViewHolder>(){


    // boite pour ranger tous les composants à controler
    class ViewHolder(view: View):RecyclerView.ViewHolder(view){

        //image du film

        val movieImage=view.findViewById<ImageView>(R.id.image_item)



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_horizontal_movie,parent,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int =5

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    }

}