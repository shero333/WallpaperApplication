package com.example.wallpaperapplication.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "image_table")
class RoomImageModel(var url:String?,var name:String?,@field:PrimaryKey(autoGenerate = true) var  id: Int) {

    override fun toString(): String {
        return "RoomImageModel(url='$url', name='$name', id=$id)"
    }
}