package com.papum.homecookscompanion.view.inventory

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.papum.homecookscompanion.R

class InventoryViewHolder(itemView: View) : ViewHolder(itemView) {
	val tvType:TextView		= itemView.findViewById(R.id.recycler_card_inventory_type)
    val tvName:TextView		= itemView.findViewById(R.id.recycler_card_inventory_text)
    val tvQuantity:TextView	= itemView.findViewById(R.id.recycler_card_inventory_quantity)
}