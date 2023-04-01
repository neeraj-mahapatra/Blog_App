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
import android.widget.Toast
import com.example.blogapp.MainActivity
import com.example.blogapp.R
import com.example.blogapp.databinding.FragmentLogInBinding
import com.example.blogapp.home.view.HomeFragment
import com.example.blogapp.signup.view.SignUpFragment
import com.example.blogapp.userDataCollection.view.UserDataInputFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pranavpandey.android.dynamic.toasts.DynamicToast

class LogInFragment : Fragment() {

    private var _binding: FragmentLogInBinding? = null
    private val binding get() = _binding!!
    private lateinit var logInText: TextView
    private lateinit var logInEmail: EditText
    private lateinit var logInPassword: EditText
    private lateinit var logInButton: Button

    private val onClickListener = View.OnClickListener setOnClickListener@{ view ->
        when (view) {
            logInText -> {
                onLogInTextClicked()
            }
            logInButton -> {
                onLogInButtonClicked()
            }
        }
    }


    private fun inits() {
        logInText = binding.loginText
        logInEmail = binding.logInEmail
        logInPassword = binding.logInPassword
        logInButton = binding.loginButton
        logInButton.isEnabled = false
    }

    private fun setListeners() {
        logInText.setOnClickListener(onClickListener)
        logInButton.setOnClickListener(onClickListener)
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
    private fun onLogInButtonClicked() {
        val email = binding.logInEmail.text.toString()
        val password = binding.logInPassword.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            activity?.let { it1 ->
                DynamicToast.makeError(
                    it1,
                    "Please fill in both email and password fields to continue.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // User logged in successfully
                    val uid = FirebaseAuth.getInstance().currentUser?.uid
                    val userDocRef = FirebaseFirestore.getInstance().collection("users").document(uid!!)
                    userDocRef.get().addOnSuccessListener { documentSnapshot ->
                        val status = documentSnapshot.getBoolean("status")
                        if (status == false) {
                            // Update user status to true in Firestore
                            userDocRef.update("status", true)
                                .addOnSuccessListener {
                                    Log.d("message", "User status updated successfully")
                                }
                                .addOnFailureListener { exception ->
                                    Log.d("message", "Error updating user status: ${exception.message}")
                                }
                            replaceFragment(UserDataInputFragment())
                        } else {
                            replaceFragment(HomeFragment())
                        }
                    }.addOnFailureListener { exception ->
                        // Error retrieving user document
                        Log.d("message", "Error retrieving user document: ${exception.message}")
                    }
                }
                else {
                    Log.d("message", "login fail")
                    val message = task.exception?.message ?: "An unknown error occurred."
                    when {
                        message.contains("no user record") -> {
                            activity?.let { it1 ->
                                DynamicToast.makeError(
                                    it1,
                                    "The email entered is not registered. Please sign up or try again with a registered email.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        message.contains("password is invalid") -> {
                            activity?.let { it1 ->
                                DynamicToast.makeError(
                                    it1,
                                    "The password entered is incorrect. Please try again with the correct password.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        else -> {
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
    }

    private fun onLogInTextClicked() {
        replaceFragment(SignUpFragment())
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).showMenuIcon(false)
        (activity as? MainActivity)?.resetMenu()
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
                logInButton.isEnabled = logInEmail.text.isNotEmpty() && logInPassword.text.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        logInEmail.addTextChangedListener(textWatcher)
        logInPassword.addTextChangedListener(textWatcher)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        fun newInstance() = LogInFragment()
    }

}