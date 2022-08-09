package com.example.challengekotlin.ui.viewholder

import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.challengekotlin.R
import com.example.challengekotlin.data.model.Movie
import com.example.challengekotlin.data.webservice.OnItemMovieClickListener
import com.example.challengekotlin.databinding.ItemMovieBinding

class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemMovieBinding.bind(view)

    fun render(movieModel: Movie, onMovieClickListener:OnItemMovieClickListener){
        binding.tvTitleMovie.text = movieModel.title

        Glide.with(binding.ivPictureMovie.context)
            .load("https://image.tmdb.org/t/p/w300${movieModel.poster}")
            .into(binding.ivPictureMovie)

        itemView.setOnClickListener { onMovieClickListener.onMovieItemClick(movieModel.id) }
    }
}