package com.example.wallpaperapplication.main.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wallpaperapplication.main.viewmodel.CategoryViewModel
import com.example.wallpaperapplication.main.viewmodel.SharedViewModelImages
import com.example.wallpaperapplication.repository.network.retroinjection.UnsplashRepository

class SharedViewModelImagesFactory(
    private val unsplashRepository: UnsplashRepository,
    private val type:String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(SharedViewModelImages::class.java)){
            return SharedViewModelImages(unsplashRepository,type) as T
        }
        throw IllegalArgumentException("Unknown view Model")

    }
}