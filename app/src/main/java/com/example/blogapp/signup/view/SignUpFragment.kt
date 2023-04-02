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
import com.pranavpandey.android.dynamic.toasts.DynamicToast


class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var signUpText: TextView
    private lateinit var signUpEmail: EditText
    private lateinit var signUpPassword: EditText
    private lateinit var signUpButton: Button

    private val onClickListener = View.OnClickListener { view ->
        when (view) {
            signUpText -> {
                onSignUpTextClicked()
            }
            signUpButton -> {
                onSignUpButtonClicked()
            }

        }
    }

    private fun onSignUpTextClicked() {
        replaceFragment(LogInFragment())
    }

    private fun onSignUpButtonClicked() {
        val email = signUpEmail.text.toString()
        val password = signUpPassword.text.toString()

        if (email.isEmpty()) {
            activity?.let {
                DynamicToast.makeError(it, R.string.error_email_empty.toString())
                    .show()
            }
        }

        if (password.isEmpty()) {
            activity?.let {
                DynamicToast.makeError(it, R.string.error_password_empty.toString())
                    .show()
            }
        }

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
                            context?.let { it1 -> DynamicToast.makeSuccess(it1, "Signed Up Successfully...").show() }
                            replaceFragment(LogInFragment())
                        }
                        .addOnFailureListener { e ->
                            Log.w("message", "Error adding user data to Firestore", e)
                        }
                } else {
                    Log.d("message", "not registered successfully")
                    val errorMsg = when {
                        task.exception?.message?.contains("WEAK_PASSWORD") == true ->
                            R.string.error_weak_password.toString()
                        task.exception?.message?.contains("EMAIL_EXISTS") == true ->
                            R.string.error_user_exists.toString()
                        task.exception?.message?.contains("INVALID_EMAIL") == true ||
                                task.exception?.message?.contains("MISSING_EMAIL") == true ->
                            R.string.error_auth_failed.toString()
                        else ->
                            task.exception?.message ?: getString(R.string.error_auth_failed)
                    }
                    activity?.let { DynamicToast.makeError(it, errorMsg).show() }
                }
            }
    }

    private fun inits() {
        signUpText = binding.signUpText
        signUpEmail = binding.signUpEmail
        signUpPassword = binding.signUpPassword
        signUpButton = binding.signUpButton
        signUpButton.isEnabled = false
    }

    private fun setListeners() {
        signUpText.setOnClickListener(onClickListener)
        signUpButton.setOnClickListener(onClickListener)
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
        (activity as? MainActivity)?.resetMenu()
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
                signUpButton.isEnabled =
                    signUpEmail.text.isNotEmpty() && signUpPassword.text.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        signUpEmail.addTextChangedListener(textWatcher)
        signUpPassword.addTextChangedListener(textWatcher)
    }
}