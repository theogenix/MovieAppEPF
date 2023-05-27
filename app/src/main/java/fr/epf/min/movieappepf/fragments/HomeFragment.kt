package fr.epf.min.movieappepf.fragments

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fr.epf.min.movieappepf.*
import fr.epf.min.movieappepf.adapter.MovieAdapter
import fr.epf.min.movieappepf.adapter.MovieItemDecoration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*

class HomeFragment(private val context: MainActivity) : Fragment() {
    private val movieList: MutableList<MovieModel> = mutableListOf()
    private val trendingMoviesList: MutableList<MovieModel> = mutableListOf()

    private lateinit var horizontalRecyclerView: RecyclerView
    private lateinit var verticalRecyclerView: RecyclerView
    private var selectedGenreId: Int = 0
    private val removedMoviesList = mutableListOf<MovieModel>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Chargement des données depuis le fichier texte
        val fileName = "donneesFilm.txt"
        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            file.createNewFile()
        }

        val reader = BufferedReader(InputStreamReader(file.inputStream(), Charsets.UTF_8))
        val listeSauvegardee = mutableListOf<String>()
        var line: String?

        while (reader.readLine().also { line = it } != null) {
            listeSauvegardee.add(line.toString())
        }
        reader.close()

        // Convertir les données sauvegardées en objets MovieModel
        val gson = Gson()
        val savedMovies: List<MovieModel> = listeSauvegardee.map { movieString ->
            gson.fromJson(movieString, object : TypeToken<MovieModel>() {}.type)
        }

        val saveButton: Button = view.findViewById(R.id.syncroButton)
        val saveIcon: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.ic_syncro)
        saveButton.setCompoundDrawablesWithIntrinsicBounds(saveIcon, null, null, null)
        saveButton.setBackgroundColor(Color.TRANSPARENT)
        saveButton.setOnClickListener {
            saveDataToFile()
            Toast.makeText(requireContext(), "Données sauvegardées.", Toast.LENGTH_SHORT).show()
        }

        //println("savedMovies: $savedMovies")


        // Liste des films à ajouter au tt début
        val newMovies = listOf(
            MovieModel(
                "Star Wars",
                "A rogue band of resistance fighters unite for a mission to steal the Death Star plans and bring a new hope to the galaxy.",
                "https://fr.web.img6.acsta.net/pictures/16/10/19/14/33/069648.jpg",
                true,
                "2016-12-14",
                "en",
                5.25
            ),
            MovieModel(
                "Your name",
                "High schoolers Mitsuha and Taki are complete strangers living separate lives. But one night, they suddenly switch places. Mitsuha wakes up in Taki’s body, and he in hers. This bizarre occurrence continues to happen randomly, and the two must adjust their lives around each other.",
                "https://fr.web.img2.acsta.net/pictures/16/12/12/13/49/295774.jpg",
                true,
                "2016-08-26",
                "en",
                8.523
            ),
            MovieModel(
                "Harry Potter",
                "Harry Potter has lived under the stairs at his aunt and uncle's house his whole life. But on his 11th birthday, he learns he's a powerful wizard—with a place waiting for him at the Hogwarts School of Witchcraft and Wizardry. As he learns to harness his newfound powers with the help of the school's kindly headmaster, Harry uncovers the truth about his parents' deaths—and about the villain who's to blame.",
                "https://fr.web.img2.acsta.net/pictures/18/07/02/17/25/3643090.jpg",
                true,
                "2001-11-16",
                "en",
                7.915
            ),
            MovieModel(
                "Avengers",
                "When an unexpected enemy emerges and threatens global safety and security, Nick Fury, director of the international peacekeeping agency known as S.H.I.E.L.D., finds himself in need of a team to pull the world back from the brink of disaster. Spanning the globe, a daring recruitment effort begins!",
                "https://fr.web.img3.acsta.net/medias/nmedia/18/85/31/58/20042068.jpg",
                true,
                "2012-04-25",
                "en",
                7.707
            ),
            MovieModel(
                "Ice Age",
                "Manny, Diego, and Sid embark upon another adventure after their continent is set adrift. Using an iceberg as a ship, they encounter sea creatures and battle pirates as they explore a new world.",
                "https://static.fnac-static.com/multimedia/Images/A8/A8/B1/47/4698536-1505-1505-1/tsp20180314141735/L-Age-De-Glace-4-AFFICHE-CINEMA-ORIGINALE.jpg",
                true,
                "2012-06-27",
                "en",
                6.352
            )
        )
        fetchTrendingMovies("en")
        //eviter de rajouter dans movieList les movies deja existant de savedMovies
        val moviesToAdd = savedMovies.filter { savedMovie ->
            !movieList.any { movie -> movie.name == savedMovie.name }
        }
        movieList.addAll(moviesToAdd)
        //println("moviesTOadd: $moviesToAdd")

        // Ajouter uniquement les films qui ne sont pas déjà présents dans la liste

        newMovies.forEach { movie ->
            val isMovieAlreadyAdded = movieList.any { it.name == movie.name }
            val isMovieInRemovedList = removedMoviesList.any { it.name == movie.name }

            if (!isMovieAlreadyAdded && !isMovieInRemovedList) {
                movieList.add(movie)
            }
        }


        horizontalRecyclerView = view.findViewById(R.id.horizontal_recycler_view)
        horizontalRecyclerView.adapter = MovieAdapter(inflater,context, movieList, R.layout.item_horizontal_movie, removedMoviesList)


        verticalRecyclerView = view.findViewById(R.id.vertical_recycler_view)
        verticalRecyclerView.adapter = MovieAdapter(inflater,context, trendingMoviesList, R.layout.item_vertical_movie, removedMoviesList)
        verticalRecyclerView.addItemDecoration(MovieItemDecoration())

        //println("ma moviesList: $movieList")

        return view
    }
    override fun onPause() {
        super.onPause()
        saveDataToFile()
    }

    private fun saveDataToFile() {
        // Sauvegarde des données dans un fichier texte
        val file = File(requireContext().filesDir, "donneesFilm.txt")
        val writer = BufferedWriter(FileWriter(file))
        val gson = Gson()
        // Utilisation d'un HashSet pour vérifier l'unicité des films
        val savedMovies = HashSet<MovieModel>()

        // Fusion des films de movieList et de l'objet compagnon
        val allMovies = movieList + MainActivity.movieList

        for (element in allMovies) {
            if (!savedMovies.contains(element)) {
                // Vérifier si le film n'est pas déjà présent dans le fichier
                val isMovieAlreadySaved = savedMovies.any { savedMovie ->
                    savedMovie.name == element.name
                }

                if (!isMovieAlreadySaved) {
                    savedMovies.add(element)
                    val movieString = gson.toJson(element)
                    writer.write(movieString)
                    writer.newLine()
                }
            }
        }
        writer.close()
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
        val searchIcon: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.ic_search)
        searchButton.setCompoundDrawablesWithIntrinsicBounds(searchIcon, null, null, null)
        searchButton.setBackgroundColor(Color.TRANSPARENT)


        searchButton.setOnClickListener {
            // Vérifier si un genre est sélectionné
            if (selectedGenreId != 0) {
                fetchMoviesByGenre(selectedGenreId)
            }
        }
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
            if (existingMovie == null && !removedMoviesList.contains(newMovie)) {
                movieList.add(newMovie)
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