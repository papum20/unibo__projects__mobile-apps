package com.papum.homecookscompanion.view.products

import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.papum.homecookscompanion.R

class ProductsWithResultViewHolder(itemView: View) : ViewHolder(itemView) {

	val layoutInfo:ConstraintLayout	= itemView.findViewById(R.id.recycler_product_layout_info)

	val icon: ImageView 		= itemView.findViewById(R.id.recycler_product_img_type)

	val tvName:TextView			= itemView.findViewById(R.id.recycler_product_text)

	val btnSelect: ImageButton = itemView.findViewById(R.id.recycler_product_btn_select)
}