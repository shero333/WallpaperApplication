package com.example.wallpaperapplication.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.wallpaperapplication.repository.network.retroinjection.UnsplashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

class SharedViewModelImages constructor(repository: UnsplashRepository, type: String) : ViewModel() {

    val list = repository.callWallpaperApi(type).cachedIn(viewModelScope)

}