package com.example.challengekotlin.ui.view

import android.os.Build
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.challengekotlin.R
import com.example.challengekotlin.core.Constants
import com.example.challengekotlin.data.model.Movie
import com.example.challengekotlin.data.webservice.ConsumeRetrofit
import com.example.challengekotlin.data.webservice.MovieApiServiceInterface
import com.example.challengekotlin.data.webservice.OnItemMovieClickListener
import com.example.challengekotlin.databinding.ActivityMainBinding
import com.example.challengekotlin.ui.adapter.MovieAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.stream.Collectors

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener,
    OnItemMovieClickListener {

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

    private fun validateKey(key: String?): Boolean {
        return !key.isNullOrEmpty()
    }

    private fun searchMovies(){
        CoroutineScope(Dispatchers.IO).launch {

            val call = ConsumeRetrofit.getRetrofit()
                .create(MovieApiServiceInterface::class.java)
                .getMovies("popular?api_key=${Constants.key}")

            val callMovies = call.body()

            runOnUiThread {
                if(call.isSuccessful){
                    val listMovies = callMovies?.movies ?: emptyList()
                    moviesList.clear()
                    moviesList.addAll(listMovies)
                    moviesOriginalList.addAll(listMovies)

                    adapter.notifyDataSetChanged()

                }else{
                    if(validateKey(Constants.key)) showMessageError() else showAlertDialog()
                }

                hideKeyboard()
            }
        }
    }

    private fun searchMoviesByTitle(title: String){

        if(title.isNotEmpty()){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val collection: MutableList<Movie> = moviesList.stream()
                    .filter {  i -> i.title.lowercase().contains(title.lowercase())}
                    .collect(Collectors.toList())

                moviesList.clear()
                moviesList.addAll(collection)
            } else {
                for(movie in moviesOriginalList){
                    if(movie.title.lowercase().contains(title.lowercase())){
                        moviesList.add(movie)
                    }
                }
            }
        } else{
            moviesList.clear()
            moviesList.addAll(moviesOriginalList)
        }
        adapter.notifyDataSetChanged()
    }

    private fun hideKeyboard() {
        val noKeyboard = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        noKeyboard.hideSoftInputFromWindow(binding.clMain.windowToken, 0)
    }

    private fun showMessageError(){
        Toast.makeText(this, getString(R.string.message_error_api), Toast.LENGTH_SHORT).show()
    }

    // Methods Search in edittext
    override fun onQueryTextSubmit(query: String?): Boolean {
        hideKeyboard()
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

    // AlertDialog to show a message if the error was the key
    private fun showAlertDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.key_dialog_title))
        builder.setMessage(getString(R.string.key_dialog_message))
        builder.setIcon(AppCompatResources.getDrawable(this,R.drawable.ic_warning))

        builder.setPositiveButton(getString(R.string.key_dialog_positive_button)) {
                _, _ ->
            Toast.makeText(this, getString(R.string.key_dialog_positive_message),
                Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton(getString(R.string.key_dialog_negative_button)) {
                dialog, _ -> dialog.cancel()
        }

        builder.show()
    }

    // Set the Toolbar in this activity
    private fun setToolbar() {
        supportActionBar?.title = getString(R.string.title_movies)
    }

    // Launch Details Fragment with the data by transaction
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