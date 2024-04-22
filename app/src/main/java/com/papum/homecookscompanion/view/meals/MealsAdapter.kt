package com.papum.homecookscompanion.view.meals

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.database.EntityProductAndMeals
import java.time.LocalDateTime


class MealsAdapter(var items:List<EntityProductAndMeals>?) : Adapter<MealsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealsViewHolder {
        return MealsViewHolder(
            LayoutInflater.from(parent.context)
				.inflate(R.layout.recycler_card_meals, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MealsViewHolder, position: Int) {
		Log.d("MEALS_VIEW_HOLDER", "${items}; ${position}")

		items?.get(position)?.let { item ->
			val localDate: LocalDateTime = item.mealsItem.date

			holder.tvName.text = item.product.parent?.let { p ->
					"${item.product.name}, $p"
				} ?: item.product.name
			holder.tvType.text =
				if(!item.product.isEdible)		"(NonEdible)"
				else if(item.product.isRecipe)	"(Recipe)"
				else							"(Food)"
			holder.tvQuantity.text	= item.mealsItem.quantity.toString()
			holder.tvTime.text		= holder.itemView.context.getString( R.string.placeholder_product_time,
				localDate.hour, localDate.minute )
		}

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