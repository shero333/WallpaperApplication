package com.example.wallpaperapplication.repository.network.retroinjection

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.example.wallpaperapplication.repository.network.Api
import javax.inject.Inject

class UnsplashRepository @Inject constructor(private val api: Api) {

    fun callWallpaperApi(order_by:String) = Pager(
        config = PagingConfig(pageSize = 30, maxSize = 100),
        pagingSourceFactory = { PageLoader(api,order_by) }
    ).liveData

    fun callSearchedWallpaperApi(query:String, order_by:String) = Pager(
        config = PagingConfig(pageSize = 30, maxSize = 100),
        pagingSourceFactory = { CategoryPageLoader(api,order_by,query) }
    ).liveData
}