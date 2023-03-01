package com.example.wallpaperapplication.repository.network.retroinjection

import com.example.wallpaperapplication.repository.network.Api
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModuleApiClient {

    companion object {
        private const val BASE_URL = "https://api.unsplash.com"
    }

    @get:Provides
    @get:Singleton
    val retroInstance: Retrofit
        get() = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Singleton
    @Provides
    fun getApi(retrofit: Retrofit): Api {
        return retrofit.create(Api::class.java)
    }
}