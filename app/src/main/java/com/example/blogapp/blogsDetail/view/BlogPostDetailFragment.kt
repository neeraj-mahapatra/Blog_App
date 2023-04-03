package com.example.blogapp.blogsDetail.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.blogapp.MainActivity
import com.example.blogapp.R
import com.example.blogapp.databinding.FragmentBlogPostDetailBinding
import com.example.blogapp.databinding.FragmentHomeBinding
import com.example.blogapp.home.model.BlogPost
import com.example.blogapp.home.model.BlogPostAdapter
import com.example.blogapp.home.view.HomeFragment
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*


class BlogPostDetailFragment : Fragment() {

    private var _binding: FragmentBlogPostDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var blogTitle: TextView
    private lateinit var blogDescription: TextView
    private lateinit var blogCreator: TextView
    private lateinit var blogCreationDate: TextView
    private lateinit var blogImage: ImageView
    private lateinit var backImage: ImageView

    private val onClickListener = View.OnClickListener { view ->
        when (view) {
            backImage ->{
                replaceFragment(HomeFragment())
            }
        }
    }

    private fun inits() {
        blogTitle = binding.titleTextView
        blogDescription = binding.descriptionTextView
        blogCreator = binding.creatorTextView
        blogCreationDate = binding.createdAtTextView
        blogImage = binding.imageView
        backImage = binding.detailBlogBackButton
    }

    private fun setListeners() {
        backImage.setOnClickListener(onClickListener)
    }

    override fun onPause() {
        super.onPause()
        (activity as? MainActivity)?.resetMenu()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentBlogPostDetailBinding.inflate(inflater, container, false)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inits()
        setListeners()
        showBlogPost()

    }

    private fun showBlogPost() {
        val blogPost = arguments?.getParcelable<BlogPost>("blogPost")

        blogPost?.let {
            binding.titleTextView.text = it.title
            binding.creatorTextView.text = "By, ${it.creatorName}"
            binding.createdAtTextView.text =
                SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it.createdAt)
            Picasso.get().load(it.imageUrl).fit().into(binding.imageView)
            binding.descriptionTextView.text = it.description
        }

    }


    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

}