package com.papum.homecookscompanion.view.edit.scan

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.papum.homecookscompanion.R

class ScanViewHolder(itemView: View) : ViewHolder(itemView) {

	val tvRecognized:TextView		= itemView.findViewById(R.id.recycler_card_receipt_recognized)
	val tvRecognizedPrice:TextView	= itemView.findViewById(R.id.recycler_card_receipt_recognized_price)
	val tvProduct:TextView			= itemView.findViewById(R.id.recycler_card_receipt_product)
	val etPrice:EditText			= itemView.findViewById(R.id.recycler_card_receipt_product_price_et)
	val etQuantity:EditText			= itemView.findViewById(R.id.recycler_card_receipt_product_quantity_et)
	val btnSelectProduct:Button		= itemView.findViewById(R.id.recycler_card_receipt_btn_product)
	val btnRemove:Button			= itemView.findViewById(R.id.recycler_card_receipt_btn_remove)
}