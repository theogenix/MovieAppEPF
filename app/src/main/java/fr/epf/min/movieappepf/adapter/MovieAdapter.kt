package fr.epf.min.movieappepf.adapter

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowId
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.epf.min.movieappepf.MainActivity
import fr.epf.min.movieappepf.MovieModel
import fr.epf.min.movieappepf.MoviePopup
import fr.epf.min.movieappepf.R
import fr.epf.min.movieappepf.fragments.ResultFragment

class MovieAdapter (
    val context:MainActivity,
    private val movieList:List<MovieModel>,
    private val layoutId: Int

    ):RecyclerView.Adapter<MovieAdapter.ViewHolder>(){


    // boite pour ranger tous les composants à controler
    class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        //image du film
        val movieImage=view.findViewById<ImageView>(R.id.image_item)
        val movieName:TextView?=view.findViewById(R.id.name_item)
        val movieVoteAverage:TextView?=view.findViewById(R.id.vote_average_item)
        val starIcon=view.findViewById<ImageView>(R.id.star_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(layoutId,parent,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int =movieList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // recuperer les infos du film
        val currentMovie=movieList[position]
        // utiliser dépendances glide pour récup image a partir de son url
        Glide.with(context).load(Uri.parse(currentMovie.imageUrl)).into(holder.movieImage)
        // mettre à jour nom du film
        holder.movieName?.text=currentMovie.name
        // mettre à jour description du film
        holder.movieVoteAverage?.text= currentMovie.voteAverage.toString()
        // verifier si le film a ete like ou non
        if (holder.starIcon != null) {
            if (currentMovie.liked){
                holder.starIcon.setImageResource(R.drawable.ic_star)
            }else{
                holder.starIcon.setImageResource(R.drawable.ic_unstar)
            }

        }
        //interaction lors du click sur un film

        holder.itemView.setOnClickListener {
            //afficher la popup
            MoviePopup(this,currentMovie).show()

        }

    }
}