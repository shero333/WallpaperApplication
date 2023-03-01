package com.example.wallpaperapplication.main.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wallpaperapplication.main.viewmodel.CategoryViewModel
import com.example.wallpaperapplication.main.viewmodel.SharedViewModelImages
import com.example.wallpaperapplication.repository.network.retroinjection.UnsplashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

class CategoryViewModelFactory(
    private val unsplashRepository: UnsplashRepository,
    private val category:String,
    private val type:String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)){
            return CategoryViewModel(unsplashRepository,category,type) as T
        }
        throw IllegalArgumentException("Unknown view Model")
    }
}