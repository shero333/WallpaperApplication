package com.example.wallpaperapplication.repository.localrepo

import androidx.lifecycle.LiveData
import com.example.wallpaperapplication.models.RoomImageModel
import com.example.wallpaperapplication.repository.localrepo.utils.DAO
import javax.inject.Inject

class RepositoryRoom @Inject constructor(
    private val dao: DAO?
) {
    suspend fun insert(model: RoomImageModel?) {
        dao!!.insert(model)
    }

//    // creating a method to update data in database.
//    suspend fun update(model: RoomImageModel?) {
//        dao!!.update(model)
//    }

    // creating a method to delete the data in our database.
    suspend fun delete(model: RoomImageModel?) {
        dao!!.delete(model)
    }

    fun getAll():List<RoomImageModel>{
        return dao!!.allImages
    }
}