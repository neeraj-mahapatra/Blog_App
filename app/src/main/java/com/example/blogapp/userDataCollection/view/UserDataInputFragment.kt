package com.example.blogapp.userDataCollection.view

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import com.example.blogapp.MainActivity
import com.example.blogapp.R
import com.example.blogapp.databinding.FragmentUserDataInputBinding
import com.example.blogapp.home.view.HomeFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage


class UserDataInputFragment : Fragment() {

    private var _binding: FragmentUserDataInputBinding? = null
    private val binding get() = _binding!!
    private lateinit var userImageView: ImageView
    private lateinit var userFullName: EditText
    private lateinit var userUserName: EditText
    private lateinit var userDescription: EditText
    private lateinit var userSubmitButton: Button
    private lateinit var storageRef: StorageReference
    private lateinit var db: FirebaseFirestore
    private var imageUri: Uri? = null // stores the selected image Uri
    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            // Handle the returned Uri here
            if (uri != null) {
                imageUri = uri
                userImageView.setImageURI(imageUri)
            }
        }

    private val onClickListener = View.OnClickListener { view ->
        when (view) {
            userImageView -> {
                //here user can upload image
                selectImage()
            }
            userSubmitButton -> {
                //collect all data and send to the firestore
                val fullName = userFullName.text.toString().trim()
                val userName = userUserName.text.toString().trim()
                val description = userDescription.text.toString().trim()
                uploadUserDataToFirestore(fullName, userName, description)
            }
        }
    }

    private fun inits() {
        userImageView = binding.userInputImage
        userFullName = binding.userInputFullName
        userUserName = binding.userInputUserName
        userDescription = binding.userInputDesc
        userSubmitButton = binding.submitButton
        storageRef = Firebase.storage.reference.child("users/${Firebase.auth.currentUser?.uid}/profile_image")
        db = Firebase.firestore
    }

    private fun setListeners() {
        userImageView.setOnClickListener(onClickListener)
        userSubmitButton.setOnClickListener(onClickListener)
    }

    private fun selectImage() {
        pickImage.launch("image/*")
    }

    private fun uploadUserDataToFirestore(fullName: String, userName: String, description: String) {
        if (imageUri == null || imageUri!!.path == null) {
            uploadUserData(fullName, userName, description, null)
        } else {
            storageRef.putFile(imageUri!!)
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    storageRef.downloadUrl
                }
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val imageUrl = task.result.toString()
                        uploadUserData(fullName, userName, description, imageUrl)
                    } else {
                        Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun uploadUserData(fullName: String, userName: String, description: String, imageUrl: String?) {
        val user = hashMapOf(
            "fullName" to fullName,
            "userName" to userName,
            "description" to description,
            "userImage" to imageUrl
        )

        val uid = Firebase.auth.currentUser?.uid ?: return

        db.collection("users").document(uid)
            .set(user, SetOptions.merge()) // merge with existing document
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Data saved successfully", Toast.LENGTH_SHORT).show()
                replaceFragment(HomeFragment())
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to save data", Toast.LENGTH_SHORT).show()
            }
    }
    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
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

}