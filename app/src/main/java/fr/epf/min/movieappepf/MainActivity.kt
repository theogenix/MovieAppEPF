package fr.epf.min.movieappepf

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fr.epf.min.movieappepf.fragments.HomeFragment
import fr.epf.min.movieappepf.fragments.ResearchFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //injecter le fragment ds la boite fragment_container

        val transaction=supportFragmentManager.beginTransaction()

        // HomeFragment à remplacer par le fragment en question pour tester la page à tester
        //transaction.replace(R.id.fragment_container,HomeFragment(this))
        transaction.replace(R.id.fragment_container,ResearchFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }
}