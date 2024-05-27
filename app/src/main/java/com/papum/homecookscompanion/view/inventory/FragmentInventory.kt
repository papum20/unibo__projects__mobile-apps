package com.papum.homecookscompanion.view.inventory

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityAlerts
import com.papum.homecookscompanion.model.database.EntityInventory
import com.papum.homecookscompanion.model.database.EntityProduct
import com.papum.homecookscompanion.view.products.FragmentDialogAddToList
import com.papum.homecookscompanion.view.products.FragmentDialogAddToMeals
import java.time.LocalDateTime


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentInventory.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentInventory :
	Fragment(R.layout.page_fragment_inventory),
	InventoryAdapter.IListenerInventoryItem,
	FragmentDialogAddToList.IListenerDialog,
	FragmentDialogAddToMeals.IListenerDialog
{

	private lateinit var navController: NavController
	private val viewModel: InventoryViewModel by viewModels {
		InventoryViewModelFactory(
			Repository(requireActivity().application)
		)
	}

	val adapter = InventoryAdapter(mutableListOf(), this)


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

		viewModel.getAllProductsInInventoryWithAlerts().observe(viewLifecycleOwner) { products ->
			adapter.updateItems(products.toMutableList())
		}

		/* UI listeners */

		view.findViewById<Button>(R.id.inventory_recycler_btn_scan).setOnClickListener {
			navController.navigate(
				FragmentInventoryDirections.actionFragmentInventoryToFragmentScanReceipt()
			)

			/*
			context?.let { context ->
				val textRecognizer = TextRecognizer.Builder(context).build()

				val imageFrame = Frame.Builder()
					.setBitmap(bitmap) // your image bitmap
					.build()

				var imageText = ""


				val textBlocks = textRecognizer.detect(imageFrame)

				for (i in 0 until textBlocks.size()) {
					val textBlock = textBlocks[textBlocks.keyAt(i)]
					imageText = textBlock.value // return string
				}
			}
			 */
		}

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
	}

	/* FragmentDialogAddTo*.IListenerDialog */

	override fun onClickAddToList(dialog: DialogFragment, productId: Long, quantity: Float) {
		Log.d(TAG,  "Adding to list: id $productId to ${quantity}")
		viewModel.addToList(productId, quantity)
	}

	override fun onClickAddToMeals(dialog: DialogFragment, productId: Long, date: LocalDateTime, quantity: Float) {
		Log.d(TAG,  "Adding to meals: id $productId to ${quantity}")
		adapter.items?.find { item -> item.product.id == productId }?.let { item ->
			item.inventoryItem?.let { inventoryItem ->
				val _inventoryItem = inventoryItem.apply {
						this.quantity?.let { quantityCurrent ->
							this.quantity = quantityCurrent - quantity
						}
					}
				viewModel.addToMealsFromInventory(_inventoryItem, date, quantity)
				adapter.updateItemInInventory(_inventoryItem)

			} ?: Log.e(TAG, "Tried to add to a meal a productt in inventory.")
		}
	}

	override fun onClickAddToListCancel(dialog: DialogFragment) {

	}

	override fun onClickAddToMealsCancel(dialog: DialogFragment) {

	}



	companion object {

		private const val TAG = "INVENTORY"

        @JvmStatic
        fun newInstance() =
            FragmentInventory()
    }
}