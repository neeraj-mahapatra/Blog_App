package com.example.blogapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.blogapp.databinding.ActivityMainBinding
import com.example.blogapp.splash.SplashFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var menuIcon: ImageView
    private lateinit var menuItem1: ImageView
    private lateinit var menuItem2: ImageView
    private var isExpanded = false

    private val onClickListener = View.OnClickListener { view ->
        when (view) {
            menuIcon -> {
                if (isExpanded) {
                    // Collapse the menu
                    menuItem1.visibility = View.GONE
                    menuItem2.visibility = View.GONE
                    menuIcon.animate().rotation(0f).start()
                } else {
                    // Expand the menu
                    menuItem1.visibility = View.VISIBLE
                    menuItem2.visibility = View.VISIBLE
                    menuIcon.animate().rotation(180f).start()
                }
                isExpanded = !isExpanded
            }
        }
    }

    private fun inits() {
        menuIcon = binding.menuIcon
        menuItem1 = binding.menuItem1
        menuItem2 = binding.menuItem2
        menuItem1.visibility = View.GONE
        menuItem2.visibility = View.GONE
    }

    private fun setListeners() {
        menuIcon.setOnClickListener(onClickListener)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        inits()
        setListeners()
        replaceFragment(SplashFragment())
    }


    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun showMenuIcon(show: Boolean) {
        menuIcon.visibility = if (show) View.VISIBLE else View.GONE
    }
}