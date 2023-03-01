package com.example.wallpaperapplication.main

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.app.WallpaperManager
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.*
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.example.wallpaperapplication.R
import com.example.wallpaperapplication.databinding.ActivityBackgroundWallpaperListBinding
import com.example.wallpaperapplication.main.viewmodel.SharedViewModelLocalStore
import com.example.wallpaperapplication.models.RoomImageModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


@AndroidEntryPoint

class BackgroundWallpaperListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBackgroundWallpaperListBinding
    private lateinit var SharedviewModel: SharedViewModelLocalStore
    private var count = 0
    private var count1 = 0
    private var count2 = 0
    private var mInterstitialAd: InterstitialAd? = null
    private lateinit var progressDialog:ProgressDialog

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBackgroundWallpaperListBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setAd()

        SharedviewModel = ViewModelProvider(this)[SharedViewModelLocalStore::class.java]

        val dialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)

        val intentimage = intent.getStringExtra("image")
        val intentname = intent.getStringExtra("name")
        val intentdate = intent.getStringExtra("date")
        val intentpublisher = intent.getStringExtra("created_by")
        val intentheight = intent.getStringExtra("height")
        val intentwidth = intent.getStringExtra("width")
        val intentpixels = intent.getStringExtra("pixels")
        val intentsizeImage = intent.getStringExtra("image_size")


        progressDialog = ProgressDialog(this, R.style.AppCompatAlertDialogStyle)
        progressDialog.setMessage("Loading image.....")
        progressDialog.show()


        Glide.with(this)
            .asBitmap()
            .load(intentimage)
            .listener(object : RequestListener<Bitmap>{
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?,
                                          isFirstResource: Boolean): Boolean {
                    //if image fails to load
                    return true
                }

                override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?,
                                             isFirstResource: Boolean): Boolean {

                    progressDialog.dismiss()
                    return false
                }

            })
            .into(binding.image)



        binding.infoButton.setOnClickListener {

            val dialogue = Dialog(this)
            dialogue.setContentView(R.layout.image_details)
            dialogue.setCancelable(false)

            val close = dialogue.findViewById<AppCompatTextView>(R.id.close)
            val url = dialogue.findViewById<AppCompatTextView>(R.id.url)
            val date = dialogue.findViewById<AppCompatTextView>(R.id.date)
            val creator = dialogue.findViewById<AppCompatTextView>(R.id.creator)
            val size = dialogue.findViewById<AppCompatTextView>(R.id.size)
            val pixels = dialogue.findViewById<AppCompatTextView>(R.id.pixels)
            val publisher = dialogue.findViewById<AppCompatTextView>(R.id.publisher)
            val type_file_image = dialogue.findViewById<AppCompatTextView>(R.id.type_file)

            val fileName = ((Long.MAX_VALUE - System.currentTimeMillis()).toInt())

            url.text = intentimage
            date.text = intentdate
            creator.text = intentpublisher
            size.text = "$intentheight / $intentwidth ($intentsizeImage px)"
            pixels.text = intentpixels
            publisher.text = intentpublisher
            type_file_image.text = "$fileName.jpg"

            close?.setOnClickListener {
                dialogue.dismiss()
            }

            dialogue.show()

        }

        //fav listeners
        binding.saveButton.setOnClickListener {

            binding.saveButton.visibility = View.GONE
            binding.favButton.visibility = View.VISIBLE

            binding.favButton.playAnimation()

            count2++
            if (count2 == 3) {

                count2 = 0
                mInterstitialAd?.show(this)

                mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent()

                        setAd()

                        try {

                                SharedviewModel.LikeImage(
                                    intentimage,
                                    ((Long.MAX_VALUE - System.currentTimeMillis()).toInt()),
                                    intentname
                                )

                                val snackbar = Snackbar.make(
                                    binding.root, "Added to favourites!",
                                    Snackbar.LENGTH_LONG
                                ).setAction("Ok", null)
                                snackbar.setActionTextColor(Color.BLUE)
                                val snackbarView = snackbar.view
                                snackbarView.setBackgroundColor(resources.getColor(R.color.icon_splash_background))

                                val textView =
                                    snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView

                                textView.setTextColor(Color.WHITE)
                                textView.textSize = 20f
                                snackbar.show()


                        } catch (e: Exception) {

                                SharedviewModel.LikeImage(
                                    intentimage,
                                    ((Long.MAX_VALUE - System.currentTimeMillis()).toInt()),
                                    "No name"
                                )

                                val snackbar = Snackbar.make(
                                    binding.root, "Added to favourites!",
                                    Snackbar.LENGTH_LONG
                                ).setAction("Ok", null)
                                snackbar.setActionTextColor(Color.BLUE)
                                val snackbarView = snackbar.view
                                snackbarView.setBackgroundColor(resources.getColor(R.color.icon_splash_background))

                                val textView =
                                    snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView

                                textView.setTextColor(Color.WHITE)
                                textView.textSize = 20f
                                snackbar.show()



                        }

                    }
                }
            } else {

                try {

                    SharedviewModel.LikeImage(
                        intentimage,
                        ((Long.MAX_VALUE - System.currentTimeMillis()).toInt()),
                        intentname
                    )

                    val snackbar = Snackbar.make(
                        binding.root, "Added to favourites!",
                        Snackbar.LENGTH_LONG
                    ).setAction("Ok", null)
                    snackbar.setActionTextColor(Color.BLUE)
                    val snackbarView = snackbar.view
                    snackbarView.setBackgroundColor(resources.getColor(R.color.icon_splash_background))

                    val textView =
                        snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView

                    textView.setTextColor(Color.WHITE)
                    textView.textSize = 20f
                    snackbar.show()


                } catch (e: Exception) {

                    SharedviewModel.LikeImage(
                        intentimage,
                        ((Long.MAX_VALUE - System.currentTimeMillis()).toInt()),
                        "No name"
                    )

                    val snackbar = Snackbar.make(
                        binding.root, "Added to favourites!",
                        Snackbar.LENGTH_LONG
                    ).setAction("Ok", null)
                    snackbar.setActionTextColor(Color.BLUE)
                    val snackbarView = snackbar.view
                    snackbarView.setBackgroundColor(resources.getColor(R.color.icon_splash_background))

                    val textView =
                        snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView

                    textView.setTextColor(Color.WHITE)
                    textView.textSize = 20f
                    snackbar.show()

                }
            }

        }

        binding.favButton.setOnClickListener {
            binding.saveButton.setImageResource(R.drawable.heart)
            binding.saveButton.visibility = View.VISIBLE
            binding.favButton.visibility = View.GONE

            count1++
            if (count1 == 3) {

                count1 = 0
                mInterstitialAd?.show(this)

                mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent()

                        setAd()

                        try {

                            SharedviewModel.dislikeImage(
                                RoomImageModel(
                                    intentimage,
                                    intentname,
                                    ((Long.MAX_VALUE - System.currentTimeMillis()).toInt())
                                )
                            )


                            val snackbar = Snackbar.make(
                                binding.root, "Removed from favourites!",
                                Snackbar.LENGTH_LONG
                            ).setAction("Ok", null)
                            snackbar.setActionTextColor(Color.BLUE)
                            val snackbarView = snackbar.view
                            snackbarView.setBackgroundColor(resources.getColor(R.color.icon_splash_background))

                            val textView =
                                snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView

                            textView.setTextColor(Color.WHITE)
                            textView.textSize = 20f
                            snackbar.show()

                        } catch (e: Exception) {

                            SharedviewModel.dislikeImage(
                                RoomImageModel(
                                    intentimage,
                                    "No name",
                                    ((Long.MAX_VALUE - System.currentTimeMillis()).toInt())
                                )
                            )


                            val snackbar = Snackbar.make(
                                binding.root, "Removed from favourites!",
                                Snackbar.LENGTH_LONG
                            ).setAction("Ok", null)
                            snackbar.setActionTextColor(Color.BLUE)
                            val snackbarView = snackbar.view
                            snackbarView.setBackgroundColor(resources.getColor(R.color.icon_splash_background))

                            val textView =
                                snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView

                            textView.setTextColor(Color.WHITE)
                            textView.textSize = 20f
                            snackbar.show()
                        }

                    }
                }
            } else {
                try {

                    SharedviewModel.dislikeImage(
                        RoomImageModel(
                            intentimage,
                            intentname,
                            ((Long.MAX_VALUE - System.currentTimeMillis()).toInt())
                        )
                    )


                    val snackbar = Snackbar.make(
                        binding.root, "Removed from favourites!",
                        Snackbar.LENGTH_LONG
                    ).setAction("Ok", null)
                    snackbar.setActionTextColor(Color.BLUE)
                    val snackbarView = snackbar.view
                    snackbarView.setBackgroundColor(resources.getColor(R.color.icon_splash_background))

                    val textView =
                        snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView

                    textView.setTextColor(Color.WHITE)
                    textView.textSize = 20f
                    snackbar.show()

                } catch (e: Exception) {

                    SharedviewModel.dislikeImage(
                        RoomImageModel(
                            intentimage,
                            "No name",
                            ((Long.MAX_VALUE - System.currentTimeMillis()).toInt())
                        )
                    )


                    val snackbar = Snackbar.make(
                        binding.root, "Removed from favourites!",
                        Snackbar.LENGTH_LONG
                    ).setAction("Ok", null)
                    snackbar.setActionTextColor(Color.BLUE)
                    val snackbarView = snackbar.view
                    snackbarView.setBackgroundColor(resources.getColor(R.color.icon_splash_background))

                    val textView =
                        snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView

                    textView.setTextColor(Color.WHITE)
                    textView.textSize = 20f
                    snackbar.show()
                }

            }

        }

        //download image
        binding.downloadButton.setOnClickListener {

            count++
            if (count == 3) {

                count = 0
                mInterstitialAd?.show(this)

                mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent()

                        setAd()
                    }
                }

                Glide.with(this)
                    .asBitmap()
                    .load(intentimage)
                    .into(object : SimpleTarget<Bitmap?>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {

                            saveMediaToStorage(resource)

                        }
                    })
            } else {
                Glide.with(this)
                    .asBitmap()
                    .load(intentimage)
                    .into(object : SimpleTarget<Bitmap?>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap?>?
                        ) {

                            saveMediaToStorage(resource)

                        }
                    })
            }
        }

        //set wallpaper
        binding.setAsWallpaper.setOnClickListener {

            val wallpaperManager =
                WallpaperManager.getInstance(this@BackgroundWallpaperListActivity);

            Glide.with(this)
                .asBitmap()
                .load(intentimage)
                .into(object : SimpleTarget<Bitmap?>() {
                    @SuppressLint("InflateParams")
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap?>?
                    ) {


                        dialog.setContentView(R.layout.wallpaper_dialog_layout)
                        dialog.setCancelable(false)

                        val setBoth = dialog.findViewById<AppCompatTextView>(R.id.both)
                        val setHome = dialog.findViewById<AppCompatTextView>(R.id.home)
                        val setLock = dialog.findViewById<AppCompatTextView>(R.id.lock)
                        val cancel = dialog.findViewById<AppCompatTextView>(R.id.cancel_button)

                        cancel?.setOnClickListener {
                            dialog.dismiss()
                        }

                        setBoth?.setOnClickListener {

                            wallpaperManager.setBitmap(resource)

                            val snackbar = Snackbar.make(
                                binding.root, "Wallpaper applied to both successfully!",
                                Snackbar.LENGTH_LONG
                            ).setAction("Ok", null)
                            snackbar.setActionTextColor(Color.BLUE)
                            val snackbarView = snackbar.view
                            snackbarView.setBackgroundColor(resources.getColor(R.color.icon_splash_background))

                            val textView =
                                snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView

                            textView.setTextColor(Color.WHITE)
                            textView.textSize = 20f
                            snackbar.show()
                            dialog.dismiss()
                        }

                        setHome?.setOnClickListener {

                            wallpaperManager.setBitmap(
                                resource,
                                null,
                                true,
                                WallpaperManager.FLAG_SYSTEM
                            )


                            val snackbar = Snackbar.make(
                                binding.root, "Wallpaper applied to Home successfully!",
                                Snackbar.LENGTH_LONG
                            ).setAction("Ok", null)
                            snackbar.setActionTextColor(Color.BLUE)
                            val snackbarView = snackbar.view
                            snackbarView.setBackgroundColor(resources.getColor(R.color.icon_splash_background))

                            val textView =
                                snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView

                            textView.setTextColor(Color.WHITE)
                            textView.textSize = 20f
                            snackbar.show()
                            dialog.dismiss()
                        }

                        setLock?.setOnClickListener {

                            wallpaperManager.setBitmap(
                                resource,
                                null,
                                true,
                                WallpaperManager.FLAG_LOCK
                            )

                            val snackbar = Snackbar.make(
                                binding.root, "Wallpaper applied to Lock successfully!",
                                Snackbar.LENGTH_LONG
                            ).setAction("Ok", null)

                            snackbar.setActionTextColor(Color.BLUE)
                            val snackbarView = snackbar.view
                            snackbarView.setBackgroundColor(resources.getColor(R.color.icon_splash_background))

                            val textView =
                                snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView

                            textView.setTextColor(Color.WHITE)
                            textView.textSize = 20f
                            snackbar.show()
                            dialog.dismiss()
                        }

                        dialog.show()

                    }
                })
        }


        //internet connectivity
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

                startActivity(Intent(this@BackgroundWallpaperListActivity,MainScreenActivity::class.java))
                finish()
            }
        }

        val connectivityManager = getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        connectivityManager.requestNetwork(networkRequest, networkCallback)

    }


    fun saveMediaToStorage(bitmap: Bitmap) {
        //Generating a file name
        val filename = "${System.currentTimeMillis()}.jpg"

        //Output stream
        var fos: OutputStream? = null

        //For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //getting the contentResolver
            this.contentResolver?.also { resolver ->

                //Content resolver will process the contentvalues
                val contentValues = ContentValues().apply {

                    //putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                //Inserting the contentValues to contentResolver and getting the Uri
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                //Opening an outputstream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            //These for devices running on android < Q
            //So I don't think an explanation is needed here
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            //Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)

            val snackbar = Snackbar.make(
                binding.root, "Saved to gallery!",
                Snackbar.LENGTH_LONG
            ).setAction("Ok", null)
            snackbar.setActionTextColor(Color.BLUE)
            val snackbarView = snackbar.view
            snackbarView.setBackgroundColor(resources.getColor(R.color.icon_splash_background))

            val textView =
                snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView

            textView.setTextColor(Color.WHITE)
            textView.textSize = 20f
            snackbar.show()
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