package com.example.blogapp.profile.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.blogapp.MainActivity
import com.example.blogapp.R
import com.example.blogapp.databinding.FragmentProfileBinding
import com.example.blogapp.home.view.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var userName: TextView
    private lateinit var userFullName: TextView
    private lateinit var userDesc: TextView
    private lateinit var userImage: ImageView
    private lateinit var backImageView: ImageView


    private val onClickListener = View.OnClickListener { view ->
        when (view) {
            backImageView -> {
                replaceFragment(HomeFragment())
            }
        }
    }
    private fun inits() {
        userName = binding.userProfileUserName
        userFullName = binding.userProfileFullName
        userDesc = binding.userProfileDescription
        userImage = binding.userProfileImage
        backImageView = binding.userProfileBackButton
    }

    private fun setListeners() {
        backImageView.setOnClickListener(onClickListener)
    }

    override fun onPause() {
        super.onPause()
        (activity as? MainActivity)?.resetMenu()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inits()
        setListeners()

        val auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            val uid = auth.currentUser!!.uid

            val db = FirebaseFirestore.getInstance()
            val usersCollection = db.collection("users")

            usersCollection.whereEqualTo("uid", uid).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val username = document.getString("userName")
                        val fullName = document.getString("fullName")
                        val description = document.getString("description")
                        val imageUrl = document.getString("userImage")

                        userName.text = username
                        userFullName.text = fullName
                        userDesc.text = description

                        Glide.with(this)
                            .load(imageUrl)
                            .transform(CircleCrop())
                            .into(userImage)
                    }
                }
                .addOnFailureListener {
                    Log.d("message", "Unable to get data")
                }

        }
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}