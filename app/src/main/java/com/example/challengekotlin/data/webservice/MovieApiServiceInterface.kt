package com.example.challengekotlin.data.webservice

import com.example.challengekotlin.data.model.Movie
import com.example.challengekotlin.data.model.MovieResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface MovieApiServiceInterface {
    @GET
    suspend fun getMovies(@Url url: String): Response<MovieResponse>

    @GET
    suspend fun getDetails(@Url url: String): Response<Movie>
}