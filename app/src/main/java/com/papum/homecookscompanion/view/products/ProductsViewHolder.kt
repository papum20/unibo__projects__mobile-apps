package com.papum.homecookscompanion.view.products

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.papum.homecookscompanion.R

class ProductsViewHolder(itemView:View) : ViewHolder(itemView) {
    val tvItemName:TextView = itemView.findViewById(R.id.recycler_card_products_item_text)
}