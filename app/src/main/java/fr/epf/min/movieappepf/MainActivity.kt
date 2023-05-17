package fr.epf.min.movieappepf

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import fr.epf.min.movieappepf.fragments.HomeFragment
import fr.epf.min.movieappepf.fragments.ResearchFragment
import android.util.Log


class MainActivity : AppCompatActivity() {
    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navigationView = findViewById<BottomNavigationView>(R.id.navigation_view)
        navigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_page -> {
                    loadFragment(HomeFragment(this))
                    true
                }
                R.id.search_bar -> {
                    loadFragment(ResearchFragment())
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
