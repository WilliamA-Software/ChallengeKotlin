package com.example.challengekotlin.data.model

import com.google.gson.annotations.SerializedName

data class MovieResponse (
    @SerializedName("page") val page: Int,
    @SerializedName("results") val movies: MutableList<Movie>,
    @SerializedName("total_results") val totalResults: Int,
    @SerializedName("total_pages") val totalPages: Int
)

data class Movie (
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("original_language") val originalLanguage: String,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("popularity") val popularity: Double,
    val genres: MutableList<Genre>,
    @SerializedName("poster_path") val poster: String?,          // Image
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("original_title") val originalTitle: String
)

data class Genre (
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)