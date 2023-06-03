package fr.epf.min.movieappepf.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.epf.min.movieappepf.MainActivity
import fr.epf.min.movieappepf.MovieModel
import fr.epf.min.movieappepf.MoviePopup
import fr.epf.min.movieappepf.R
import org.json.JSONObject
import java.io.*

class MovieAdapter (
    private val inflater: LayoutInflater,
    val context:MainActivity,

    private val movieList:MutableList<MovieModel>,
    private val layoutId: Int,
    private val removedMoviesList: MutableList<MovieModel>,
    ):RecyclerView.Adapter<MovieAdapter.ViewHolder>(){

    class ViewHolder(view: View):RecyclerView.ViewHolder(view){
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
        val currentMovie=movieList[position]
        Glide.with(context).load(Uri.parse(currentMovie.imageUrl)).into(holder.movieImage)
        holder.movieName?.text=currentMovie.name
        holder.movieVoteAverage?.text= currentMovie.voteAverage.toString()
        if (holder.starIcon != null) {
            if (currentMovie.liked){
                holder.starIcon.setImageResource(R.drawable.ic_star)
            }else{
                holder.starIcon.setImageResource(R.drawable.ic_unstar)
            }
        }

        val unfavButton = holder.itemView.findViewById<Button>(R.id.unfavButton)
        val icon = ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_trashbinbis)
        unfavButton?.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)
        unfavButton?.setBackgroundResource(android.R.color.transparent)
        unfavButton?.setOnClickListener {
            val removedMovie = movieList[position]
            removedMoviesList.add(removedMovie)
            movieList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount - position)
            val removedMovieName = removedMovie.name
            removeMovieFromFile(removedMovieName, context.filesDir)
            Toast.makeText(holder.itemView.context, "movie removed : $removedMovieName", Toast.LENGTH_SHORT).show()
        }

        holder.itemView.setOnClickListener {
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
            file.delete()
            tempFile.renameTo(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}