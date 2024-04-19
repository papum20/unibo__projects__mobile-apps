package com.papum.homecookscompanion.view.inventory

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.database.EntityProductAndInventory


class InventoryAdapter(var items:List<EntityProductAndInventory>?) : Adapter<InventoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        return InventoryViewHolder(
            LayoutInflater.from(parent.context)
				.inflate(R.layout.recycler_card_inventory, parent, false)
        )
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
		Log.d("INVENTORY_VIEW_HOLDER", "create at position ${position}")

		val name: String = items?.get(position)?.let {
			it.product?.parent?.let { p ->
				"${it.product.name}, $p"
			} ?: it.product.name
		} ?: "[wrong entry]"
		val type: String = items?.get(position)?.let {
			if(!it.product.isEdible)		"(NonEdible)"
			else if(it.product.isRecipe)	"(Recipe)"
			else							"(Food)"
		} ?: "(UnknownType)"
		val quantity: String = items?.get(position)?.inventoryItem?.quantity.toString() ?: "??"

		holder.tvType.text		= type
		holder.tvName.text		= name
		holder.tvQuantity.text	= quantity
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

	/*
    fun addItem(newItem: ListItem) {
        items?.let {
			it.add(newItem)
			notifyItemInserted(it.size - 1)
		}
    }

    fun deleteItem(name:String) {
		items?.let {
			val index = it.indexOf(
				it.find { item -> item.name == name }
			)
			it.removeAt(index)
			notifyItemRemoved(index)
		}
	}
	*/

}