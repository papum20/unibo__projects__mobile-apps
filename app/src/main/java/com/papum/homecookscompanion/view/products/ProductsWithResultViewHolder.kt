package com.papum.homecookscompanion.view.products

import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.papum.homecookscompanion.R

class ProductsWithResultViewHolder(itemView: View) : ViewHolder(itemView) {

	val layoutInfo:LinearLayout	= itemView.findViewById(R.id.recycler_product_layout_info)

    val tvName:TextView			= itemView.findViewById(R.id.recycler_product_text)
    val tvType:TextView			= itemView.findViewById(R.id.recycler_product_type)

	val btnSelect:Button		= itemView.findViewById(R.id.recycler_product_btn_select)
}