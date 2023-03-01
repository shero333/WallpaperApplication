package com.example.wallpaperapplication.models

import com.example.wallpaperapplication.models.helperModels.category_items.Result

data class SearchImageModel(
    val results: List<Result>,
    val total: Int,
    val total_pages: Int
)