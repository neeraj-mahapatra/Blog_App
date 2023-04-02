package com.example.blogapp.createBlog.view

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.blogapp.MainActivity
import com.example.blogapp.R
import com.example.blogapp.databinding.FragmentCreateBlogBinding
import com.example.blogapp.home.view.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class CreateBlogFragment : Fragment() {

    private var _binding: FragmentCreateBlogBinding? = null
    private val binding get() = _binding!!

    private lateinit var blogTitle: EditText
    private lateinit var blogDescription: EditText
    private lateinit var blogSelectImageButton: Button
    private lateinit var blogSelectedImage: ImageView
    private lateinit var blogSubmitButton: Button
    private lateinit var blogBackButton: ImageView
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private var selectedImageUri: Uri? = null

    private val onClickListener = View.OnClickListener { view ->
        when (view) {
            blogSelectImageButton -> {
                selectImage()
            }
            blogSubmitButton -> {
                onSubmit()
            }
            blogBackButton -> {
                replaceFragment(HomeFragment())
            }
        }
    }

    private fun onSubmit() {
        // Get the blog details from the EditTexts and ImageView
        val title = blogTitle.text.toString()
        val description = blogDescription.text.toString()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Generate a new document ID for the blog post
        val blogId = FirebaseFirestore.getInstance().collection("blogs").document().id

        // Upload the image to Firebase Storage
        val storageRef = FirebaseStorage.getInstance().reference.child("blog_images/$blogId")
        val uploadTask = selectedImageUri?.let { storageRef.putFile(it) }
        uploadTask?.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            // Retrieve the download URL of the uploaded image
            storageRef.downloadUrl
        }?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUrl = task.result.toString()

                // Create a new blog post document in the "blogs" collection
                val blogData = hashMapOf(
                    "title" to title,
                    "description" to description,
                    "imageUrl" to downloadUrl,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "userId" to userId
                )
                FirebaseFirestore.getInstance().collection("blogs").document(blogId)
                    .set(blogData)
                    .addOnSuccessListener {
                        // Blog post created successfully
                        Toast.makeText(requireContext(), "Blog post created!", Toast.LENGTH_SHORT).show()
                        // Navigate to the user's blog post list
                        replaceFragment(HomeFragment())
                    }
                    .addOnFailureListener { e ->
                        // Error creating blog post
                        Toast.makeText(requireContext(), "Error creating blog post: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Error uploading image
                Toast.makeText(requireContext(), "Error uploading image: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun selectImage() {
        pickImageLauncher.launch("image/*")
    }

    private fun inits() {
        blogTitle = binding.userCreateBlogPostTitle
        blogDescription = binding.userCreateBlogPostDescription
        blogSelectImageButton = binding.userCreateBlogPostSelectImageButton
        blogSelectedImage = binding.userCreateBlogPostSelectedImagePreview
        blogSubmitButton = binding.userCreateBlogPostSubmitButton
        blogBackButton = binding.userCreateBlogPostBackButton

    }

    private fun setListeners() {
        blogSelectImageButton.setOnClickListener(onClickListener)
        blogSubmitButton.setOnClickListener(onClickListener)
        blogBackButton.setOnClickListener(onClickListener)
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.resetMenu()
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                blogSelectedImage.setImageURI(selectedImageUri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateBlogBinding.inflate(inflater, container, false)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inits()
        setListeners()
    }
}