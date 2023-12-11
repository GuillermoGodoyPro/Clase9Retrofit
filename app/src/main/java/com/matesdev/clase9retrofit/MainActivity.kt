package com.matesdev.clase9retrofit

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BreedsAdapter
    private lateinit var searchView: SearchView
    private lateinit var spinner : Spinner
    private var imagesByBreedList = mutableListOf<String>()
    private var listadoRazas = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerImg)
        spinner = findViewById(R.id.spinner)
        searchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(this)


        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = BreedsAdapter(imagesByBreedList)
        recyclerView.adapter = adapter

        getListOfBreeds()


    }

    private fun getListOfBreeds() {
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(ApiService::class.java).getListOfBreeds("breeds/list/all")
            val response = call.body()

            // que corra en la interface de usuario
            runOnUiThread {
                if(call.isSuccessful){
                    val breedsMap = response?.breeds
                    breedsMap?.let {
                        breedsMap.map {
                            listadoRazas.add(it.key)
                        }
                    }
                    //declara var breed, y asigna valor a cada keys
                    /*for(breed in breedsMap.keys ) {
                            listadoRazas.add(breed)
                    */

                    setSpiner()

                } else {
                    showError()
                }

                hideKeyboard()
            }
        }
    }

    private fun setSpiner() {
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listadoRazas)
        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val breed = listadoRazas[p2]
                getListOfImagesByBreed(breed)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

    }

    private fun getListOfImagesByBreed(breed : String) {

        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(ApiService::class.java).getImageByBreeds("breed/$breed/images")
            val response = call.body()

            runOnUiThread {
                if(call.isSuccessful){
                    val images = response?.images ?: emptyList()
                    imagesByBreedList.clear()
                    imagesByBreedList.addAll(images)
                    adapter.notifyDataSetChanged()
                } else {
                    showError()
                }

                hideKeyboard()
            }
        }

    }

    private fun hideKeyboard() {
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        var view: View? = this.currentFocus
        if(view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showError() {
        Toast.makeText(this, "fallo en la llamada", Toast.LENGTH_SHORT).show()
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://dog.ceo/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    override fun onQueryTextSubmit(breed: String?): Boolean {
        if(!breed.isNullOrBlank()) {
            getListOfImagesByBreed(breed)
        }
        return true
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        return false
    }


}
