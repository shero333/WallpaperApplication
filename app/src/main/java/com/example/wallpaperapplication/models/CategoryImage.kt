package com.example.wallpaperapplication.models

class CategoryImage constructor(private var name: String? = null, private var isClicked: Boolean){
    override fun toString(): String {
        return "CategoryImage(name=$name, isSelected=$isClicked)"
    }

    fun setIsClicked(clicked: Boolean) {
        isClicked = clicked
    }

    fun getIsClicked(): Boolean {
        return isClicked
    }

    fun getName(): String? {
        return name
    }
}