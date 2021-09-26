package com.example.materialdesign.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.materialdesign.R
import com.example.materialdesign.databinding.MainActivityBinding
import com.example.materialdesign.view.recycler.RecyclerActivity
import com.example.materialdesign.view.transitions.AnimationsActivity
import com.example.materialdesign.view.viewpager.ApiActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(ThemeHolder.theme)

        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Передача каждого идентификатора меню как набора идентификаторов, потому что каждый
        // пнкт меню следует рассматривать как пункты назначения верхнего уровня.
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.pictureOfTheDayFragment, R.id.navigation_settings)
        )

        //отображать заголовок на панели приложения на основе метки места назначения,кнопку вверх отображать не будем, т к есть кнопка "домой" внизу
        setupActionBarWithNavController(navController, appBarConfiguration)

        navView.setupWithNavController(navController)
    }

    //Action Bar с планетами
    override fun onCreateOptionsMenu(menu: Menu) : Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.action_bar, menu);
        return true;
    }

    //Action Bar с планетами
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar items
        return when (item.itemId) {
            R.id.planets -> {
                val planets = Intent(this, ApiActivity::class.java)
                startActivity(planets)
                true
            }
            R.id.animations -> {
                val animations = Intent(this, AnimationsActivity::class.java)
                startActivity(animations)
                true
            }
            R.id.recycler -> {
                val recycler = Intent(this, RecyclerActivity::class.java)
                startActivity(recycler)
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_CURRENT_THEME,ThemeHolder.theme)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        ThemeHolder.theme = savedInstanceState.getInt(KEY_CURRENT_THEME)
    }
    
    companion object {
        const val KEY_CURRENT_THEME = "current_theme"
    }

    object ThemeHolder{
        var theme = R.style.IndigoTheme
    }
}