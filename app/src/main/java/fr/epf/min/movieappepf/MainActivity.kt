package fr.epf.min.movieappepf

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import fr.epf.min.movieappepf.fragments.ResearchFragment
import android.util.Log
import androidx.fragment.app.FragmentManager
import fr.epf.min.movieappepf.fragments.HomeFragment
import fr.epf.min.movieappepf.fragments.QrcodeFragment


class MainActivity : AppCompatActivity() {

    //movieList en variable statique
    companion object {
        val movieList = arrayListOf<MovieModel>()
    }
    private lateinit var homeFragment: HomeFragment

    private fun loadFragment(fragment: Fragment) {
        Log.d("MainActivity", "Loading fragment: ${fragment.javaClass.simpleName}")
        val fragmentManager = supportFragmentManager
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navigationView = findViewById<BottomNavigationView>(R.id.navigation_view)
        navigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_page -> {
                    if (::homeFragment.isInitialized) {
                        homeFragment.updateMovieList(movieList)
                        loadFragment(homeFragment)
                        println("Les compagnions: $movieList")
                        println("il passe par update")
                    } else {
                        homeFragment = HomeFragment(this)
                        loadFragment(homeFragment)
                    }
                    true

                }
                R.id.search_bar -> {
                    loadFragment(ResearchFragment())
                    true
                }
                R.id.qr_code -> {
                    loadFragment(QrcodeFragment())
                    true
                }
                else ->{
                    Log.e("MainActivity", "Fragment not found for item ID: ${item.itemId}")
                    false
                }
            }
        }
        loadFragment(HomeFragment(this))
    }
}
