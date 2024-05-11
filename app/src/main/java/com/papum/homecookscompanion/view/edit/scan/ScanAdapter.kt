package com.papum.homecookscompanion.view.edit.scan

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.papum.homecookscompanion.R


class ScanAdapter(
	private var items: MutableList<ScanModel>?,
	private val buttonsListener: IListenerOnClickReceiptEntry
) : Adapter<ScanViewHolder>() {

	private var layoutExpanded: LinearLayout? = null	// item's layout currently expanded (max 1 is expanded at a time)


	interface IListenerOnClickReceiptEntry {
		fun onClickRemove(position: Int)
		fun onClickSelectProduct()
	}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanViewHolder {
        return ScanViewHolder(
            LayoutInflater.from(parent.context)
				.inflate(R.layout.recycler_card_receipt, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ScanViewHolder, position: Int) {

		items?.get(position)?.let { item ->
			holder.tvRecognized.text		= item.recognizedProduct
			holder.tvRecognizedPrice.text	= item.recognizedPrice.toString()
			holder.tvProduct.text			= item.product
			holder.etPrice.setText(item.recognizedPrice.toString())
		}

		/* UI listeners */
		holder.btnSelectProduct.setOnClickListener { _ ->

		}

		holder.btnRemove.setOnClickListener { _ ->
			buttonsListener.onClickRemove(holder.adapterPosition)
		}

		holder.etPrice.doOnTextChanged { text, start, before, count ->
			items?.get(holder.adapterPosition)?.let { item ->
				item.price = text.toString().toFloatOrNull()
			}
		}

    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }



	fun updateItems(newItems: MutableList<ScanModel>) {
		items = newItems
		notifyDataSetChanged()
		Log.d("RECEIPT_UPDATE", "items_n: $itemCount")
	}

    fun addItem(newItem: ScanModel) {
        items?.let {
			it.add(newItem)
			notifyItemInserted(it.size - 1)
		}
    }

    fun deleteItem(index: Int) {
		items?.let {
			it.removeAt(index)
			notifyItemRemoved(index)
		}
	}


}