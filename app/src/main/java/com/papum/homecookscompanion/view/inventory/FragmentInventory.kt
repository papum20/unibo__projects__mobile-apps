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
import com.papum.homecookscompanion.model.database.EntityInventory
import com.papum.homecookscompanion.model.database.EntityProduct
import com.papum.homecookscompanion.model.database.EntityProductAndInventoryWithAlerts
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

	val adapter = InventoryAdapter(listOf(), this)


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
			adapter.let {
				Log.d("INVENTORY_ALL", "products: ${it.itemCount}")
				Log.d("INVENTORY_ALL", "products ids: ${products.map{ p -> "${p.product.id}.${p.product.name}-${p.inventoryItem?.quantity}-${p.alert?.quantity};" }}")
				it.updateItems(products)
			}
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
			FragmentInventoryDirections.actionFragmentInventoryToFragmentEditFoodFromInventory(product.id.toString())
		)
	}

	override fun onSetQuantity(inventoryItem: EntityInventory, quantity: Float) {
		viewModel.updateInventory(inventoryItem, quantity)
	}

	/* FragmentDialogAddTo*.IListenerDialog */

	override fun onClickAddToList(dialog: DialogFragment, productId: Long, quantity: Float) {
		Log.d("INVENTORY_ADD_LIST",  "id $productId to ${quantity}")
		viewModel.addToList(productId, quantity)
	}

	override fun onClickAddToMeals(dialog: DialogFragment, productId: Long, date: LocalDateTime, quantity: Float) {
		Log.d("INVENTORY_ADD_MEALS",  "id $productId to ${quantity}")
		adapter.items?.find { item -> item.product.id == productId }?.let { item ->
			viewModel.addToMealsFromInventory(
				item.inventoryItem.apply {
					this.quantity?.let { quantityCurrent ->
						this.quantity = quantityCurrent - quantity
					}
				}, date, quantity
			)
		}
	}

	override fun onClickAddToListCancel(dialog: DialogFragment) {

	}

	override fun onClickAddToMealsCancel(dialog: DialogFragment) {

	}



	companion object {
        @JvmStatic
        fun newInstance() =
            FragmentInventory()
    }
}