package com.example.wallpaperapplication.repository.network.retroinjection

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.wallpaperapplication.repository.network.Api
import javax.inject.Inject
import com.example.wallpaperapplication.models.helperModels.category_items.Result

class CategoryPageLoader @Inject constructor(private val api: Api, private var order_by:String,private var query:String) : PagingSource<Int, Result>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Result> {
        return try {

            val position = params.key ?: 1
            val response = api.getSearchedImages(query,position,12,order_by)

            Log.d("DataDirectCategories", "load: $response")

            return LoadResult.Page(
                data = response.results,
                prevKey = if (position == 1) null else position.inc(),
                nextKey = if (response.results.size <30) null else position.inc()
            )


        } catch (e: java.lang.Exception) {
            Log.i("load: ", "${e.message}")
            LoadResult.Error(e)

        }
    }

    override fun getRefreshKey(state: PagingState<Int, Result>): Int? {

        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }


}