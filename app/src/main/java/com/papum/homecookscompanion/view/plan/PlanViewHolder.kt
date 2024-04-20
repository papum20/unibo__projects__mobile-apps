package com.papum.homecookscompanion.view.plan

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.papum.homecookscompanion.R

class PlanViewHolder(itemView:View) : ViewHolder(itemView) {
	val tvType:TextView		= itemView.findViewById(R.id.recycler_card_plan_type)
	val tvName:TextView		= itemView.findViewById(R.id.recycler_card_plan_text)
	val tvQuantity:TextView	= itemView.findViewById(R.id.recycler_card_plan_quantity)
}