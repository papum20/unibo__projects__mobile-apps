package com.papum.homecookscompanion.view.products

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.database.EntityProduct


class ProductsAdapter(var items:List<EntityProduct>?) : Adapter<ProductsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        return ProductsViewHolder(
            LayoutInflater.from(parent.context)
				.inflate(R.layout.recycler_card_products_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
		Log.d("PRODUCTS_VIEW_HOLDER", "create at position ${position}")

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