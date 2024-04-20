package com.papum.homecookscompanion.view.plan

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.database.EntityProductAndList
import com.papum.homecookscompanion.model.database.EntityProductAndPlan


class PlanAdapter(var items:List<EntityProductAndPlan>?) : Adapter<PlanViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        return PlanViewHolder(
            LayoutInflater.from(parent.context)
				.inflate(R.layout.recycler_card_list, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
		Log.d("LIST_VIEW_HOLDER", "${items}; ${position}")

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
		val quantity: String = items?.get(position)?.planItem?.quantity.toString() ?: "??"

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