package com.example.factory_news.api

data class NewsJson(
    val articles: List<Article>,
    val sortBy: String,
    val source: String,
    val status: String
)