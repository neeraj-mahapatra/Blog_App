package com.example.blogapp.home.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blogapp.MainActivity
import com.example.blogapp.R
import com.example.blogapp.blogsDetail.view.BlogPostDetailFragment
import com.example.blogapp.databinding.FragmentHomeBinding
import com.example.blogapp.home.model.BlogPost
import com.example.blogapp.home.model.BlogPostAdapter
import com.example.blogapp.login.view.LogInFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import java.util.*


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var logOutButton : ImageView
    private lateinit var userGreetings: TextView

    private lateinit var blogPostRecyclerView: RecyclerView
    private lateinit var blogPostAdapter: BlogPostAdapter

    private val onClickListener = View.OnClickListener { view ->
        when (view) {
            logOutButton -> {
                logOut()
            }
        }
    }

    private fun logOut() {
        FirebaseAuth.getInstance().signOut()
        replaceFragment(LogInFragment())
    }

    private fun inits() {
        logOutButton = binding.logOutButton
        userGreetings = binding.homeFragmentGreetings
        blogPostAdapter = BlogPostAdapter(emptyList())
        blogPostRecyclerView = binding.blogPostRecyclerView
    }

    private fun setListeners() {
        logOutButton.setOnClickListener(onClickListener)
    }

    override fun onPause() {
        super.onPause()
        (activity as? MainActivity)?.resetMenu()
    }

    override fun onResume() {
        super.onResume()
        showGreeting()
    }

    private fun showGreeting() {
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val uuid = currentUser?.uid ?: ""
        val greeting: String = getGreeting()

        val userRef = db.collection("users").document(uuid)
        userRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val name = document.getString("userName")
                    val message = "$greeting, $name"
                    userGreetings.text = message
                }
            }
            .addOnFailureListener { exception ->
                Log.d("message", "Error getting user document: ", exception)
            }
    }

    private fun getGreeting(): String {
        val cal = Calendar.getInstance()

        return when (cal.get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> "Good morning"
            in 12..15 -> "Good afternoon"
            else -> "Good evening"
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return (binding.root)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inits()
        setListeners()
        setDataToRecyclerView()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setDataToRecyclerView() {
        val db = FirebaseFirestore.getInstance()
        val usersRef = db.collection("users")
        val blogPosts = mutableListOf<BlogPost>()

        blogPostAdapter = BlogPostAdapter(blogPosts)
        blogPostRecyclerView.apply {
            adapter = blogPostAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(ItemOffsetDecoration(16))
        }

        blogPostAdapter.onItemClickListener = { blogPost ->
            val args = Bundle()
            args.putParcelable("blogPost", blogPost)
            val blogPostDetailFragment = BlogPostDetailFragment()
            blogPostDetailFragment.arguments = args
            replaceFragment(blogPostDetailFragment)
        }

        db.collection("blogs")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                result.map { document ->
                    val uid = document.getString("userId") ?: ""
                    val title = document.getString("title") ?: ""
                    val imageUrl = document.getString("imageUrl") ?: ""
                    val createdAt = document.getDate("createdAt") ?: Date()
                    val description = document.getString("description") ?: ""

                    usersRef.document(uid).get()
                        .addOnSuccessListener { userDocument ->
                            if (userDocument != null && userDocument.exists()) {
                                val creatorName = userDocument.getString("userName").toString()
                                val blogPost = BlogPost(
                                    title = title,
                                    imageUrl = imageUrl,
                                    creatorName = creatorName,
                                    createdAt = createdAt,
                                    description = description
                                )
                                blogPosts.add(blogPost)
                                blogPostAdapter.notifyDataSetChanged()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d(TAG, "Error getting document: ", exception)
                        }
                }
                blogPostAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents: ", exception)
            }
    }



    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        private const val TAG = "HomeFragment"
    }

}