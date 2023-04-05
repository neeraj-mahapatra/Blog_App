package com.example.blogapp.home.model

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blogapp.R
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class BlogPostAdapter(private var blogPosts: List<BlogPost>) :
    RecyclerView.Adapter<BlogPostAdapter.ViewHolder>() {

    var onItemClickListener: ((BlogPost) -> Unit)? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val creatorTextView: TextView = itemView.findViewById(R.id.creatorTextView)
        val createdAtTextView: TextView = itemView.findViewById(R.id.createdAtTextView)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.blog_post_item, parent, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val blogPost = blogPosts[position]
        holder.titleTextView.text = blogPost.title
        holder.creatorTextView.text = "By, ${blogPost.creatorName}"
        holder.createdAtTextView.text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            .format(blogPost.createdAt)
        Picasso.get().load(blogPost.imageUrl).fit().into(holder.imageView)

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(blogPost)
        }
    }

    override fun getItemCount(): Int {
        return blogPosts.size
    }
}