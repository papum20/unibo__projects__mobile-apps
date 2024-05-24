package com.papum.homecookscompanion.view.list

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.papum.homecookscompanion.R

class ListViewHolder(itemView:View) : ViewHolder(itemView) {


	val layoutCollapse: LinearLayout = itemView.findViewById(R.id.recycler_card_list_layout_collapse)
	val layoutInfo: ConstraintLayout = itemView.findViewById(R.id.recycler_card_list_layout_info)

	val tvMeasure:TextView	= itemView.findViewById(R.id.recycler_card_list_measure)
	val tvName:TextView		= itemView.findViewById(R.id.recycler_card_list_text)
	val etQuantity: EditText = itemView.findViewById(R.id.recycler_card_list_quantity)
	val tvType:TextView		= itemView.findViewById(R.id.recycler_card_list_type)

	val btnExpand: Button = itemView.findViewById(R.id.recycler_card_list_btn_expand)
	val btnRemove: Button = itemView.findViewById(R.id.recycler_card_list_btn_remove)
}