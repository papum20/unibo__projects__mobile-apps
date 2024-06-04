package com.papum.homecookscompanion.view.list

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.database.EntityAlerts
import com.papum.homecookscompanion.model.database.EntityInventory
import com.papum.homecookscompanion.model.database.EntityList
import com.papum.homecookscompanion.model.database.EntityProduct
import com.papum.homecookscompanion.model.database.EntityProductAndList
import com.papum.homecookscompanion.view.inventory.InventoryAdapter


class ListAdapter(
	var items: MutableList<EntityProductAndList>,
	private val uiListener: IListenerListItem
) : Adapter<ListViewHolder>() {

	private var layoutExpanded: LinearLayout? = null	// item's layout currently expanded (max 1 is expanded at a time)


	interface IListenerListItem {

		fun onClickInfo(product: EntityProduct)
		fun onClickRemove(listItem: EntityList)

		/**
		 * `id` used to create entry if not existing in inventory.
		 */
		fun onSetQuantity(id: Long, listItem: EntityList?, quantity: Float)

	}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(
            LayoutInflater.from(parent.context)
				.inflate(R.layout.recycler_card_list, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {

		holder.etQuantity.setText(items.getOrNull(position)?.listItem?.let { item ->
				item.quantity.toString()
			}?: "0.00"
		)
		// associate item with EditText
		holder.etQuantity.tag = items[position]

		holder.tvName.text = items.getOrNull(position)?.let {
			it.product.parent.let { p ->
				"${it.product.name}, $p"
			}
		} ?: "[wrong entry]"

		holder.tvType.text = items.getOrNull(position)?.let {
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
			items.getOrNull(holder.adapterPosition)?.let { item ->
				uiListener.onClickInfo(item.product)
			}
		}

		// update product quantity for list
		holder.etQuantity.setOnEditorActionListener { v, actionId, event ->
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				val item = v.tag as? EntityProductAndList
				item?.let {
					(v as EditText).text.toString().toFloatOrNull()?.let { quantity ->
						uiListener.onSetQuantity(item.product.id, item.listItem, quantity)
					}
					// hide keyboard
					val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
					imm.hideSoftInputFromWindow(v.windowToken, 0)
				}
				true
			} else {
				false
			}
		}


		// remove product from list
		holder.btnRemove.setOnClickListener { _ ->
			items.getOrNull(holder.adapterPosition)?.let { item ->
				uiListener.onClickRemove(item.listItem)
			}
		}

	}

    override fun getItemCount(): Int {
        return items.size
    }

	@SuppressLint("NotifyDataSetChanged")	// all fetched products change
	fun updateItems(newItems: MutableList<EntityProductAndList>) {
		layoutExpanded?.visibility = View.GONE
		layoutExpanded = null
		items = newItems
		notifyDataSetChanged()
		Log.d(TAG, "new products: $itemCount")
	}

    fun deleteItem(productId: Long) {
		items.find { item -> item.product.id == productId }?.let { foundItem ->
			items.indexOf(foundItem).let { position ->
				if(position != -1) {
					items.removeAt(position)
					notifyItemRemoved(position)
				}
			}
		}
	}

	fun updateItemInList(newListItem: EntityList) {
		items.find { item -> item.product.id == newListItem.idProduct }?.let { foundItem ->
			items.indexOf(foundItem).let { position ->
				layoutExpanded?.visibility = View.GONE
				layoutExpanded = null
				if(position != -1) {
					items[position] = foundItem.apply { listItem = newListItem }
					notifyItemChanged(position)
					Log.d(TAG, "Updated item at position $position; products: $itemCount")
				}
			}
		}
	}


	companion object {

		private  const val TAG = "LIST_adapter"
	}

}