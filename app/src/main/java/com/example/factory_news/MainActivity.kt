package com.example.factory_news

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.factory_news.adapters.RecyclerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://newsapi.org/"

class MainActivity : AppCompatActivity() {

    // Definiranje varijabli.
    lateinit var countdownTimer: CountDownTimer
    private var seconds = 5L

    private var titlesList = mutableListOf<String>()
    private var descList = mutableListOf<String>()
    private var imagesList = mutableListOf<String>()
    private var linksList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        makeAPIRequest()
    }

    // // Jednostavan fade u animaciji kada  aplikacija završi s učitavanjem podataka.
    private fun fadeIn() {
        whiteScreen.animate().apply {
            alpha(0f)
            duration = 3000
        }.start()
    }

    // Zahtijeva podatke s API-a i prosljeđuje ih u Recycler View.
    private fun makeAPIRequest() {

        progressBar.visibility = View.VISIBLE

        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiRequest::class.java)

        GlobalScope.launch(Dispatchers.IO) {

            try {
                val response = api.getNews()

                for (article in response.articles) {
                    Log.d("MainActivity", "Rezultat + $article")
                    addToList(article.title, article.description, article.urlToImage, article.url)
                }

                // U trenutku kada su podatci dohvaćeni UI se ažurira.
                withContext(Dispatchers.Main) {
                    setUpRecyclerView()
                    fadeIn()
                    progressBar.visibility = View.GONE
                }

            } catch (e: Exception) {
                Log.d("MainActivity", e.toString())
                withContext(Dispatchers.Main) {
                    attemptRequestAgain()

                }
            }

        }
    }

    private fun attemptRequestAgain() {

        countdownTimer = object: CountDownTimer(5*1000,1000){
            override fun onFinish() {
                makeAPIRequest()
                countdownTimer.cancel()
                noInternet.visibility = View.GONE
                this@MainActivity.seconds+=5
            }

            override fun onTick(millisUntilFinished: Long) {
                noInternet.visibility = View.VISIBLE
                noInternet.text = "Ups, došlo je do pogreške...\nPokušajte ponovo za: ${millisUntilFinished/1000}"
                Log.d("MainActivity", "Ups, došlo je do pogreške. Pokušajte ponovo za ${millisUntilFinished/1000} sekundi.")
            }
        }

        countdownTimer.start()
    }

    private fun setUpRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = RecyclerAdapter(titlesList, descList, imagesList, linksList)
    }

    // Dodaje stavke u Recycler View.
    private fun addToList(title: String, description: String, image: String, link: String) {
        linksList.add(link)
        titlesList.add(title)
        descList.add(description)
        imagesList.add(image)
    }
}
