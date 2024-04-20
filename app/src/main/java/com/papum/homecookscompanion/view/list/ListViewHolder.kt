package com.papum.homecookscompanion.view.list

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.papum.homecookscompanion.R

class ListViewHolder(itemView:View) : ViewHolder(itemView) {
	val tvType:TextView		= itemView.findViewById(R.id.recycler_card_list_type)
    val tvName:TextView		= itemView.findViewById(R.id.recycler_card_list_text)
	val tvQuantity:TextView	= itemView.findViewById(R.id.recycler_card_list_quantity)
}