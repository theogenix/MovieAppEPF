package fr.epf.min.movieappepf.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import fr.epf.min.movieappepf.R
import fr.epf.min.movieappepf.adapter.MovieAdapter

class HomeFragment : Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view=inflater?.inflate(R.layout.fragment_home,container,false)

        // recuperer le recycler view
        val horizontalRecyclerView= view!!.findViewById<RecyclerView>(R.id.horizontal_recycler_view)
        horizontalRecyclerView.adapter=MovieAdapter()
        return view
    }



}