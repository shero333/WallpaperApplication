package com.example.wallpaperapplication.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.wallpaperapplication.repository.network.retroinjection.UnsplashRepository

class CategoryViewModel(repository: UnsplashRepository, category: String, type: String) : ViewModel() {

    val listCategories = repository.callSearchedWallpaperApi(category, type).cachedIn(viewModelScope)

}