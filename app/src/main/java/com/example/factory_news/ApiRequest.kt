package com.example.factory_news

import com.example.factory_news.api.NewsJson
import retrofit2.http.GET

interface ApiRequest {

    // Dio linka unutar kojeg su pohranjeni podatci (bbc_news).
    @GET("v1/articles?source=bbc-news&sortBy=top&apiKey=6946d0c07a1c4555a4186bfcade76398")
    suspend fun getNews() : NewsJson
}