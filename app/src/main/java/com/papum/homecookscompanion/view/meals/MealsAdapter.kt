package com.papum.homecookscompanion.view.meals

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.database.EntityList
import com.papum.homecookscompanion.model.database.EntityProduct
import com.papum.homecookscompanion.model.database.EntityProductAndList
import com.papum.homecookscompanion.model.database.EntityProductAndMealsWithNutrients
import com.papum.homecookscompanion.utils.UtilProducts
import com.papum.homecookscompanion.utils.UtilViewProduct
import com.papum.homecookscompanion.view.list.ListAdapter
import java.time.LocalDateTime


class MealsAdapter(
	var items:List<EntityProductAndMealsWithNutrients>,
	private val uiListener: IListenerMealsItem,
	val context: Context?
) : Adapter<MealsViewHolder>() {

	interface IListenerMealsItem {
		fun onSetQuantity(item: EntityProductAndMealsWithNutrients, quantity: Float)
	}


	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealsViewHolder {
        return MealsViewHolder(
            LayoutInflater.from(parent.context)
				.inflate(R.layout.recycler_card_meals, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MealsViewHolder, position: Int) {

		items.getOrNull(position)?.let { item ->

			/* fill fields */
			UtilViewProduct.set(item.product, holder.tvName, holder.icon)

			item.meal.date.let { date ->
				holder.tvTime.text = context?.getString(R.string.product_format_time,
					date.hour, date.minute)
			}
			holder.etQuantity.setText(
				context?.getString(R.string.product_format_quantity, item.meal.quantity) ?: "" )

			// associate item with EditText
			holder.etQuantity.tag = items.getOrNull(position)

			context?.let { context ->
				holder.tvKcal.text = UtilProducts.getKcalShort(context, item.nutrients.kcal)
				holder.tvCarb.text = UtilProducts.getCarbShort(context, item.nutrients.carbohydrates)
				holder.tvFats.text = UtilProducts.getFatsShort(context, item.nutrients.fats)
				holder.tvProt.text = UtilProducts.getProtShort(context, item.nutrients.proteins)
			}

		}


		/* UI listeners */

		// update product quantity for list
		holder.etQuantity.setOnEditorActionListener { v, actionId, event ->
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				val item = v.tag as? EntityProductAndMealsWithNutrients
				item?.let {
					(v as EditText).text.toString().toFloatOrNull()?.let { quantity ->
						uiListener.onSetQuantity(item, quantity)
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


	}

    override fun getItemCount(): Int {
        return items.size
    }

	fun updateAll(items: List<EntityProductAndMealsWithNutrients>) {
		this.items = items
		notifyDataSetChanged()
	}


}