package com.example.wallpaperapplication.main

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
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
import android.widget.*
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.wallpaperapplication.R
import com.example.wallpaperapplication.databinding.ActivityMainScreenBinding
import com.example.wallpaperapplication.main.adapters.CategoriesAdapter
import com.example.wallpaperapplication.main.adapters.CategoryNameListAdapter
import com.example.wallpaperapplication.main.adapters.WallpaperImageListPagingAdapter
import com.example.wallpaperapplication.main.viewmodel.SharedViewModelImages
import com.example.wallpaperapplication.main.viewmodel.factory.SharedViewModelImagesFactory
import com.example.wallpaperapplication.models.Category
import com.example.wallpaperapplication.models.CategoryImage
import com.example.wallpaperapplication.models.UnsplashImageModelItem
import com.example.wallpaperapplication.privacy_policy.PoliciesActivity
import com.example.wallpaperapplication.repository.network.retroinjection.UnsplashRepository
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import layout.ListLoaderViewAdapter
import javax.inject.Inject


@AndroidEntryPoint

class MainScreenActivity : AppCompatActivity(), CategoryNameListAdapter.CategorySelected,
    WallpaperImageListPagingAdapter.ClickThumbnail, CategoriesAdapter.SelectItem {

    private lateinit var binding: ActivityMainScreenBinding
    private lateinit var categoryNamesAdapter: CategoryNameListAdapter
    private lateinit var itemsListAdapter: WallpaperImageListPagingAdapter
    private lateinit var categoriesAdapter: CategoriesAdapter
    private var categories: ArrayList<CategoryImage> = ArrayList()
    private lateinit var SharedviewModel: SharedViewModelImages
    private lateinit var SharedviewModelFactory: SharedViewModelImagesFactory
    private lateinit var progressDialog: ProgressDialog
    private var count = 0
    private var count2 = 0
    private var mInterstitialAd: InterstitialAd? = null
    private lateinit var adRequest: AdRequest
    private lateinit var dialog: Dialog
    private lateinit var adLoader: AdLoader
    private var typeData = "trending"

    @Inject
    lateinit var unsplashRepository: UnsplashRepository


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainScreenBinding.inflate(layoutInflater)

        setContentView(binding.root)

        dialog = Dialog(this)
        dialog.setContentView(R.layout.exit_dialog_layout)
        dialog.setCancelable(false)


        val adTemplate = dialog.findViewById<TemplateView>(R.id.ad_template)

        adLoader = AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
            .forNativeAd {

                val templateStyle = NativeTemplateStyle.Builder().build()
                adTemplate.setStyles(templateStyle)
                adTemplate.setNativeAd(it)

            }.build()

        adLoader.loadAd(AdRequest.Builder().build())


        binding.actionBar.privacyPolicy.setOnClickListener {

            startActivity(Intent(this@MainScreenActivity, PoliciesActivity::class.java))

        }

        MobileAds.initialize(this) {}

        //banner
        adRequest = AdRequest.Builder().build()

        binding.bannerAd.loadAd(adRequest)

        setAd()

        SharedviewModelFactory = SharedViewModelImagesFactory(unsplashRepository,typeData)
        SharedviewModel = ViewModelProvider(this,SharedviewModelFactory)[SharedViewModelImages::class.java]

        //key words recyclerview
        categoryNamesAdapter = CategoryNameListAdapter(this@MainScreenActivity, this)

        categories.add(CategoryImage("Trending", true))
        categories.add(CategoryImage("Random", false))
        categories.add(CategoryImage("Weekly Popular", false))
        categories.add(CategoryImage("Monthly Popular", false))

        categories.add(CategoryImage("Most Popular", false))

        binding.categoryNamesList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.categoryNamesList.setHasFixedSize(true)
        binding.categoryNamesList.scrollToPosition(categoryNamesAdapter.itemCount - 1)
        binding.categoryNamesList.adapter = categoryNamesAdapter

        //setting data in the adapter
        categoryNamesAdapter.categoryList = categories


        //categories names data recyclerview
        itemsListAdapter = WallpaperImageListPagingAdapter(this@MainScreenActivity, this)

        binding.categoriesNamesItemsList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.categoriesNamesItemsList.setHasFixedSize(true)
        binding.categoriesNamesItemsList.scrollToPosition(itemsListAdapter.itemCount - 1)

        binding.categoriesNamesItemsList.adapter = itemsListAdapter.withLoadStateHeaderAndFooter(
            header = ListLoaderViewAdapter(),
            footer = ListLoaderViewAdapter()
        )

        progressDialog = ProgressDialog(this, R.style.AppCompatAlertDialogStyle)
        progressDialog.setMessage("Loading images.....")

        getData()

        //Categories Data
        categoriesAdapter = CategoriesAdapter(this@MainScreenActivity, this)

        binding.categoryList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.categoryList.setHasFixedSize(true)
        binding.categoryList.adapter = categoriesAdapter

        val categories = ArrayList<Category>()
        categories.add(
            Category(
                name = "Nature",
                description = "The last update Nature wallpapers are here",
                image = R.drawable.nature
            )
        )
        categories.add(
            Category(
                name = "Abstract",
                description = "The last update Abstract wallpapers are here",
                image = R.drawable.abs
            )
        )
        categories.add(
            Category(
                name = "Minimalist",
                description = "The last update Minimalist wallpapers are here",
                image = R.drawable.minimalist
            )
        )
        categories.add(
            Category(
                name = "Funny",
                description = "The last update Funny wallpapers are here",
                image = R.drawable.funny
            )
        )
        categories.add(
            Category(
                name = "Fiction",
                description = "The last update Fiction wallpapers are here",
                image = R.drawable.fictions
            )
        )
        categories.add(
            Category(
                name = "Imagination",
                description = "The last update Imagination wallpapers are here",
                image = R.drawable.imagination
            )
        )
        categories.add(
            Category(
                name = "High-Tech",
                description = "The last update High-Tech wallpapers are here",
                image = R.drawable.high_tech
            )
        )
        categories.add(
            Category(
                name = "Cars",
                description = "The last update Cars wallpapers are here",
                image = R.drawable.cars
            )
        )
        categories.add(
            Category(
                name = "Bikes",
                description = "The last update Bikes wallpapers are here",
                image = R.drawable.bikes
            )
        )
        categories.add(
            Category(
                name = "Laptops",
                description = "The last update Laptops wallpapers are here",
                image = R.drawable.laptops
            )
        )

        categoriesAdapter.categories = categories


        //back-press
        onBackPressedDispatcher.addCallback(this) {

            exitDialog()

        }

        if (checkForInternet(this)){
            binding.mainLayout.visibility = View.VISIBLE
            binding.noConnectionLayout.noConnectionLayout.visibility = View.GONE
        }else{
            binding.mainLayout.visibility = View.GONE
            binding.noConnectionLayout.noConnectionLayout.visibility = View.VISIBLE
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()


        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            // network is available for use
            override fun onAvailable(network: Network) {
                super.onAvailable(network)

                onRestart()

                runOnUiThread {
                    binding.mainLayout.visibility = View.VISIBLE
                    binding.noConnectionLayout.noConnectionLayout.visibility = View.GONE
                }
            }

            // lost network connection
            override fun onLost(network: Network) {
                super.onLost(network)

                runOnUiThread {
                    binding.mainLayout.visibility = View.GONE
                    binding.noConnectionLayout.noConnectionLayout.visibility = View.VISIBLE
                }

            }
        }

        val connectivityManager = getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        connectivityManager.requestNetwork(networkRequest, networkCallback)


    }

    private fun checkForInternet(context: Context): Boolean {

        // register activity with the connectivity manager service
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Returns a Network object corresponding to
        // the currently active default data network.
        val network = connectivityManager.activeNetwork ?: return false

        // Representation of the capabilities of an active network.
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            // Indicates this network uses a Wi-Fi transport,
            // or WiFi has network connectivity
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

            // Indicates this network uses a Cellular transport. or
            // Cellular has network connectivity
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

            // else return false
            else -> false
        }
    }

    private fun exitDialog() {

        val exit = dialog.findViewById<AppCompatTextView>(R.id.exit_button)
        val cancel = dialog.findViewById<AppCompatTextView>(R.id.cancel_button)

        exit.setOnClickListener {
            finishAffinity()
            dialog.dismiss()
        }

        cancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }


    @SuppressLint("NotifyDataSetChanged")
    fun getData() {

        SharedviewModel.list.observe(this) {

            itemsListAdapter.submitData(lifecycle, it)

            itemsListAdapter.notifyDataSetChanged()

            //loading state listener
            itemsListAdapter.addLoadStateListener { loadstate ->

                if (loadstate.source.refresh is LoadState.Loading){
                    progressDialog.show()
                    binding.progressbarList.visibility = View.VISIBLE
                }
                else{
                    progressDialog.dismiss()
                    binding.progressbarList.visibility = View.GONE
                }
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun categoryChoosen(nameCategory: CategoryImage, itemPosition: Int) {

        typeData = nameCategory.getName()!!

        SharedviewModelFactory = SharedViewModelImagesFactory(unsplashRepository,typeData)
        SharedviewModel = ViewModelProvider(this,SharedviewModelFactory)[SharedViewModelImages::class.java]

        getData()
        itemsListAdapter.notifyDataSetChanged()

        onRestart()

        categoryNamesAdapter.changeSelectionStatus(itemPosition)
        binding.categoryNamesList.scrollToPosition(itemPosition)
        binding.categoriesNamesItemsList.scrollToPosition(0)
    }

    override fun itemClicked(regularImage: UnsplashImageModelItem?) {

        count2++
        if (count2 == 3) {

            count2 = 0
            mInterstitialAd?.show(this)

            Glide.with(this)
                .asBitmap()
                .load(regularImage?.urls?.regular)
                .into(object : SimpleTarget<Bitmap?>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap?>?
                    ) {

                        mInterstitialAd?.fullScreenContentCallback =
                            object : FullScreenContentCallback() {
                                override fun onAdDismissedFullScreenContent() {
                                    super.onAdDismissedFullScreenContent()

                                    setAd()

                                    try {

                                        val intent =
                                            Intent(
                                                this@MainScreenActivity,
                                                BackgroundWallpaperListActivity::class.java
                                            )
                                        intent.putExtra("image", regularImage!!.urls.regular)
                                        intent.putExtra("name", regularImage.description.toString())
                                        intent.putExtra("date", regularImage.created_at)
                                        intent.putExtra("created_by", regularImage.user.name)
                                        intent.putExtra("height", regularImage.height.toString())
                                        intent.putExtra("width", regularImage.width.toString())
                                        intent.putExtra("pixels", resource.density.toString())
                                        intent.putExtra("image_size", resource.byteCount.toString())
                                        startActivity(intent)

                                    } catch (e: java.lang.Exception) {

                                        val intent =
                                            Intent(
                                                this@MainScreenActivity,
                                                BackgroundWallpaperListActivity::class.java
                                            )
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
                .into(object : SimpleTarget<Bitmap?>() { override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {

                        try {

                            val intent =
                                Intent(
                                    this@MainScreenActivity,
                                    BackgroundWallpaperListActivity::class.java
                                )
                            intent.putExtra("image", regularImage!!.urls.regular)
                            intent.putExtra("name", regularImage.description.toString())
                            intent.putExtra("date", regularImage.created_at)
                            intent.putExtra("created_by", regularImage.user.name)
                            intent.putExtra("height", regularImage.height.toString())
                            intent.putExtra("width", regularImage.width.toString())
                            intent.putExtra("pixels", resource.density.toString())
                            intent.putExtra("image_size", resource.byteCount.toString())
                            startActivity(intent)

                        } catch (e: java.lang.Exception) {

                            val intent =
                                Intent(
                                    this@MainScreenActivity,
                                    BackgroundWallpaperListActivity::class.java
                                )
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

override fun itemClick(category: Category) {

    count++
    if (count == 3) {

        count = 0
        mInterstitialAd?.show(this)

        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()

                setAd()

                val intent = Intent(this@MainScreenActivity, MotionLayoutListActivity::class.java)
                intent.putExtra("name_category", category.name)
                intent.putExtra("desc_category", category.description)
                startActivity(intent)
            }
        }
    } else {
        val intent = Intent(this@MainScreenActivity, MotionLayoutListActivity::class.java)
        intent.putExtra("name_category", category.name)
        intent.putExtra("desc_category", category.description)
        startActivity(intent)

    }

}

private fun setAd() {

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