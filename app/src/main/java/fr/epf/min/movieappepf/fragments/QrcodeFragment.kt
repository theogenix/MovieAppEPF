package fr.epf.min.movieappepf.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import fr.epf.min.movieappepf.Movie
import fr.epf.min.movieappepf.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CompoundBarcodeView

class QrcodeFragment: Fragment() {
    private lateinit var scannerView: CompoundBarcodeView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_qrcode, container, false)
        scannerView = view.findViewById(R.id.cameraPreview)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val scanButton = view.findViewById<Button>(R.id.scanButton)
        scanButton.setOnClickListener {
            if (hasCameraPermission()) {
                startCamera()
            } else {
                requestCameraPermission()
            }
        }
    }
    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    private fun startCamera() {
        scannerView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                result?.let {
                    if (isAdded && !isRequestingMovieDetails) {
                        isRequestingMovieDetails = true
                        val movieId = result.text.toIntOrNull()
                        if (movieId != null) {
                            fetchMovieDetails(movieId)
                        } else {
                            Toast.makeText(requireContext(), "Invalid QR Code", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            override fun possibleResultPoints(resultPoints: List<ResultPoint>?) {
            }
        })
        scannerView.setStatusText("")
        scannerView.resume()
    }


    override fun onResume() {
        super.onResume()
        if (hasCameraPermission()) {
            startCamera()
        } else {
            requestCameraPermission()
        }
    }

    override fun onPause() {
        super.onPause()
        scannerView.pause()
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 123
    }
    private var isRequestingMovieDetails = false

    private fun fetchMovieDetails(movieId: Int) {
        val apiKey = "6b20b9e496710f84a435a42ec1086350"
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val movieDetails = retrofit.create(ResearchFragment.MovieApiService::class.java)
        val call = movieDetails.getMovieDetails(movieId, apiKey)
        call.enqueue(object : Callback<Movie> {
            override fun onResponse(call: Call<Movie>, response: Response<Movie>) {
                if (response.isSuccessful) {
                    val movie = response.body()
                    if (movie != null) {
                        showQrcodeMovieDetails(movie)
                    }
                } else {
                    Log.e("QrcodeFragment", "Erreur de réponse de l'API: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<Movie>, t: Throwable) {
                Log.e("QrcodeFragment", "Erreur lors de l'exécution de la requête de recherche: ${t.message}")
            }
        })
    }

    private fun showQrcodeMovieDetails(movie: Movie) {
        val stackQrcodeFragment = StackQrcodeFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, stackQrcodeFragment)
            .commit()
        val newResultFragment = ResultFragment()
        val bundle = createMovieBundle(movie)
        newResultFragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .add(R.id.stackQrcodeContainer, newResultFragment)
            .commit()
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
 }


