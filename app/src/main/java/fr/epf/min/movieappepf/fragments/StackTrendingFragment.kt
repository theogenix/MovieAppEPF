package fr.epf.min.movieappepf.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.epf.min.movieappepf.R

class StackTrendingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Utilisation du fichier XML de mise en page "fragment_stack_trending"
        return inflater.inflate(R.layout.fragment_stack_trending, container, false)
    }

    override fun onResume() {
        super.onResume()
    }
}
