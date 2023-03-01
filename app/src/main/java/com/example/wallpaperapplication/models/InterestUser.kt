package com.example.wallpaperapplication.models

class InterestUser constructor(private var name: String? = null, private var isSelected: Boolean) {

    override fun toString(): String {
        return "InterestUser(name=$name, isSelected=$isSelected)"
    }

    fun setSelected(selection: Boolean) {
        isSelected = selection
    }

    fun getSelected(): Boolean {
        return isSelected
    }

    fun getName(): String? {
        return name
    }
}