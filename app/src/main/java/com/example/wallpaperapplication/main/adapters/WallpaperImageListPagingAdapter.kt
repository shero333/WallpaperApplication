package com.example.wallpaperapplication.main.adapters

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wallpaperapplication.R
import com.example.wallpaperapplication.models.UnsplashImageModelItem

class WallpaperImageListPagingAdapter(
    private val activity: Activity,
    private val itemclick: ClickThumbnail,
) :
    PagingDataAdapter<UnsplashImageModelItem, WallpaperImageListPagingAdapter.WallpaperImageListPagingViewHolder>(COMPARATOR) {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WallpaperImageListPagingViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_itemview, parent, false)
        return WallpaperImageListPagingViewHolder(view)
    }

    override fun onBindViewHolder(holder: WallpaperImageListPagingViewHolder, position: Int) {

        Log.d("ItemRecyclerView", "onBindViewHolder: ${getItem(position)}")

        val item = getItem(position)

        Glide.with(activity).load(item?.urls?.full).placeholder(R.drawable.placeholder).into(holder.image)

        holder.itemView.setOnClickListener {
            itemclick.itemClicked(item)
        }

    }


    class WallpaperImageListPagingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: AppCompatImageView = itemView.findViewById(R.id.category_thumbnail)
    }

    companion object {

        private val COMPARATOR = object : DiffUtil.ItemCallback<UnsplashImageModelItem>() {

            override fun areItemsTheSame(
                oldItem: UnsplashImageModelItem,
                newItem: UnsplashImageModelItem
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: UnsplashImageModelItem,
                newItem: UnsplashImageModelItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface ClickThumbnail {

        fun itemClicked(regularImage: UnsplashImageModelItem?)

    }

}