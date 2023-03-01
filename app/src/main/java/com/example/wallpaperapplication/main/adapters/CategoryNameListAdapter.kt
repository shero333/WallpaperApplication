package com.example.wallpaperapplication.main.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wallpaperapplication.R
import com.example.wallpaperapplication.models.CategoryImage

class CategoryNameListAdapter(
    private var activity: Activity,
    private var selected: CategorySelected
):RecyclerView.Adapter<CategoryNameListAdapter.NameListAdapterViewHolder>() {

    var categoryList: ArrayList<CategoryImage>?=null
    var selected_category_position = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NameListAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_name_itemview,parent,false)

        return NameListAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: NameListAdapterViewHolder, position: Int) {

        val itemPosition = position

        holder.categoryName.text = categoryList!![position].getName()

        changeItemBackground(
            holder,
            categoryList!![position].getIsClicked() && selected_category_position == position
        )


        holder.itemView.setOnClickListener {

            if (selected_category_position != itemPosition) {
                selected_category_position = itemPosition
                selected.categoryChoosen(categoryList!![position],itemPosition)
            }
        }

    }

    override fun getItemCount(): Int {
        return if (categoryList != null)
            categoryList!!.size
        else
            0
    }

    @SuppressLint("NotifyDataSetChanged")
    fun changeSelectionStatus(position: Int) {
        for (category in categoryList!!) {
            category.setIsClicked(selected_category_position == position)
        }
        notifyDataSetChanged()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun changeItemBackground(
        holder: NameListAdapterViewHolder,
        isSelected: Boolean
    ) {
        if (isSelected) {
            holder.categoryName.isSelected = true
            holder.categoryName.setTextColor(activity.resources.getColor(R.color.icon_splash_background))

        } else {
            holder.categoryName.isSelected = false
            holder.categoryName.setTextColor(Color.BLACK)
        }
    }

    class NameListAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val categoryName : AppCompatTextView = itemView.findViewById(R.id.listItem_categoryName)
    }

    interface CategorySelected{

        fun categoryChoosen(nameCategory:CategoryImage,itemPosition: Int)
    }
}