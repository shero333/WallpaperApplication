package com.example.wallpaperapplication.one_off_screens

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.wallpaperapplication.databinding.ActivityOneOffScreenDataBinding
import com.example.wallpaperapplication.main.MainScreenActivity
import com.example.wallpaperapplication.models.InterestUser
import com.example.wallpaperapplication.one_off_screens.adapter.RecyclerlistAdapter
import com.example.wallpaperapplication.preferences.PreferenceManager

class OneOffScreenData : AppCompatActivity(), RecyclerlistAdapter.ItemSelected {

    private lateinit var binding: ActivityOneOffScreenDataBinding
    private lateinit var interestAdapter: RecyclerlistAdapter
    private var interests: ArrayList<String> = ArrayList()
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOneOffScreenDataBinding.inflate(layoutInflater)

        setContentView(binding.root)

        preferenceManager = PreferenceManager(this)

        val interestNames = ArrayList<InterestUser>()

        interestNames.add(InterestUser("Nature", false))
        interestNames.add(InterestUser("Animations", false))
        interestNames.add(InterestUser("Amoled", false))
        interestNames.add(InterestUser("Illustrations", false))
        interestNames.add(InterestUser("Minimalist", false))
        interestNames.add(InterestUser("Vintage", false))
        interestNames.add(InterestUser("Art", false))
        interestNames.add(InterestUser("Abstract", false))

        interestAdapter = RecyclerlistAdapter(interestNames, this)

        val manager = GridLayoutManager(this, 2)

        binding.interestList.layoutManager = manager

        binding.interestList.setHasFixedSize(true)

        binding.interestList.scrollToPosition(interestAdapter.itemCount - 1)

        binding.interestList.adapter = interestAdapter

        binding.nextButton.setOnClickListener {

            preferenceManager.store_interests(interests)

            val intent = Intent(this@OneOffScreenData, MainScreenActivity::class.java)
            startActivity(intent)
            finish()

        }

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun selectedItemName(interest: InterestUser) {
        interests.add(interest.getName()!!)
    }
}