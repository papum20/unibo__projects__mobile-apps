package com.papum.homecookscompanion.view.inventory

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityAlerts
import com.papum.homecookscompanion.model.database.EntityInventory
import com.papum.homecookscompanion.model.database.EntityProduct
import com.papum.homecookscompanion.utils.Const
import com.papum.homecookscompanion.utils.errors.BadQuantityException
import com.papum.homecookscompanion.view.dialogs.FragmentDialogAddToInventory
import com.papum.homecookscompanion.view.dialogs.FragmentDialogAddToList
import com.papum.homecookscompanion.view.dialogs.FragmentDialogAddToMeals
import com.papum.homecookscompanion.view.edit.recipe.EditRecipeViewModel
import com.papum.homecookscompanion.view.products.ProductResultViewModel
import com.papum.homecookscompanion.view.products.ProductResultViewModelFactory
import com.papum.homecookscompanion.view.products.ProductsViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentInventory.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentInventory :
	Fragment(R.layout.page_fragment_inventory),
	InventoryAdapter.IListenerInventoryItem,
	FragmentDialogAddToInventory.IListenerDialog,
	FragmentDialogAddToList.IListenerDialog,
	FragmentDialogAddToMeals.IListenerDialog
{

	private lateinit var navController: NavController
	private val viewModel: InventoryViewModel by viewModels {
		InventoryViewModelFactory(
			Repository(requireActivity().application)
		)
	}
	private val viewModel_selectProduct: ProductResultViewModel by viewModels {
		ProductResultViewModelFactory(requireActivity())
	}

	val adapter = InventoryAdapter(mutableListOf(), this)

	var addingRecipe = false


	override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.page_fragment_inventory, container, false)
    }

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		navController = findNavController()

		/* recycler */
		val recycler = view.findViewById<RecyclerView>(R.id.inventory_recycler_view)
		recycler.adapter = adapter
		recycler.layoutManager = LinearLayoutManager(context)

		/* UI listeners */

		view.findViewById<ImageButton>(R.id.inventory_btn_scan).setOnClickListener {
			navController.navigate(
				FragmentInventoryDirections.actionFragmentInventoryToFragmentScanReceipt()
			)
		}

		view.findViewById<ImageButton>(R.id.inventory_editRecipe_btn).setOnClickListener {
			navController.navigate(
				FragmentInventoryDirections.actionFragmentInventoryToProductsWithResult(
					ProductsViewModel.ARG_FILTER_RECIPES)
			)
		}

		/* Observers */

		viewModel.getAllProductsInInventoryWithAlerts().observe(viewLifecycleOwner) { products ->
			adapter.updateItems(products.toMutableList())
		}

		// on successful add recipe
		viewModel.addRecipeResult.observe(viewLifecycleOwner) { result ->
			if(result != null) {
				Log.d(TAG, "Add recipe to inventory: $result")
				if (result == InventoryViewModel.Companion.AddRecipeResult.SUCCESS)
					showSuccessAddRecipeToInventory()
				else
					showErrorLowQuantityRecipe()
				viewModel.resetAddRecipeResult()
			}
		}

		// selected recipe
		viewModel_selectProduct.selectedProduct.observe(viewLifecycleOwner) { selectedProduct ->
			if(selectedProduct != null) {
				addingRecipe = true
				showDialogAddToInventory(selectedProduct)
				addingRecipe = false
				viewModel_selectProduct.reset()
			}
		}

	}


	/* Display */

	private fun showDialogAddToInventory(product: EntityProduct) {
		FragmentDialogAddToInventory.newInstance(this, product)
			.show(parentFragmentManager, "ADD_MEALS")
	}
	private fun showErrorAddToMeals(productId: Long, quantity: Float) {
		Log.e(TAG, "Error adding quantity ${Const.getQuantityString(quantity)} of product $productId to meals")
		Toast.makeText(context,
			"Error: insufficient quantity; add more in inventory or add from Products instead",
			Toast.LENGTH_LONG
		)
			.show()
	}
	private fun showErrorLowQuantityRecipe() {
		Log.e(TAG, "Error adding recipe to inventory, insufficient ingredients")
		Toast.makeText(context,
			"Error: insufficient ingredients; add more in inventory or add from Products instead",
			Toast.LENGTH_LONG
		)
			.show()
	}
	private fun showSuccessAddToAlerts(productId: Long, quantity: Float) {
		Log.d(TAG, "Added quantity ${Const.getQuantityString(quantity)} of product $productId to alerts")
		Toast.makeText(context, "Added alert for ${Const.getQuantityString(quantity)}", Toast.LENGTH_SHORT)
			.show()
	}
	private fun showSuccessAddRecipeToInventory() {
		Log.d(TAG, "Added recipe to inventory")
		Toast.makeText(context,
			"Recipe added from ingredients!",
			Toast.LENGTH_SHORT
		)
			.show()
	}
	private fun showSuccessAddToList(productId: Long, quantity: Float) {
		Log.d(TAG, "Added quantity ${Const.getQuantityString(quantity)} of product $productId to list")
		Toast.makeText(context, "Added ${Const.getQuantityString(quantity)} to list", Toast.LENGTH_SHORT)
			.show()
	}
	private fun showSuccessAddToMeals(productId: Long, quantity: Float) {
		Log.d(TAG, "Added quantity ${Const.getQuantityString(quantity)} of product $productId to meals")
		Toast.makeText(context, "Added ${Const.getQuantityString(quantity)} to meals", Toast.LENGTH_SHORT)
			.show()
	}


	/* InventoryAdapter.IListenerInventoryItem */

	override fun onClickAddToList(product: EntityProduct) {
		FragmentDialogAddToList.newInstance(this, product)
			.show(parentFragmentManager, "ADD_INVENTORY")

	}

	override fun onClickAddToMeals(product: EntityProduct) {
		FragmentDialogAddToMeals.newInstance(this, product)
			.show(parentFragmentManager, "ADD_MEALS")
	}

	override fun onClickInfo(product: EntityProduct) {
		navController.navigate(
			FragmentInventoryDirections.actionFragmentInventoryToEditFood(product.id)
		)
	}

	override fun onSetQuantity(id: Long, inventoryItem: EntityInventory?, quantity: Float) {
		val _inventoryItem = inventoryItem ?: viewModel.addToInventory(id, quantity)
		val newInventoryItem = viewModel.updateInventory(_inventoryItem, quantity)
		adapter.updateItemInInventory(newInventoryItem)
	}

	override fun onSetAlert(id: Long, alert: EntityAlerts?, quantity: Float) {
		val _alert = alert ?: viewModel.addAlert(id, quantity)
		val newAlert = viewModel.updateAlert(_alert, quantity)
		adapter.updateItemAlert(newAlert)
		showSuccessAddToAlerts(id, quantity)
	}

	/* FragmentDialogAddTo*.IListenerDialog */

	override fun onClickAddToInventory(dialog: DialogFragment, productId: Long, quantity: Float) {
		viewLifecycleOwner.lifecycleScope.launch {
			viewModel.cookRecipe(productId, quantity)
		}
	}

	override fun onClickAddToList(dialog: DialogFragment, productId: Long, quantity: Float) {
		showSuccessAddToList(productId, quantity)
		viewModel.addToList(productId, quantity)
	}

	override fun onClickAddToMeals(dialog: DialogFragment, productId: Long, date: LocalDateTime, quantity: Float) {
		Log.d(TAG, "Adding to meals: ${Const.getQuantityString(quantity)} of id $productId")
		adapter.items?.find { item -> item.product.id == productId }?.let { item ->
			item.inventoryItem?.let { inventoryItem ->

				try {
					viewModel.addToMealsFromInventory(inventoryItem, date, quantity)
					showSuccessAddToMeals(productId, quantity)
				} catch (e: Exception) {
					Log.e(TAG, "Error in adding: ${e.message}")
					showErrorAddToMeals(productId, quantity)
				}
			} ?: showErrorAddToMeals(productId, quantity)
		}
	}

	override fun onClickRemove(inventoryItem: EntityInventory) {
		Log.d(TAG,  "Removing from inventory: id ${inventoryItem.idProduct}")
		viewModel.removeFromInventory(inventoryItem)
		adapter.deleteItem(inventoryItem.idProduct)
	}

	override fun onClickAddToInventoryCancel(dialog: DialogFragment) {

	}
	override fun onClickAddToListCancel(dialog: DialogFragment) {

	}
	override fun onClickAddToMealsCancel(dialog: DialogFragment) {

	}




	companion object {

		private const val TAG = "Inventory"

        @JvmStatic
        fun newInstance() =
            FragmentInventory()
    }
}