package com.example.challengekotlin

import android.os.Build
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.challengekotlin.core.Constants
import com.example.challengekotlin.data.model.Movie
import com.example.challengekotlin.data.webservice.MovieApiServiceInterface
import com.example.challengekotlin.data.webservice.OnItemMovieClickListener
import com.example.challengekotlin.databinding.ActivityMainBinding
import com.example.challengekotlin.ui.adapter.MovieAdapter
import com.example.challengekotlin.ui.view.DetailsMovieFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.stream.Collectors


class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener, OnItemMovieClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MovieAdapter
    private val moviesList = mutableListOf<Movie>()
    private val moviesOriginalList = mutableListOf<Movie>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.svSearch.setOnQueryTextListener(this)

        setToolbar()
        initializeRecyclerView()
        searchMovies()
    }

    private fun initializeRecyclerView(){
        adapter = MovieAdapter(moviesList, this)
        binding.rvMovies.layoutManager = LinearLayoutManager(this)
        binding.rvMovies.adapter = adapter
    }

    private fun getRetrofit(): Retrofit{
        return Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/movie/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun searchMovies(){
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(MovieApiServiceInterface::class.java).getMovies("popular?api_key=${Constants.key}")
            val callMovies = call.body()

            runOnUiThread {
                if(call.isSuccessful){
                    val listMovies = callMovies?.movies ?: emptyList()
                    moviesList.clear()
                    moviesList.addAll(listMovies)

                    moviesOriginalList.addAll(listMovies)

                    adapter.notifyDataSetChanged()
                    // show recycler
                }else{
                    showMessageError()
                }
                hideKeyboard()
            }
        }
    }

    private fun searchMoviesByTitle(title: String){

        if(title.isNotEmpty()){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                var collection: MutableList<Movie> = moviesList.stream()
                    .filter {  i -> i.title.toLowerCase().contains(title.toLowerCase())}
                    .collect(Collectors.toList())

                moviesList.clear()
                moviesList.addAll(collection)
            } else {
                for(movie in moviesOriginalList){
                    if(movie.title.toLowerCase().contains(title.toLowerCase())){
                        moviesList.add(movie)
                    }
                }
            }
        }else{
            moviesList.clear()
            moviesList.addAll(moviesOriginalList)

        }
        adapter.notifyDataSetChanged()


    }


    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.clMain.windowToken, 0)
    }

    private fun showMessageError(){
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
    }

    // Methods Search in edittext
    override fun onQueryTextSubmit(query: String?): Boolean {

        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if(!query.isNullOrEmpty()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                searchMoviesByTitle(query)
            }
        }else{
            moviesList.clear()
            moviesList.addAll(moviesOriginalList)
            adapter.notifyDataSetChanged()
        }
        return false
    }


    // When click movie put only Id and then retrofit with this id
    override fun onMovieItemClick(id: Int) {
        Toast.makeText(this, id.toString(), Toast.LENGTH_SHORT).show()
        val args = Bundle()

        args.putInt("key_id", id)
        launchDetailsFragment(args)
    }

    /*

    // When search movie put by args all characteristics title, poster, genre, etc
    override fun onMovieItemClick(movie: Movie) {
        Toast.makeText(this, movie.id.toString(), Toast.LENGTH_SHORT).show()
        val args = Bundle()

        args.putString("key_id",movie.id.toString())
        args.putString("key_poster", movie.poster)

        launchDetailsFragment(args)
    }

     */


    private fun setToolbar() {
        supportActionBar?.title = getString(R.string.title_movies)
    }

    private fun launchDetailsFragment(args:Bundle?){
        val fragment = DetailsMovieFragment()

        args.let { fragment.arguments = it }

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.clMain, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}