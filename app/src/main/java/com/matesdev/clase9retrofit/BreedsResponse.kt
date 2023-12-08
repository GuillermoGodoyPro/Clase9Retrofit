package com.matesdev.clase9retrofit

import com.google.gson.annotations.SerializedName

//data porque nos permite comparar estas clases
data class BreedsResponse (
    // ver la respuesta del ep para poner el tipo de dato
    @SerializedName("message")val images: List<String>,
    val status: String

)


