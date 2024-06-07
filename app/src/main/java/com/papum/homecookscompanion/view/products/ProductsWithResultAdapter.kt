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


class ProductsWithResultAdapter(
	private var items: List<EntityProduct>?,
	private val buttonsListener: IListenerOnClickProduct
) : Adapter<ProductsWithResultViewHolder>() {

	private var layoutExpanded: ConstraintLayout? = null	// item's layout currently expanded (max 1 is expanded at a time)


	interface IListenerOnClickProduct {

		fun onClickInfo(product: EntityProduct)
		fun onClickSelect(product: EntityProduct)
	}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsWithResultViewHolder {
        return ProductsWithResultViewHolder(
            LayoutInflater.from(parent.context)
				.inflate(R.layout.recycler_card_products, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ProductsWithResultViewHolder, position: Int) {

		/* fill fields */
		items?.getOrNull(position)?.let {
			UtilViewProduct.set(it, holder.tvName, holder.icon)
		}

		/* buttons listeners */

		// open product info and nutrients
		holder.layoutInfo.setOnClickListener { _ ->
			items?.getOrNull(holder.adapterPosition)?.let { product ->
				buttonsListener.onClickInfo(product)
			}
		}

		holder.btnSelect.setOnClickListener {
			items?.getOrNull(holder.adapterPosition)?.let { product ->
				buttonsListener.onClickSelect(product)
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