package com.example.blogapp.login.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.blogapp.MainActivity
import com.example.blogapp.R
import com.example.blogapp.databinding.FragmentHomeBinding
import com.example.blogapp.databinding.FragmentLogInBinding
import com.example.blogapp.home.view.HomeFragment
import com.example.blogapp.signup.view.SignUpFragment


class LogInFragment : Fragment() {

    private var _binding: FragmentLogInBinding? = null
    private val binding get() = _binding!!
    private lateinit var logIn_Text: TextView

    private val onClickListener = View.OnClickListener { view ->
        when (view) {
            logIn_Text -> {
                replaceFragment(SignUpFragment())
            }
        }
    }

    private fun inits() {
        logIn_Text = binding.loginText
    }

    private fun setListeners() {
        logIn_Text.setOnClickListener(onClickListener)
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).showMenuIcon(false)
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).showMenuIcon(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogInBinding.inflate(inflater, container, false)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inits()
        setListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        fun newInstance() = LogInFragment()
    }

}