package fr.epf.min.movieappepf.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.epf.min.movieappepf.R

class StackFragment : Fragment() {
    var fragmentCount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Utilisation du fichier XML de mise en page "fragment_stack"
        return inflater.inflate(R.layout.fragment_stack, container, false)
    }

    override fun onResume() {
        super.onResume()
        Log.d("StackFragment", "Nombre de fragments ajoutés : $fragmentCount")
    }
    fun incrementFragmentCount() {
        fragmentCount++
    }
}
