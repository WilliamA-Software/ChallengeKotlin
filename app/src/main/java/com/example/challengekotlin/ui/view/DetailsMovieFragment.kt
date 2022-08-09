package com.example.challengekotlin.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.challengekotlin.MainActivity
import com.example.challengekotlin.R
import com.example.challengekotlin.core.Constants
import com.example.challengekotlin.data.model.Genre
import com.example.challengekotlin.data.webservice.MovieApiServiceInterface
import com.example.challengekotlin.databinding.FragmentDetailsMovieBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * The [DetailsMovieFragment]
 * shows the movie's details .
 */

class DetailsMovieFragment : Fragment() {

    private lateinit var binding: FragmentDetailsMovieBinding
    private var mActivity: MainActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentDetailsMovieBinding.inflate(inflater, container, false)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar()
        // link Actionbar control
        setHasOptionsMenu(true)

        val idFromOther = arguments?.getInt("key_id")
        if (idFromOther != null) {
            searchDetails(idFromOther, binding)
        }else{
            Toast.makeText(binding.ivPosterMovie.context,
                "Error with Fragment Transaction",
                Toast.LENGTH_SHORT).show()
        }
    }


    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/movie/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun searchDetails(query: Int, binding: FragmentDetailsMovieBinding){
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit()
                .create(MovieApiServiceInterface::class.java)
                .getDetails("${query}?api_key=${Constants.key}")

            val callMovies = call.body()

            mActivity?.runOnUiThread {
                if (call.isSuccessful) {

                    if (callMovies != null) {

                        view?.let {

                            binding.tvTitleMovie.text = callMovies.title

                            Glide.with(it.context)
                                .load("https://image.tmdb.org/t/p/w300${callMovies.poster}")
                                .into(binding.ivPosterMovie)

                            binding.tvOriginalTitleMovie.text =
                                getString(R.string.movie_details_original_title) +
                                        ": " + callMovies.originalTitle

                            binding.tvIdMovie.text =
                                getString(R.string.movie_details_id) +
                                ": " + callMovies.id

                            binding.tvOriginalLanguageMovie.text =
                                getString(R.string.movie_details_original_language) +
                                ": " + callMovies.originalLanguage

                            binding.tvReleaseDateMovie.text =
                                getString(R.string.movie_details_release_date) +
                                ": " + callMovies.releaseDate

                            binding.tvPopularityMovie.text =
                                getString(R.string.movie_details_popularity) +
                                ": " + callMovies.popularity

                            binding.tvVoteAverageMovie.text =
                                getString(R.string.movie_details_vote_average) +
                                ": " + callMovies.voteAverage


                            val stringGenres = getStringGenres(callMovies.genres)

                            binding.tvGenreMovie.text =
                                getString(R.string.movie_details_vote_genres) +
                                ": $stringGenres"

                            binding.btnBack.setOnClickListener {
                                mActivity?.onBackPressed()
                            }
                        }

                    }

                } else {
                    showMessageError()
                }
            }

        }
    }

    private fun getStringGenres(genres:List<Genre>):String{
        var stringGenres = ""
        for(movie in genres){
            stringGenres += "${movie.name}/"
        }
        return stringGenres
    }

    private fun showMessageError(){
        Toast.makeText(view?.context, "Error", Toast.LENGTH_SHORT).show()
    }

    private fun setToolbar() {
        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)

        mActivity?.supportActionBar?.title = getString(R.string.title_details)

    }

    // When click in each option menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){

            //Back
            android.R.id.home -> {
                mActivity?.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        setHasOptionsMenu(false)
        super.onDestroy()
    }


}