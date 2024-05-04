package com.papum.homecookscompanion.view.inventory

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.database.EntityProduct
import com.papum.homecookscompanion.model.database.EntityProductAndInventoryWithAlerts
import com.papum.homecookscompanion.view.products.ProductsAdapter


class InventoryAdapter(
	var items:List<EntityProductAndInventoryWithAlerts>?,
	private val buttonsListener: InventoryAdapter.IListenerOnClickInventoryItem
) : Adapter<InventoryViewHolder>() {

	private var layoutExpanded: LinearLayout? = null	// item's layout currently expanded (max 1 is expanded at a time)


	interface IListenerOnClickInventoryItem {

		fun onClickInfo(product: EntityProduct)
		fun onClickAddToInventory(product: EntityProduct)
		fun onClickAddToList(product: EntityProduct)
		fun onClickAddToMeals(product: EntityProduct)

	}


	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        return InventoryViewHolder(
            LayoutInflater.from(parent.context)
				.inflate(R.layout.recycler_card_inventory, parent, false)
        )
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
		Log.d("INVENTORY_VIEW_HOLDER", "create at position ${position}")

		holder.tvAlert.text = items?.get(position)?.let {
			it.alert?.let { alert ->
				alert.quantity.toString()
			} ?: "(none)"
		}

		holder.tvName.text = items?.get(position)?.let {
			it.product.parent?.let { p ->
				"${it.product.name}, $p"
			} ?: it.product.name
		} ?: "[wrong entry]"

		holder.etQuantity.text = items?.get(position)?.inventoryItem?.let { item ->
			item.quantity.toString()
		}?: "??"

		holder.tvType.text = items?.get(position)?.let {
			if(!it.product.isEdible)		"(NonEdible)"
			else if(it.product.isRecipe)	"(Recipe)"
			else							"(Food)"
		} ?: "(UnknownType)"

		/* UI listeners */

		// expand/collapse
		holder.btnExpand.setOnClickListener { _ ->
			when(holder.layoutCollapse.visibility) {
				View.GONE	-> {
					layoutExpanded?.visibility			= View.GONE
					holder.layoutCollapse.visibility	= View.VISIBLE
					layoutExpanded = holder.layoutCollapse
				}
				else		-> holder.layoutCollapse.visibility = View.GONE
			}
		}

		items?.get(position)?.let { item ->
			// open product info and nutrients
			holder.layoutInfo.setOnClickListener { _ ->
				buttonsListener.onClickInfo(item.product)
			}

			// add product to inventory
			holder.etQuantity.doOnTextChanged { text, start, before, count ->
				buttonsListener.onClickAddToInventory(item)
			}

			// add product to list
			holder.btnAddList.setOnClickListener { _ ->
				buttonsListener.onClickAddToList(item)
			}
			// add product to meals
			holder.btnAddMeals.setOnClickListener { _ ->
				buttonsListener.onClickAddToMeals(item)
			}
		}

    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

	@SuppressLint("NotifyDataSetChanged")	// all fetched products change
	fun updateItems(newItems: List<EntityProductAndInventoryWithAlerts>) {
		layoutExpanded?.visibility = View.GONE
		layoutExpanded = null
		items = newItems
		notifyDataSetChanged()
		Log.d("INVENTORY_UPDATE", "products: $itemCount")
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