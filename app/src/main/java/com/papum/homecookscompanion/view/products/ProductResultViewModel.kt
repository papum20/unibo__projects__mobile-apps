package com.papum.homecookscompanion.view.products

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.papum.homecookscompanion.model.database.EntityProduct
import kotlin.jvm.Throws

class ProductResultViewModel : ViewModel() {

	private var _selectedProduct = MutableLiveData<EntityProduct?>()
	val selectedProduct: LiveData<EntityProduct?>
		get() = _selectedProduct



	/**
	 * Set the selected product to null.
	 * Call this right after reading its value, so the observer isn't
	 * called again on every view reconstruction.
	 */
	fun reset() {
		_selectedProduct.value = null
	}

	fun selectProduct(product: EntityProduct) {
		_selectedProduct.value = product
	}

}


/**
 * Factory for constructing ProductResultViewModel, scoping it to the provided activity.
 */
class ProductResultViewModelFactory(private val activity: FragmentActivity) : ViewModelProvider.Factory {

	@Throws(IllegalArgumentException::class)
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if(modelClass.isAssignableFrom(ProductResultViewModel::class.java)) {
			//@Suppress("UNCHECKED_CAST")
			return ViewModelProvider(activity)[ProductResultViewModel::class.java] as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}