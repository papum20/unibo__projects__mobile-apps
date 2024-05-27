package com.papum.homecookscompanion.view.edit.recipe

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.papum.homecookscompanion.R

class EditRecipeViewHolder(itemView:View) : ViewHolder(itemView) {


	val layoutInfo: ConstraintLayout = itemView.findViewById(R.id.recycler_card_editRecipe_layout_info)

	val tvMeasure:TextView	= itemView.findViewById(R.id.recycler_card_editRecipe_measure)
	val tvName:TextView		= itemView.findViewById(R.id.recycler_card_editRecipe_text)
	val etQuantity: EditText = itemView.findViewById(R.id.recycler_card_editRecipe_quantity)
	val tvType:TextView		= itemView.findViewById(R.id.recycler_card_editRecipe_type)

	val btnRemove: Button = itemView.findViewById(R.id.recycler_card_editRecipe_btn_remove)
}