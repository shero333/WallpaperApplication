package com.example.wallpaperapplication.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wallpaperapplication.models.RoomImageModel
import com.example.wallpaperapplication.repository.localrepo.RepositoryRoom
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModelLocalStore @Inject constructor(private val repositoryRoom: RepositoryRoom) : ViewModel() {

    fun LikeImage(imageUrl: String?,id:Int,name: String?){
        viewModelScope.launch {
            repositoryRoom.insert(RoomImageModel(imageUrl,name,id))
        }
    }

    fun dislikeImage(roomImageModel:RoomImageModel){
        viewModelScope.launch {
            repositoryRoom.delete(roomImageModel)
        }
    }
}