package com.example.factory_news.api

data class Article(
    // Definiranje nepromjenjivih varijabli.
    val author: String,
    val description: String,
    val publishedAt: Any,
    val title: String,
    val url: String,
    val urlToImage: String
)