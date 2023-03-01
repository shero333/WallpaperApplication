package com.example.wallpaperapplication.models.helperModels.simple_paging

data class Sponsorship(
    val impression_urls: List<String>,
    val sponsor: Sponsor,
    val tagline: String,
    val tagline_url: String
)