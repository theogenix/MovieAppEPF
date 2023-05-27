package fr.epf.min.movieappepf.adapter

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowId
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.epf.min.movieappepf.MainActivity
import fr.epf.min.movieappepf.MovieModel
import fr.epf.min.movieappepf.MoviePopup
import fr.epf.min.movieappepf.R
import fr.epf.min.movieappepf.fragments.ResultFragment
import org.json.JSONObject
import java.io.*

class MovieAdapter (
    private val inflater: LayoutInflater,
    val context:MainActivity,

    private val movieList:MutableList<MovieModel>,
    private val layoutId: Int,
    private val removedMoviesList: MutableList<MovieModel>,
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
        val view = inflater.inflate(layoutId, parent, false)
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

        val unfavButton = holder.itemView.findViewById<Button>(R.id.unfavButton)
        val icon = ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_trashbinbis)
        unfavButton?.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)
        unfavButton?.setBackgroundResource(android.R.color.transparent)
        unfavButton?.setOnClickListener {
            //println("movieList dans fonction remove: $movieList")
            val removedMovie = movieList[position]
            //println("position dans fonction: $position")
            removedMoviesList.add(removedMovie)
            movieList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount - position)
            //println("movieList après fonction remove: $movieList")

            // Toast pour afficher après le retrait du film de la liste
            val removedMovieName = removedMovie.name
            removeMovieFromFile(removedMovieName, context.filesDir)
            Toast.makeText(holder.itemView.context, "movie removed : $removedMovieName", Toast.LENGTH_SHORT).show()
        }

        holder.itemView.setOnClickListener {
            //afficher la popup
            MoviePopup(this,currentMovie).show()
        }
    }
    private fun removeMovieFromFile(movieName: String, filesDir: File) {
        val fileName = "donneesFilm.txt"
        val file = File(filesDir, fileName)

        if (!file.exists()) {
            return
        }

        try {
            val tempFile = File(filesDir, "temp.txt")
            tempFile.createNewFile()

            val reader = BufferedReader(FileReader(file))
            val writer = BufferedWriter(FileWriter(tempFile))

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val movieJson = JSONObject(line)
                val name = movieJson.optString("name")
                if (name != movieName) {
                    writer.write(line)
                    writer.newLine()
                }
            }

            writer.close()
            reader.close()

            // Supprimer le fichier d'origine et renommer le fichier temporaire
            file.delete()
            tempFile.renameTo(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}