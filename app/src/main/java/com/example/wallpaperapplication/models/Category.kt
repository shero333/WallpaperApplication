package com.example.wallpaperapplication.models

class Category(var name:String, var description:String, var image: Int) {

    override fun toString(): String {
        return "Category(name='$name', description='$description', image='$image')"
    }

}