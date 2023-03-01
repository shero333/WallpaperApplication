package com.example.wallpaperapplication.one_off_screens.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wallpaperapplication.R
import com.example.wallpaperapplication.models.InterestUser

class RecyclerlistAdapter(
    private var interestList: ArrayList<InterestUser>,
    private var itemSelected: ItemSelected
) : RecyclerView.Adapter<RecyclerlistAdapter.ListAdapterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAdapterViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.items_view, parent, false)
        return ListAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListAdapterViewHolder, position: Int) {

        holder.itemName.text = interestList[position].getName()

        if (interestList[position].getSelected()) {
            holder.selectionImage.setImageResource(R.drawable.selected)
        } else {
            holder.selectionImage.setImageResource(0)
        }

        holder.itemView.setOnClickListener {
            interestList[position].setSelected(!interestList[position].getSelected())
            notifyItemChanged(position)

            itemSelected.selectedItemName(interestList[position])
        }
    }

    override fun getItemCount(): Int {
        return interestList.size
    }

    class ListAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val itemName: AppCompatTextView = itemView.findViewById(R.id.listItem)
        val selectionImage: AppCompatImageView = itemView.findViewById(R.id.selection_image)

    }

    interface ItemSelected {
        fun selectedItemName(interest: InterestUser)
    }
}