package com.example.wallpaperapplication.repository.localrepo.utils

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.wallpaperapplication.models.RoomImageModel

@Dao
interface DAO {
    // below method is use to
    // add data to database.
    @Insert
    suspend fun insert(model: RoomImageModel?)

    // below method is use to update
    // the data in our database.
    @Update
    suspend fun update(model: RoomImageModel?)

    // below line is use to delete a
    // specific item in our database.
    @Delete
    suspend fun delete(model: RoomImageModel?)

    // below line is to read all the courses from our database.
    // in this we are ordering our courses in ascending order
    // with our course name.
    @get:Query("SELECT * FROM image_table ORDER BY url ASC")
    val allImages: List<RoomImageModel>
}