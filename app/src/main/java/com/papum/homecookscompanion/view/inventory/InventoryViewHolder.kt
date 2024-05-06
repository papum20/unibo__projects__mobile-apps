package com.papum.homecookscompanion.view.inventory

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.papum.homecookscompanion.R

class InventoryViewHolder(itemView: View) : ViewHolder(itemView) {

	val layoutCollapse: LinearLayout = itemView.findViewById(R.id.recycler_card_inventory_layout_collapse)
	val layoutInfo: ConstraintLayout = itemView.findViewById(R.id.recycler_card_inventory_layout_info)

	val etAlert:EditText	= itemView.findViewById(R.id.recycler_card_inventory_alert)
	val tvMeasure:TextView	= itemView.findViewById(R.id.recycler_card_inventory_measure)
    val tvName:TextView		= itemView.findViewById(R.id.recycler_card_inventory_text)
    val etQuantity:EditText	= itemView.findViewById(R.id.recycler_card_inventory_quantity)
	val tvType:TextView		= itemView.findViewById(R.id.recycler_card_inventory_type)

	val btnExpand: Button		= itemView.findViewById(R.id.recycler_card_inventory_btn_expand)
	val btnAddList: Button		= itemView.findViewById(R.id.recycler_card_inventory_btn_addList)
	val btnAddMeals: Button		= itemView.findViewById(R.id.recycler_card_inventory_btn_addMeals)
}