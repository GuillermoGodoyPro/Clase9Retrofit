package com.matesdev.clase9retrofit

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {

    @GET
    suspend fun getImageByBreeds(@Url url: String): Response<BreedsResponse>

}

