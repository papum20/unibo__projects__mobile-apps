package com.papum.homecookscompanion.list

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.papum.homecookscompanion.R

class ListViewHolder(itemView:View) : ViewHolder(itemView) {
    val tvItemName:TextView = itemView.findViewById(R.id.recycler_card_list_item_text)
}