package com.example.blogapp.login.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.blogapp.databinding.FragmentLogInBinding
import com.example.blogapp.home.view.HomeFragment
import com.example.blogapp.signup.view.SignUpFragment
import com.example.blogapp.userDataCollection.view.UserDataInputFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class LogInFragment : Fragment() {

    private var _binding: FragmentLogInBinding? = null
    private val binding get() = _binding!!
    private lateinit var logIn_Text: TextView
    private lateinit var logIn_Email: EditText
    private lateinit var logIn_Password: EditText
    private lateinit var logIn_Button: Button

    private val onClickListener = View.OnClickListener { view ->
        when (view) {
            logIn_Text -> {
                replaceFragment(SignUpFragment())
            }
            logIn_Button -> {
                val email = binding.logInEmail.text.toString()
                val password = binding.logInPassword.text.toString()

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // User logged in successfully
                            val uid = FirebaseAuth.getInstance().currentUser?.uid
                            val userDocRef = FirebaseFirestore.getInstance().collection("users").document(uid!!)
                            userDocRef.get().addOnSuccessListener { documentSnapshot ->
                                val status = documentSnapshot.getBoolean("status")
                                if (status == false) {
                                    replaceFragment(UserDataInputFragment())
                                    // Update user status to true in Firestore
                                    userDocRef.update("status", true)
                                        .addOnSuccessListener {
                                            Log.d("message", "User status updated successfully")
                                        }
                                        .addOnFailureListener { exception ->
                                            Log.d("message", "Error updating user status: ${exception.message}")
                                        }
                                } else {
                                    replaceFragment(HomeFragment())
                                }
                            }.addOnFailureListener { exception ->
                                // Error retrieving user document
                                Log.d("message", "Error retrieving user document: ${exception.message}")
                            }
                        } else {
                            // Login failed
                            Log.d("message", "login fail")
                        }
                    }
            }

        }
    }

    private fun inits() {
        logIn_Text = binding.loginText
        logIn_Email = binding.logInEmail
        logIn_Password = binding.logInPassword
        logIn_Button = binding.loginButton
        logIn_Button.isEnabled = false
    }

    private fun setListeners() {
        logIn_Text.setOnClickListener(onClickListener)
        logIn_Button.setOnClickListener(onClickListener)
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Check if user is already logged in
        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser!=null) {
            replaceFragment(HomeFragment())
        }
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
        makeTheLogInButtonInActiveAtFirst()
    }

    private fun makeTheLogInButtonInActiveAtFirst() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                logIn_Button.isEnabled = logIn_Email.text.isNotEmpty() && logIn_Password.text.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        logIn_Email.addTextChangedListener(textWatcher)
        logIn_Password.addTextChangedListener(textWatcher)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        fun newInstance() = LogInFragment()
    }

}