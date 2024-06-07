package com.papum.homecookscompanion.utils

import android.widget.ImageView
import android.widget.TextView
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.database.EntityProduct

object UtilViewProduct {

	fun set(product: EntityProduct, tvName: TextView, imgType: ImageView) {
		/* fill fields */
		tvName.text = "${product.name}, ${product.parent}"
		imgType.setImageResource(
			if(!product.isEdible)		R.drawable.package_128
			else if(product.isRecipe)	R.drawable.salad_128
			else						R.drawable.vegetable_128
		)
	}

}