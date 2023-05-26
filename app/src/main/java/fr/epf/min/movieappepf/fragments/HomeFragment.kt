package fr.epf.min.movieappepf.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import fr.epf.min.movieappepf.*
import fr.epf.min.movieappepf.adapter.MovieAdapter
import fr.epf.min.movieappepf.adapter.MovieItemDecoration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment(private val context: MainActivity) : Fragment() {
    private val movieList = arrayListOf<MovieModel>()
    private val trendingMoviesList: MutableList<MovieModel> = mutableListOf()


    private lateinit var horizontalRecyclerView: RecyclerView
    private lateinit var verticalRecyclerView: RecyclerView
    private var selectedGenreId: Int = 0




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
        fetchTrendingMovies("en")

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
        verticalRecyclerView.adapter = MovieAdapter(context, trendingMoviesList, R.layout.item_vertical_movie)
        verticalRecyclerView.addItemDecoration(MovieItemDecoration())

        return view
    }
    private fun updateTrendingMoviesList(newMoviesList: List<MovieModel>) {
        trendingMoviesList.clear()
        trendingMoviesList.addAll(newMoviesList)
        verticalRecyclerView.adapter?.notifyDataSetChanged()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinner()

        val searchButton = view.findViewById<Button>(R.id.genreButton)
        searchButton.setOnClickListener {
            // Vérifier si un genre est sélectionné
            if (selectedGenreId != 0) {
                fetchMoviesByGenre(selectedGenreId)
            }
        }

        // Autres initialisations et configurations si nécessaires
    }

    private fun setupSpinner() {
        val spinner = requireView().findViewById<Spinner>(R.id.spinner)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genreList.map { it.first })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedGenreId = genreList[position].second

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

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


    private fun fetchMoviesByGenre(genreId: Int) {
        val apiKey = "6b20b9e496710f84a435a42ec1086350"

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val movieService = retrofit.create(ResearchFragment.MovieApiService::class.java)
        val call = movieService.getMoviesByGenre(apiKey,genreId)
        call.enqueue(object : Callback<SearchResult> {
            override fun onResponse(call: Call<SearchResult>, response: Response<SearchResult>) {
                if (response.isSuccessful) {
                    val searchResult = response.body()
                    val genresMovies = searchResult?.results ?: emptyList()
                    Log.d("SearchFragment", "Requête de recherche réussie")
                    Log.d("SearchFragment", "Résultats: $genresMovies")
                    showGenreMovie(genresMovies)
                } else {
                    // Gérer les erreurs de réponse de l'API
                }
            }
            override fun onFailure(call: Call<SearchResult>, t: Throwable) {
                // Gérer les erreurs de connexion ou d'exécution de la requête
            }
        })
    }
    private fun mapMovieToMovieModel(movie: Movie): MovieModel {
        val partialPosterPath = movie.poster_path
        val fullPosterPath = "https://image.tmdb.org/t/p/original$partialPosterPath"

        return MovieModel(
            name = movie.title,
            description = movie.overview,
            imageUrl = fullPosterPath,
            liked = false,
            releaseDate = movie.release_date,
            originalLanguage = movie.original_language,
            voteAverage = movie.vote_average
        )
    }


    private fun fetchTrendingMovies(langue: String) {
        val apiKey = "6b20b9e496710f84a435a42ec1086350"

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val movieService = retrofit.create(ResearchFragment.MovieApiService::class.java)
        val call = movieService.getTrendingMovies(apiKey,langue)
        call.enqueue(object : Callback<SearchResult> {
            override fun onResponse(call: Call<SearchResult>, response: Response<SearchResult>) {
                if (response.isSuccessful) {
                    val searchResult = response.body()
                    val trendingMovies = searchResult?.results ?: emptyList()
                    val trendingMovieModels = trendingMovies.map { mapMovieToMovieModel(it) }
                    updateTrendingMoviesList(trendingMovieModels)
                    Log.d("SearchFragment", "Requête de recherche réussie")
                    Log.d("SearchFragment", "Résultats: $trendingMovies")
                    println("trendingMoviesList: $trendingMoviesList")
                    println("MoviesList: $movieList")


                } else {
                    // Gérer les erreurs de réponse de l'API
                }
            }
            override fun onFailure(call: Call<SearchResult>, t: Throwable) {
                // Gérer les erreurs de connexion ou d'exécution de la requête
            }
        })
    }
    private fun showGenreMovie(movieList: List<Movie>) {
        // Créer un nouveau fragment StackGenreFragment
        val stackGenreFragment = StackGenreFragment()

        // Ajouter le StackSuggestionFragment dans le fragment_container
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, stackGenreFragment)
            .commit()

        // Ajouter chaque ResultFragment empilé dans le StackGenreFragment
        for (movie in movieList) {
            val newResultFragment = ResultFragment()
            val bundle = createMovieBundle(movie)
            newResultFragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .add(R.id.stackGenreContainer, newResultFragment)
                // Ajouter à la pile arrière
                .addToBackStack(null)
                .commit()
        }
    }

    private fun createMovieBundle(movie: Movie): Bundle {
        val bundle = Bundle()
        bundle.putString("title", movie.title)
        bundle.putString("poster_path", movie.poster_path)
        bundle.putString("overview", movie.overview)
        bundle.putString("release_date", movie.release_date)
        bundle.putString("original_language", movie.original_language)
        bundle.putString("id", movie.id.toString())
        bundle.putDouble("popularity", movie.popularity)
        bundle.putInt("vote_count", movie.vote_count)
        bundle.putDouble("vote_average", movie.vote_average)
        return bundle
    }
    val genreList = listOf(
        Pair("Action", 28),
        Pair("Adventure", 12),
        Pair("Animation", 16),
        Pair("Comedy", 35),
        Pair("Crime", 80),
        Pair("Documentary", 99),
        Pair("Drama", 18),
        Pair("Family", 10751),
        Pair("Fantasy", 14),
        Pair("History", 36),
        Pair("Horror", 27),
        Pair("Music", 10402),
        Pair("Mystery", 9648),
        Pair("Romance", 10749),
        Pair("Science Fiction", 878),
        Pair("TV Movie", 10770),
        Pair("Thriller", 53),
        Pair("War", 10752),
        Pair("Western", 37)
    )

}