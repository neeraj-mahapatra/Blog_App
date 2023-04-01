package com.example.blogapp.userDataCollection.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.blogapp.MainActivity
import com.example.blogapp.R
import com.example.blogapp.databinding.FragmentSignUpBinding
import com.example.blogapp.databinding.FragmentUserDataInputBinding
import com.example.blogapp.login.view.LogInFragment
import com.example.blogapp.signup.view.SignUpFragment
import com.google.firebase.auth.FirebaseAuth


class UserDataInputFragment : Fragment() {

    private var _binding: FragmentUserDataInputBinding? = null
    private val binding get() = _binding!!


    private val onClickListener = View.OnClickListener { view ->
        when (view) {

        }
    }

    private fun inits() {
    }

    private fun setListeners() {

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
        // Inflate the layout for this fragment
        _binding = FragmentUserDataInputBinding.inflate(inflater, container, false)
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
        fun newInstance() = UserDataInputFragment()
    }


}