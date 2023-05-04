package fr.epf.min.movieappepf

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fr.epf.min.movieappepf.fragments.HomeFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //injecter le fragment ds la boite fragment_container

        val transaction=supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container,HomeFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }
}