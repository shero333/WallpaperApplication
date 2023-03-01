package com.example.wallpaperapplication.main.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wallpaperapplication.R
import com.example.wallpaperapplication.databinding.CategoryImagesListitemBinding
import com.example.wallpaperapplication.databinding.LayoutAdBinding
import com.example.wallpaperapplication.models.helperModels.category_items.Result
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class CategoryImageListPagingAdapter(
    private var activity: Activity,
    private val itemclick: ClickWallpaper
) : PagingDataAdapter<Result, RecyclerView.ViewHolder>(COMPARATOR) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val layoutInflater = LayoutInflater.from(activity)
        return if (viewType == TYPE_ITEM) {
            val view: View = layoutInflater.inflate(R.layout.category_images_listitem, parent, false)
            return WallpaperImageListPagingViewHolder(view)
        }
        else {
            val view: View = layoutInflater.inflate(R.layout.layout_ad, parent, false)
            WallpaperImageListPagingViewHolderAD(parent,view)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("ItemRecyclerView", "onBindViewHolder: ${getItem(position)}")

        if (getItemViewType(position) == TYPE_AD_VIEW) {
            (holder as WallpaperImageListPagingViewHolderAD).bindAdData(activity)
        }else if (getItemViewType(position) == TYPE_ITEM)
            (holder as WallpaperImageListPagingViewHolder).bindData(activity,getItem(position))


        holder.itemView.setOnClickListener {
            itemclick.itemClicked(getItem(position))
        }
    }


    override fun getItemViewType(position: Int): Int {
        return if (position % ADS_AFTER == 0) TYPE_AD_VIEW else TYPE_ITEM
    }

    class WallpaperImageListPagingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = CategoryImagesListitemBinding.bind(itemView)

        fun bindData(activity: Activity,regularImage: Result?) {
            Glide.with(activity).load(regularImage?.urls?.regular)
                .placeholder(R.drawable.placeholder).into(binding.wallpaperImageItem)
        }
    }

    //Ad view holder
    class WallpaperImageListPagingViewHolderAD(parent: ViewGroup,itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = LayoutAdBinding.bind(itemView)
        private var parent: ViewGroup

        init {
            this.parent = parent
        }

        fun populateNativeADView(nativeAd: NativeAd, adView: NativeAdView) {
            // Set the media view.
            adView.mediaView = adView.findViewById(R.id.ad_media)

            // Set other ad assets.
            adView.headlineView = adView.findViewById(R.id.ad_headline)
            adView.bodyView = adView.findViewById(R.id.ad_body)
            adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
            adView.iconView = adView.findViewById(R.id.ad_app_icon)
            adView.priceView = adView.findViewById(R.id.ad_price)
            adView.starRatingView = adView.findViewById(R.id.ad_stars)
            adView.storeView = adView.findViewById(R.id.ad_store)
            adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

            // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
            (adView.headlineView as TextView).text = nativeAd.headline
            adView.mediaView!!.setMediaContent(nativeAd.mediaContent!!)

            // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
            // check before trying to display them.
            if (nativeAd.body == null) {
                adView.bodyView!!.visibility = View.INVISIBLE
            } else {
                adView.bodyView!!.visibility = View.VISIBLE
                (adView.bodyView as TextView).text = nativeAd.body
            }
            if (nativeAd.callToAction == null) {
                adView.callToActionView!!.visibility = View.INVISIBLE
            } else {
                adView.callToActionView!!.visibility = View.VISIBLE
                (adView.callToActionView as Button).text = nativeAd.callToAction
            }
            if (nativeAd.icon == null) {
                adView.iconView!!.visibility = View.GONE
            } else {
                (adView.iconView as ImageView).setImageDrawable(
                    nativeAd.icon!!.drawable
                )
                adView.iconView!!.visibility = View.VISIBLE
            }
            if (nativeAd.price == null) {
                adView.priceView!!.visibility = View.INVISIBLE
            } else {
                adView.priceView!!.visibility = View.VISIBLE
                (adView.priceView as TextView).text = nativeAd.price
            }
            if (nativeAd.store == null) {
                adView.storeView!!.visibility = View.INVISIBLE
            } else {
                adView.storeView!!.visibility = View.VISIBLE
                (adView.storeView as TextView).text = nativeAd.store
            }

            if (nativeAd.starRating == null) {
                adView.starRatingView!!.visibility = View.INVISIBLE
            } else {
                (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
                adView.starRatingView!!.visibility = View.VISIBLE
            }
            if (nativeAd.advertiser == null) {
                adView.advertiserView!!.visibility = View.INVISIBLE
            } else {
                (adView.advertiserView as TextView).text = nativeAd.advertiser
                adView.advertiserView!!.visibility = View.VISIBLE
            }

            // This method tells the Google Mobile Ads SDK that you have finished populating your
            // native ad view with this native ad.
            adView.setNativeAd(nativeAd)
        }


        @SuppressLint("VisibleForTests")
        fun bindAdData(activity: Activity) {
            val builder = AdLoader.Builder(activity, "ca-app-pub-3940256099942544/2247696110")
                .forNativeAd { nativeAd ->
                    val nativeAdView = activity.layoutInflater.inflate(R.layout.native_ad_layout,parent,false) as NativeAdView
                    populateNativeADView(nativeAd, nativeAdView)
                    binding.adLayout.removeAllViews()
                    binding.adLayout.addView(nativeAdView)
                }
            val adLoader = builder.withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)

                    Toast.makeText(activity, loadAdError.message, Toast.LENGTH_SHORT).show()
                }
            }).build()

            adLoader.loadAd(AdRequest.Builder().build())
        }
    }

    companion object {

        private val ADS_AFTER = 9
        private val TYPE_AD_VIEW = 2
        private val TYPE_ITEM = 1

        private val COMPARATOR = object : DiffUtil.ItemCallback<Result>() {

            override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface ClickWallpaper {

        fun itemClicked(regularImage: Result?)

    }

}