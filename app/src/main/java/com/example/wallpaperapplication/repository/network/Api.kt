package com.example.wallpaperapplication.repository.network

import com.example.wallpaperapplication.models.SearchImageModel
import com.example.wallpaperapplication.models.UnsplashImageModelItem
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface Api {

    companion object {
        private const val API_KEY = "KnGcq4IOaZNKSNU20JpBHxdCZQTYSJPlYL7yGnK44AU"
    }
    @Headers("Authorization: Client-ID $API_KEY")
    @GET("/photos")
    suspend fun getImages(@Query("page") page: Int, @Query("per_page") perPage: Int, @Query("order_by") order_by:String): List<UnsplashImageModelItem>

    @Headers("Authorization: Client-ID $API_KEY")
    @GET("/search/photos")
    suspend fun getSearchedImages(@Query("query") query: String, @Query("page") page: Int, @Query("per_page") perPage: Int, @Query("order_by") order_by:String): SearchImageModel
}