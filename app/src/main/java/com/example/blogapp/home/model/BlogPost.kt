package com.example.blogapp.home.model

import java.util.*

data class BlogPost(
    val title: String = "",
    val imageUrl: String = "",
    val creatorId: String = "",
    val creatorName: String = "",
    val createdAt: Date = Date()
)