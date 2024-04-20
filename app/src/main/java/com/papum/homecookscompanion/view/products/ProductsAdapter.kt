package com.papum.homecookscompanion.view.products

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.database.EntityProduct


class ProductsAdapter(
	private var items: List<EntityProduct>?,
	private val buttonsListener: IListenerOnClickProduct
) : Adapter<ProductsViewHolder>() {

	private var layoutExpanded: LinearLayout? = null	// item's layout currently expanded (max 1 is expanded at a time)


	interface IListenerOnClickProduct {

		fun onClickAddToInventory(product: EntityProduct)
		fun onClickAddToList(product: EntityProduct)
		fun onClickAddToPlan(product: EntityProduct)

	}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        return ProductsViewHolder(
            LayoutInflater.from(parent.context)
				.inflate(R.layout.recycler_card_products, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
		Log.d("PRODUCTS_VIEW_HOLDER", "create at position ${position}")

		/* fill fields */
		val name: String = items?.get(position)?.let {
			it.parent?.let { p ->
				"${it.name}, $p"
			} ?: it.name
		} ?: "[wrong entry]"
		val type: String = items?.get(position)?.let {
			if(!it.isEdible)		"(NonEdible)"
			else if(it.isRecipe)	"(Recipe)"
			else					"(Food)"
		} ?: ("UnknownType")

		holder.tvType.text = type
		holder.tvName.text = name

		/* buttons listeners */

		// expand/collapse
		holder.layoutInfo.setOnClickListener { _ ->
			when(holder.layoutCollapse.visibility) {
				View.GONE	-> {
					layoutExpanded?.visibility			= View.GONE
					holder.layoutCollapse.visibility	= View.VISIBLE
					layoutExpanded = holder.layoutCollapse
				}
				else		-> holder.layoutCollapse.visibility = View.GONE
			}
		}

		items?.get(position)?.let { product ->
			// add product to inventory
			holder.btnAddInventory.setOnClickListener { _ ->
				buttonsListener.onClickAddToInventory(product)
			}
			// add product to list
			holder.btnAddList.setOnClickListener { _ ->
				buttonsListener.onClickAddToList(product)
			}
			// add product to plan
			holder.btnAddPlan.setOnClickListener { _ ->
				buttonsListener.onClickAddToPlan(product)
			}
		}

    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

	@SuppressLint("NotifyDataSetChanged")	// all fetched products change
	fun updateItems(newItems: List<EntityProduct>) {
		layoutExpanded?.visibility = View.GONE
		layoutExpanded = null
		items = newItems
		notifyDataSetChanged()
		Log.d("PRODUCTS_UPDATE", "products: $itemCount")
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