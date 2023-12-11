package com.matesdev.clase9retrofit

import com.google.gson.annotations.SerializedName

data class BreedsResponse (
    @SerializedName("message")val breeds: Map<String, List<String>>,
    val status: String
)
