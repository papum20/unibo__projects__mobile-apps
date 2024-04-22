package com.papum.homecookscompanion.view.meals

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.papum.homecookscompanion.R

class MealsViewHolder(itemView:View) : ViewHolder(itemView) {
	val tvType:TextView		= itemView.findViewById(R.id.recycler_card_meals_type)
	val tvName:TextView		= itemView.findViewById(R.id.recycler_card_meals_text)
	val tvQuantity:TextView	= itemView.findViewById(R.id.recycler_card_meals_quantity)
	val tvTime:TextView		= itemView.findViewById(R.id.recycler_card_meals_time)
}