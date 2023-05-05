package fr.epf.min.movieappepf.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import fr.epf.min.movieappepf.R
import fr.epf.min.movieappepf.adapter.MovieAdapter
import fr.epf.min.movieappepf.adapter.MovieItemDecoration

class HomeFragment : Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view=inflater?.inflate(R.layout.fragment_home,container,false)

        // recuperer le recycler view
        val horizontalRecyclerView= view!!.findViewById<RecyclerView>(R.id.horizontal_recycler_view)
        horizontalRecyclerView.adapter=MovieAdapter(R.layout.item_horizontal_movie)

        // recuperer le 2eme recycler view

        val verticalRecyclerView=view.findViewById<RecyclerView>(R.id.vertical_recycler_view)
        verticalRecyclerView.adapter=MovieAdapter(R.layout.item_vertical_movie)
        verticalRecyclerView.addItemDecoration(MovieItemDecoration())


        return view
    }



}