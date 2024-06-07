package com.papum.homecookscompanion.view.inventory

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.papum.homecookscompanion.R

class InventoryViewHolder(itemView: View) : ViewHolder(itemView) {

	val layoutCollapse: ConstraintLayout	= itemView.findViewById(R.id.recycler_card_inventory_layout_collapse)
	val layoutInfo: ConstraintLayout		= itemView.findViewById(R.id.recycler_card_inventory_layout_info)

	val icon: ImageView = itemView.findViewById(R.id.recycler_card_inventory_img_type)

	val etAlert:EditText	= itemView.findViewById(R.id.recycler_card_inventory_alert)
	val tvMeasure:TextView	= itemView.findViewById(R.id.recycler_card_inventory_measure)
    val tvName:TextView		= itemView.findViewById(R.id.recycler_card_inventory_text)
    val etQuantity:EditText	= itemView.findViewById(R.id.recycler_card_inventory_quantity)

	val btnExpand: ImageButton		= itemView.findViewById(R.id.recycler_card_inventory_btn_expand)
	val btnAddList: ImageButton		= itemView.findViewById(R.id.recycler_card_inventory_btn_addList)
	val btnAddMeals: ImageButton	= itemView.findViewById(R.id.recycler_card_inventory_btn_addMeals)
	val btnRemove: ImageButton 		= itemView.findViewById(R.id.recycler_card_inventory_btn_remove)
}