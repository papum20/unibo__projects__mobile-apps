package com.papum.homecookscompanion.view.products

import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.papum.homecookscompanion.R

class ProductsViewHolder(itemView: View) : ViewHolder(itemView) {

	val layoutInfo:LinearLayout		= itemView.findViewById(R.id.recycler_card_products_layout_info)
	val layoutCollapse:LinearLayout	= itemView.findViewById(R.id.recycler_card_products_layout_collapse)

    val tvName:TextView			= itemView.findViewById(R.id.recycler_card_products_text)
    val tvType:TextView			= itemView.findViewById(R.id.recycler_card_products_type)

	val btnExpand:Button		= itemView.findViewById(R.id.recycler_card_products_btn_expand)
	val btnAddInventory:Button	= itemView.findViewById(R.id.recycler_card_products_btn_addInventory)
    val btnAddList:Button		= itemView.findViewById(R.id.recycler_card_products_btn_addList)
    val btnAddMeals:Button		= itemView.findViewById(R.id.recycler_card_products_btn_addMeals)
}