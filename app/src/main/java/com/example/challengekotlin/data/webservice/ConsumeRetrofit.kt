package com.example.challengekotlin.data.webservice

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ConsumeRetrofit {

    companion object{
        fun getRetrofit(): Retrofit {
            return Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/movie/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}