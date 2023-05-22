package fr.epf.min.movieappepf

import java.io.FileDescriptor
import java.util.Date

class MovieModel (
    val name:String ="Avengers",
    val description:String="Bon film",
    val imageUrl:String="https://fr.web.img6.acsta.net/pictures/16/10/19/14/33/069648.jpg",
    var liked:Boolean=false,
    var releaseDate: String,
    var originalLanguage:String="en",
    var voteAverage:Double,
        )