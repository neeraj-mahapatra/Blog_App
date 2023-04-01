package com.example.blogapp.signup.view

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
import com.example.blogapp.databinding.FragmentSignUpBinding
import com.example.blogapp.login.view.LogInFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var signUp_Text: TextView
    private lateinit var signUp_Email: EditText
    private lateinit var signUp_Password: EditText
    private lateinit var signUp_Button: Button

    private val onClickListener = View.OnClickListener { view ->
        when (view) {
            signUp_Text -> {
                replaceFragment(LogInFragment())
            }
            signUp_Button -> {
                val email = signUp_Email.text.toString()
                val password = signUp_Password.text.toString()

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // User created successfully
                            Log.d("message", "registered successfully")

                            // Add user data to Firestore
                            val user = FirebaseAuth.getInstance().currentUser
                            val userData = hashMapOf(
                                "email" to user?.email,
                                "uid" to user?.uid,
                                "status" to false,
                                "fullName" to "",
                                "userName" to "",
                                "description" to "",
                                "userImage" to ""
                            )
                            val db = FirebaseFirestore.getInstance()
                            db.collection("users").document(user?.uid ?: "")
                                .set(userData)
                                .addOnSuccessListener {
                                    Log.d("message", "User data added to Firestore")
                                    replaceFragment(LogInFragment())
                                }
                                .addOnFailureListener { e ->
                                    Log.w("message", "Error adding user data to Firestore", e)
                                }
                        } else {
                            // User creation failed
                            Log.d("message", "not registered successfully")
                        }
                    }
            }

        }
    }

    private fun inits() {
        signUp_Text = binding.signUpText
        signUp_Email = binding.signUpEmail
        signUp_Password = binding.signUpPassword
        signUp_Button = binding.signUpButton
        signUp_Button.isEnabled = false
    }

    private fun setListeners() {
        signUp_Text.setOnClickListener(onClickListener)
        signUp_Button.setOnClickListener(onClickListener)
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
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inits()
        setListeners()
        makeTheSignUpButtonInActiveAtFirst()
    }

    private fun makeTheSignUpButtonInActiveAtFirst() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                signUp_Button.isEnabled =
                    signUp_Email.text.isNotEmpty() && signUp_Password.text.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        signUp_Email.addTextChangedListener(textWatcher)
        signUp_Password.addTextChangedListener(textWatcher)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        fun newInstance() = SignUpFragment()
    }

}