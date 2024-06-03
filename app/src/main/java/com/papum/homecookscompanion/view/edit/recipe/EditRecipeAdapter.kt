package com.papum.homecookscompanion.view.edit.recipe

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
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.database.EntityList
import com.papum.homecookscompanion.model.database.EntityProduct
import com.papum.homecookscompanion.model.database.EntityProductAndIngredientOf
import com.papum.homecookscompanion.model.database.EntityProductAndList


class EditRecipeAdapter(
	var items: MutableList<EntityProductAndIngredientOf>,
	private val uiListener: IListenerEditRecipeItem
) : Adapter<EditRecipeViewHolder>() {


	interface IListenerEditRecipeItem {

		fun onClickInfo(product: EntityProduct)
		fun onClickRemove(id: Long, position: Int)
		fun onSetQuantity(id: Long, quantity: Float, position: Int)

	}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditRecipeViewHolder {
        return EditRecipeViewHolder(
            LayoutInflater.from(parent.context)
				.inflate(R.layout.recycler_card_edit_recipe, parent, false)
        )
    }

    override fun onBindViewHolder(holder: EditRecipeViewHolder, position: Int) {

		Log.d(TAG, "on bind ${holder.adapterPosition}")

		holder.etQuantity.setText(
			items.getOrNull(position)?.ingredientItem?.quantityMin.toString()
		)
		// associate item with EditText
		holder.etQuantity.tag = items[position]

		holder.tvName.text = items[position].let {
			it.product.parent.let { p ->
				"${it.product.name}, $p"
			} ?: it.product.name
		}

		holder.tvType.text = items[position].let {
			if(!it.product.isEdible)		"(NonEdible)"
			else if(it.product.isRecipe)	"(Recipe)"
			else							"(Food)"
		}


		/* UI listeners */

		// open product info and nutrients
		holder.layoutInfo.setOnClickListener { _ ->
			items[holder.adapterPosition].let { item ->
				uiListener.onClickInfo(item.product)
			}
		}

		// update ingredient quantities
		holder.etQuantity.setOnEditorActionListener { v, actionId, event ->
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				val item = v.tag as? EntityProductAndIngredientOf
				item?.let {
					(v as EditText).text.toString().toFloatOrNull()?.let { quantity ->
						uiListener.onSetQuantity(item.product.id, quantity, holder.adapterPosition)
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

		// remove ingredient
		holder.btnRemove.setOnClickListener { _ ->
			items.getOrNull(holder.adapterPosition)?.let { item ->
				uiListener.onClickRemove(item.product.id, holder.adapterPosition)
			}
		}

	}


    override fun getItemCount(): Int {
        return items.size
    }

	@SuppressLint("NotifyDataSetChanged")	// all fetched products change
	fun updateItems(newItems: List<EntityProductAndIngredientOf>) {
		items = newItems.toMutableList()
		notifyDataSetChanged()
		Log.d(TAG, "update all, new products: $itemCount")
	}



	companion object {

		private  const val TAG = "RECIPE_adapter"
	}

}