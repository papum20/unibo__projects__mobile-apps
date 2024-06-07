package com.papum.homecookscompanion.view.meals

import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.papum.homecookscompanion.R

class MealsViewHolder(itemView:View) : ViewHolder(itemView) {

	val icon: ImageView = itemView.findViewById(R.id.recycler_card_meals_img_type)

	val tvMeasure:TextView	= itemView.findViewById(R.id.recycler_card_meals_measure)
	val tvName:TextView		= itemView.findViewById(R.id.recycler_card_meals_text)
	val etQuantity:EditText	= itemView.findViewById(R.id.recycler_card_meals_quantity)
	val tvTime:TextView		= itemView.findViewById(R.id.recycler_card_meals_time)

	val tvKcal:TextView	= itemView.findViewById(R.id.recycler_card_meals_kcal)
	val tvCarb:TextView	= itemView.findViewById(R.id.recycler_card_meals_carb)
	val tvFats:TextView	= itemView.findViewById(R.id.recycler_card_meals_fats)
	val tvProt:TextView	= itemView.findViewById(R.id.recycler_card_meals_prot)
}