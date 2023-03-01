package com.example.wallpaperapplication.privacy_policy

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.wallpaperapplication.R
import com.example.wallpaperapplication.databinding.ActivityPoliciesBinding
import com.google.android.material.snackbar.Snackbar

class PoliciesActivity : AppCompatActivity() {

    private lateinit var binding:ActivityPoliciesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPoliciesBinding.inflate(layoutInflater)

        setContentView(binding.root)
        binding.actionBar.privacyPolicy.visibility = View.GONE


        if (checkForInternet(this)){
            binding.policyPage.visibility = View.VISIBLE
            binding.noConnectionLayout.noConnectionLayout.visibility = View.GONE
        }else{
            binding.policyPage.visibility = View.GONE
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

                runOnUiThread {
                    binding.policyPage.visibility = View.VISIBLE
                    binding.noConnectionLayout.noConnectionLayout.visibility = View.GONE
                }
            }

            // Network capabilities have changed for the network
            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                super.onCapabilitiesChanged(network, networkCapabilities)

//                //snackbar
//                val snackbar = Snackbar.make(
//                    binding.root, "Network connection unstable!",
//                    Snackbar.LENGTH_LONG
//                ).setAction("Ok", null)
//                snackbar.setActionTextColor(Color.BLUE)
//                val snackbarView = snackbar.view
//                snackbarView.setBackgroundColor(resources.getColor(R.color.icon_splash_background))
//


//                val textView =
//                    snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
//
//                textView.setTextColor(Color.WHITE)
//                textView.textSize = 20f
//                snackbar.show()
            }

            // lost network connection
            override fun onLost(network: Network) {
                super.onLost(network)

                runOnUiThread {
                    binding.policyPage.visibility = View.GONE
                    binding.noConnectionLayout.noConnectionLayout.visibility = View.VISIBLE
                }
            }
        }

        val connectivityManager = getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        connectivityManager.requestNetwork(networkRequest, networkCallback)
        //Load URL

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

}