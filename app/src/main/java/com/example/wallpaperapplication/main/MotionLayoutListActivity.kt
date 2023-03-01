package com.example.wallpaperapplication.main

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.wallpaperapplication.R
import com.example.wallpaperapplication.databinding.ActivityMotionLayoutListBinding
import com.example.wallpaperapplication.main.adapters.CategoryImageListPagingAdapter
import com.example.wallpaperapplication.main.viewmodel.CategoryViewModel
import com.example.wallpaperapplication.main.viewmodel.factory.CategoryViewModelFactory
import com.example.wallpaperapplication.models.helperModels.category_items.Result
import com.example.wallpaperapplication.repository.network.retroinjection.UnsplashRepository
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import layout.ListLoaderViewAdapter
import javax.inject.Inject

@AndroidEntryPoint
class MotionLayoutListActivity : AppCompatActivity(),
    CategoryImageListPagingAdapter.ClickWallpaper {

    private lateinit var binding: ActivityMotionLayoutListBinding
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var categoryViewModelFactory: CategoryViewModelFactory
    private lateinit var adapterList: CategoryImageListPagingAdapter
    private var count = 0
    private var mInterstitialAd: InterstitialAd? = null
    private val selectedImage = this
    private lateinit var progressDialog:ProgressDialog

    @Inject
    lateinit var unsplashRepository: UnsplashRepository

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMotionLayoutListBinding.inflate(layoutInflater)

        setContentView(binding.root)

        progressDialog = ProgressDialog(this, R.style.AppCompatAlertDialogStyle)
        progressDialog.setMessage("Loading images.....")

        setAd()

        val categoryName = intent.getStringExtra("name_category")

        binding.heading.text = categoryName
        binding.description.text = "$categoryName "+resources.getString(R.string.abstract_backgrounds_are_universal_design_elements_because_they_re_free_of_implied_meaning_we_can_enjoy_them_simply_as_compositions_of_light)

        //recyclerview

        categoryViewModelFactory = CategoryViewModelFactory(unsplashRepository, categoryName!!, "trending")

        categoryViewModel = ViewModelProvider(this, categoryViewModelFactory)[CategoryViewModel::class.java]


        val manager = GridLayoutManager(this, 2)
        manager.spanSizeLookup = object : SpanSizeLookup(){
            override fun getSpanSize(position: Int): Int {
                return if (position % 9 == 0)
                    2
                else
                    1
            }
        }

        binding.listCategories.layoutManager = manager
        binding.listCategories.itemAnimator = DefaultItemAnimator()

        adapterList = CategoryImageListPagingAdapter(this@MotionLayoutListActivity, this)

        categoryViewModel.listCategories.observe(this) {
            adapterList.submitData(lifecycle,it)
            adapterList.notifyDataSetChanged()

            adapterList.addLoadStateListener { loadstate ->

                if (loadstate.source.refresh is LoadState.Loading)
                    progressDialog.show()
                else
                    progressDialog.dismiss()
            }

        }

        binding.listCategories.adapter = adapterList.withLoadStateHeaderAndFooter(
            header = ListLoaderViewAdapter(),
            footer = ListLoaderViewAdapter()
        )

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()


        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            // network is available for use
            override fun onAvailable(network: Network) {
                super.onAvailable(network)

            }

            // Network capabilities have changed for the network
            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                super.onCapabilitiesChanged(network, networkCapabilities)

            }

            // lost network connection
            override fun onLost(network: Network) {
                super.onLost(network)

                startActivity(Intent(this@MotionLayoutListActivity,MainScreenActivity::class.java))
                finish()
            }
        }

        val connectivityManager = getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }

    override fun itemClicked(regularImage: Result?) {

        val gson = Gson()
        val image = gson.toJson(regularImage)

        count++
        if (count == 3) {

            count = 0
            mInterstitialAd?.show(this)

            Glide.with(this)
                .asBitmap()
                .load(regularImage?.urls?.regular)
                .into(object : SimpleTarget<Bitmap?>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {

                        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent()

                                setAd()

                                try {

                                    val intent = Intent(this@MotionLayoutListActivity, BackgroundWallpaperListActivity::class.java)

                                    intent.putExtra("image", regularImage!!.urls.regular)
                                    intent.putExtra("name", regularImage.description)
                                    intent.putExtra("date", regularImage.created_at)
                                    intent.putExtra("created_by", regularImage.user.name)
                                    intent.putExtra("height", regularImage.height.toString())
                                    intent.putExtra("width", regularImage.width.toString())
                                    intent.putExtra("pixels", resource.density.toString())
                                    intent.putExtra("image_size", resource.byteCount.toString())
                                    startActivity(intent)



                                } catch (e: java.lang.Exception) {

                                    val intent = Intent(this@MotionLayoutListActivity, BackgroundWallpaperListActivity::class.java)

                                    intent.putExtra("image", regularImage!!.urls.regular)
                                    intent.putExtra("name", "No name")
                                    intent.putExtra("date", regularImage.created_at)
                                    intent.putExtra("created_by", regularImage.user.name)
                                    intent.putExtra("height", regularImage.height.toString())
                                    intent.putExtra("width", regularImage.width.toString())
                                    intent.putExtra("pixels", resource.density.toString())
                                    intent.putExtra("image_size", resource.byteCount.toString())
                                    startActivity(intent)

                                }

                            }
                        }

                    }
                })

        } else {

            Glide.with(this)
                .asBitmap()
                .load(regularImage?.urls?.regular)
                .into(object : SimpleTarget<Bitmap?>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {

                        try {

                            val intent = Intent(this@MotionLayoutListActivity, BackgroundWallpaperListActivity::class.java)

                            intent.putExtra("image", regularImage!!.urls.regular)
                            intent.putExtra("name", regularImage.description)
                            intent.putExtra("date", regularImage.created_at)
                            intent.putExtra("created_by", regularImage.user.name)
                            intent.putExtra("height", regularImage.height.toString())
                            intent.putExtra("width", regularImage.width.toString())
                            intent.putExtra("pixels", resource.density.toString())
                            intent.putExtra("image_size", resource.byteCount.toString())
                            startActivity(intent)



                        } catch (e: java.lang.Exception) {

                            val intent = Intent(this@MotionLayoutListActivity, BackgroundWallpaperListActivity::class.java)

                            intent.putExtra("image", regularImage!!.urls.regular)
                            intent.putExtra("name", "No name")
                            intent.putExtra("date", regularImage.created_at)
                            intent.putExtra("created_by", regularImage.user.name)
                            intent.putExtra("height", regularImage.height.toString())
                            intent.putExtra("width", regularImage.width.toString())
                            intent.putExtra("pixels", resource.density.toString())
                            intent.putExtra("image_size", resource.byteCount.toString())
                            startActivity(intent)

                        }

                    }
                })

        }

    }

    private fun setAd() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            this,
            "ca-app-pub-3940256099942544/1033173712",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("AdError", adError.toString())
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d("AdError", "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                }
            })
    }
}
