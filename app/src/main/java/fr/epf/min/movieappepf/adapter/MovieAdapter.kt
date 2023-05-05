package fr.epf.min.movieappepf.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowId
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import fr.epf.min.movieappepf.R

class MovieAdapter (private val layoutId: Int):RecyclerView.Adapter<MovieAdapter.ViewHolder>(){


    // boite pour ranger tous les composants à controler
    class ViewHolder(view: View):RecyclerView.ViewHolder(view){

        //image du film

        val movieImage=view.findViewById<ImageView>(R.id.image_item)



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(layoutId,parent,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int =5

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    }

}