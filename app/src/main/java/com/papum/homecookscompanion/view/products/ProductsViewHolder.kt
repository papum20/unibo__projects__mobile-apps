package com.papum.homecookscompanion.view.products

import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.Constraint
import androidx.constraintlayout.widget.Constraints
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.papum.homecookscompanion.R

class ProductsViewHolder(itemView: View) : ViewHolder(itemView) {

	val layoutInfo:ConstraintLayout		= itemView.findViewById(R.id.recycler_productExpandable_layout_info)
	val layoutCollapse:ConstraintLayout	= itemView.findViewById(R.id.recycler_productExpandable_layout_collapse)

	val icon:ImageView			= itemView.findViewById(R.id.recycler_productExpandable_img_type)

    val tvName:TextView			= itemView.findViewById(R.id.recycler_productExpandable_text)

	val btnExpand:ImageButton		= itemView.findViewById(R.id.recycler_productExpandable_btn_expand)
	val btnAddInventory:ImageButton	= itemView.findViewById(R.id.recycler_productExpandable_btn_addInventory)
    val btnAddList:ImageButton		= itemView.findViewById(R.id.recycler_productExpandable_btn_addList)
    val btnAddMeals:ImageButton		= itemView.findViewById(R.id.recycler_productExpandable_btn_addMeals)
}