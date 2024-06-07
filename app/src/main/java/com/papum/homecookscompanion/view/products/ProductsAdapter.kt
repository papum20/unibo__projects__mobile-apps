package com.papum.homecookscompanion.view.products

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.database.EntityProduct
import com.papum.homecookscompanion.utils.UtilViewProduct


class ProductsAdapter(
	private var items: List<EntityProduct>?,
	private val buttonsListener: IListenerOnClickProductExpandable
) : Adapter<ProductsViewHolder>() {

	private var layoutExpanded: ConstraintLayout? = null	// item's layout currently expanded (max 1 is expanded at a time)


	interface IListenerOnClickProductExpandable {

		fun onClickInfo(product: EntityProduct)
		fun onClickAddToInventory(product: EntityProduct)
		fun onClickAddToList(product: EntityProduct)
		fun onClickAddToMeals(product: EntityProduct)

	}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        return ProductsViewHolder(
            LayoutInflater.from(parent.context)
				.inflate(R.layout.recycler_card_products_expandable, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {

		/* fill fields */
		items?.getOrNull(position)?.let {
			UtilViewProduct.set(it, holder.tvName, holder.icon)
		}

		/* buttons listeners */

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
			items?.getOrNull(holder.adapterPosition)?.let { product ->
				buttonsListener.onClickInfo(product)
			}
		}

		// add product to inventory
		holder.btnAddInventory.setOnClickListener { _ ->
			items?.getOrNull(holder.adapterPosition)?.let { product ->
				buttonsListener.onClickAddToInventory(product)
			}
		}
		// add product to list
		holder.btnAddList.setOnClickListener { _ ->
			items?.getOrNull(holder.adapterPosition)?.let { product ->
				buttonsListener.onClickAddToList(product)
			}
		}
		// add product to meals
		holder.btnAddMeals.setOnClickListener { _ ->
			items?.getOrNull(holder.adapterPosition)?.let { product ->
				buttonsListener.onClickAddToMeals(product)
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

}