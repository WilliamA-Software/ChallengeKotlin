package com.example.challengekotlin.ui.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.challengekotlin.R
import com.example.challengekotlin.data.model.Movie
import com.example.challengekotlin.data.webservice.OnItemMovieClickListener
import com.example.challengekotlin.ui.viewholder.MovieViewHolder
import java.util.stream.Collectors

class MovieAdapter(
    private val movieList: MutableList<Movie>,
    private val onMovieClickListener:OnItemMovieClickListener
    ): RecyclerView.Adapter<MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        // put the layout
        val layoutInflater = LayoutInflater.from(parent.context)

        return MovieViewHolder(layoutInflater.inflate(R.layout.item_movie, parent, false))
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val item = movieList[position]
        holder.render(item, onMovieClickListener)   //event onClick in each item
    }

    override fun getItemCount(): Int = movieList.size

}