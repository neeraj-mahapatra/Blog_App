package com.example.blogapp.home.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.blogapp.R
import com.example.blogapp.databinding.FragmentHomeBinding
import com.example.blogapp.login.view.LogInFragment
import com.example.blogapp.splash.SplashFragment
import com.google.firebase.auth.FirebaseAuth


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var logOut_Button : Button

    private val onClickListener = View.OnClickListener { view ->
        when (view) {
            logOut_Button -> {
                FirebaseAuth.getInstance().signOut()
                replaceFragment(LogInFragment())
            }
        }
    }

    private fun inits() {
        logOut_Button = binding.logOutButton
    }

    private fun setListeners() {
        logOut_Button.setOnClickListener(onClickListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inits()
        setListeners()
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    companion object {
        fun newInstance() = HomeFragment()
    }

}