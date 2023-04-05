package com.example.blogapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.blogapp.createBlog.view.CreateBlogFragment
import com.example.blogapp.databinding.ActivityMainBinding
import com.example.blogapp.profile.view.ProfileFragment
import com.example.blogapp.splash.SplashFragment
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

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
            menuItem1 -> {
                replaceFragment(ProfileFragment())
            }
            menuItem2 -> {
                replaceFragment(CreateBlogFragment())
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
        menuItem1.setOnClickListener(onClickListener)
        menuItem2.setOnClickListener(onClickListener)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseRemoteConfigSetUp()
        inits()



        setListeners()
        replaceFragment(SplashFragment())


    }

    private fun firebaseRemoteConfigSetUp() {
        FirebaseApp.initializeApp(this)
        val remoteConfig = FirebaseRemoteConfig.getInstance()

        // Set default values for Remote Config parameters
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

    }


    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun showMenuIcon(show: Boolean) {
        menuIcon.visibility = if (show) View.VISIBLE else View.GONE
    }

    fun resetMenu() {
        isExpanded = false
        menuItem1.visibility = View.GONE
        menuItem2.visibility = View.GONE
        menuIcon.animate().rotation(0f).start()
    }

}