package com.papum.homecookscompanion.view.shops

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.database.EntityAlerts
import com.papum.homecookscompanion.model.database.EntityInventory
import com.papum.homecookscompanion.model.database.EntityProduct
import com.papum.homecookscompanion.model.database.EntityProductAndInventoryWithAlerts
import com.papum.homecookscompanion.model.database.EntityShops
import com.papum.homecookscompanion.view.inventory.ShopsViewHolder


class ShopsAdapter(
	var items:MutableList<EntityShops>?
) : Adapter<ShopsViewHolder>() {


	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopsViewHolder {
        return ShopsViewHolder(
            LayoutInflater.from(parent.context)
				.inflate(R.layout.recycler_card_shops, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ShopsViewHolder, position: Int) {

		items?.get(position)?.let { item ->
			holder.tvAddress.text	= item.address
			holder.tvBrand.text		= item.brand
			holder.tvCity.text		= item.city
			holder.tvLatLng.text	= holder.itemView.context.getString(
				R.string.shop_format_latlng, item.latitude, item.longitude
			)
			holder.tvState.text		= item.state


		}

    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

	@SuppressLint("NotifyDataSetChanged")	// all fetched products change
	fun updateItems(newItems: MutableList<EntityShops>) {
		items = newItems
		notifyDataSetChanged()
		Log.d("INVENTORY_UPDATE", "products: $itemCount")
	}

	/*
    fun addItem(newItem: ListItem) {
        items?.let {
			it.add(newItem)
			notifyItemInserted(it.size - 1)
		}
    }
	*/


	companion object {

		private  const val TAG = "SHOPS_ADAPTER"
	}

}