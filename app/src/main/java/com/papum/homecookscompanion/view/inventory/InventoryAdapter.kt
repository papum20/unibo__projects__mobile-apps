package com.papum.homecookscompanion.view.inventory

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.res.TypedArrayUtils
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.database.EntityAlerts
import com.papum.homecookscompanion.model.database.EntityInventory
import com.papum.homecookscompanion.model.database.EntityProduct
import com.papum.homecookscompanion.model.database.EntityProductAndInventoryWithAlerts


class InventoryAdapter(
	var items:MutableList<EntityProductAndInventoryWithAlerts>?,
	private val uiListener: InventoryAdapter.IListenerInventoryItem
) : Adapter<InventoryViewHolder>() {

	private var layoutExpanded: LinearLayout? = null	// item's layout currently expanded (max 1 is expanded at a time)


	interface IListenerInventoryItem {

		fun onClickInfo(product: EntityProduct)
		fun onClickAddToList(product: EntityProduct)
		fun onClickAddToMeals(product: EntityProduct)

		/**
		 * `id` used to create entry if not existing in inventory.
		 */
		fun onSetQuantity(id: Long, inventoryItem: EntityInventory?, quantity: Float)
		fun onSetAlert(id: Long, alert: EntityAlerts?, quantity: Float)

	}


	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        return InventoryViewHolder(
            LayoutInflater.from(parent.context)
				.inflate(R.layout.recycler_card_inventory, parent, false)
        )
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
		Log.d("INVENTORY_VIEW_HOLDER", "create at position ${position}")

		holder.etAlert.setText( items?.get(position)?.let {
				it.alert?.quantity?.toString() ?: "(none)"
			}
		)

		holder.etQuantity.setText(items?.get(position)?.inventoryItem?.let { item ->
			item.quantity.toString()
		}?: "0.00"
		)

		holder.tvName.text = items?.get(position)?.let {
			it.product.parent?.let { p ->
				"${it.product.name}, $p"
			} ?: it.product.name
		} ?: "[wrong entry]"

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

		// open product info and nutrients
		holder.layoutInfo.setOnClickListener { _ ->
			items?.get(holder.adapterPosition)?.let { item ->
				uiListener.onClickInfo(item.product)
			}
		}

		/*
		// update product quantity for alert
		holder.etAlert.setOnEditorActionListener { v, actionId, event ->
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				val quantity = v.text.toString().toFloatOrNull() ?: 0.0f
				uiListener.onSetAlert(item.product.id, item.alert, quantity)
			}
			true
		}
		*/

		// update product quantity for alert
		holder.etAlert.doOnTextChanged { text, start, before, count ->
			items?.get(holder.adapterPosition)?.let { item ->
				text.toString().toFloatOrNull()?.let { quantity ->
					uiListener.onSetAlert(item.product.id, item.alert, quantity)
				}
			}
		}

		// update product quantity to inventory
		holder.etQuantity.doOnTextChanged { text, start, before, count ->
			items?.get(holder.adapterPosition)?.let { item ->
				text.toString().toFloatOrNull()?.let { quantity ->
					uiListener.onSetQuantity(item.product.id, item.inventoryItem, quantity)
				}
			}
		}


		// add product to list
		holder.btnAddList.setOnClickListener { _ ->
			items?.get(holder.adapterPosition)?.let { item ->
				uiListener.onClickAddToList(item.product)
			}
		}
		// add product to meals
		holder.btnAddMeals.setOnClickListener { _ ->
			items?.get(holder.adapterPosition)?.let { item ->
				uiListener.onClickAddToMeals(item.product)
			}
		}

    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

	@SuppressLint("NotifyDataSetChanged")	// all fetched products change
	fun updateItems(newItems: MutableList<EntityProductAndInventoryWithAlerts>) {
		layoutExpanded?.visibility = View.GONE
		layoutExpanded = null
		items = newItems
		notifyDataSetChanged()
		Log.d("INVENTORY_UPDATE", "products: $itemCount")
	}

	fun updateItemInInventory(newInventoryItem: EntityInventory) {
		items?.find { item -> item.product.id == newInventoryItem.idProduct }?.let { foundItem ->
			items?.indexOf(foundItem)?.let { position ->
				layoutExpanded?.visibility = View.GONE
				layoutExpanded = null
				items?.set( position,
					foundItem.apply { inventoryItem = newInventoryItem }
				)
				notifyItemChanged(position)
				Log.d("TAG", "Updated item at position $position; products: $itemCount")
			}
		}
	}

	fun updateItemAlert(newAlert: EntityAlerts) {
		items?.find { item -> item.product.id == newAlert.idProduct }?.let { foundItem ->
			items?.indexOf(foundItem)?.let { position ->
				layoutExpanded?.visibility = View.GONE
				layoutExpanded = null
				items?.set( position,
					foundItem.apply { alert = newAlert }
				)
				notifyItemChanged(position)
				Log.d("TAG", "Updated item at position $position; products: $itemCount")
			}
		}
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


	companion object {

		private  const val TAG = "INVENTORY_adapter"
	}

}