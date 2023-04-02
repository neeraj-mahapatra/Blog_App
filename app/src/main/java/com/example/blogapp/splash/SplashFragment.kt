package com.example.blogapp.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.example.blogapp.MainActivity
import com.example.blogapp.R
import com.example.blogapp.databinding.FragmentSplashBinding
import com.example.blogapp.login.view.LogInFragment


class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private lateinit var imageView: ImageView
    private lateinit var menuIcon: ImageView

    private fun inits() {
        imageView = binding.splashImage
        menuIcon = (context as MainActivity).findViewById(R.id.menu_icon)
    }

    private fun setListeners() {

    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun splashAnimation() {
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.splash_animation)
        imageView.startAnimation(animation)
        Handler(Looper.getMainLooper()).postDelayed({
            replaceFragment(LogInFragment())
        }, animation.duration)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSplashBinding.inflate(inflater, container, false)

        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inits()
        menuIcon.visibility = View.GONE
        setListeners()
        splashAnimation()
    }

}