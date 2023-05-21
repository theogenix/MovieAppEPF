package fr.epf.min.movieappepf.fragments

import android.os.Bundle
import fr.epf.min.movieappepf.Movie

fun createMovieBundle(movie: Movie): Bundle {
    val bundle = Bundle()
    bundle.putString("title", movie.title)
    bundle.putString("poster_path", movie.poster_path)
    bundle.putString("overview", movie.overview)
    bundle.putString("release_date", movie.release_date)
    //bundle.putIntArray("genre_ids", movie.genre_ids.toIntArray())
    bundle.putDouble("popularity", movie.popularity)
    bundle.putInt("vote_count", movie.vote_count)
    bundle.putDouble("vote_average", movie.vote_average)
    return bundle
}