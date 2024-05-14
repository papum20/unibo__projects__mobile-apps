package com.papum.homecookscompanion.view.inventory

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.papum.homecookscompanion.R

class ShopsViewHolder(itemView: View) : ViewHolder(itemView) {

	val tvAddress: TextView	= itemView.findViewById(R.id.recycler_card_address)
	val tvBrand: TextView	= itemView.findViewById(R.id.recycler_card_brand)
	val tvCity: TextView	= itemView.findViewById(R.id.recycler_card_city)
	val tvLatLng: TextView	= itemView.findViewById(R.id.recycler_card_latlng)
	val tvState: TextView	= itemView.findViewById(R.id.recycler_card_state)
}