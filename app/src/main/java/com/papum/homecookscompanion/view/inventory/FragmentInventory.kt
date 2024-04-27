package com.papum.homecookscompanion.view.inventory

import android.R.attr.bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextRecognizer
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.view.products.FragmentProductsDirections


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentInventory.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentInventory : Fragment(R.layout.page_fragment_inventory) {

	private lateinit var navController: NavController


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

		val viewModel: InventoryViewModel by viewModels {
			InventoryViewModelFactory(
				Repository(requireActivity().application)
			)
		}

		/* recycler */
		val adapter = InventoryAdapter(listOf())

		val recycler = view.findViewById<RecyclerView>(R.id.inventory_recycler_view)
		recycler.adapter = adapter
		recycler.layoutManager = LinearLayoutManager(context)

		viewModel.getAllProductsInInventoryWithAlerts().observe(viewLifecycleOwner) { products ->
			adapter.let {
				it.items = products
				it.notifyDataSetChanged()
				Log.d("INVENTORY_ALL", "products: ${it.itemCount}")
				Log.d("INVENTORY_ALL", "products ids: ${products.map{ p -> "${p.product.id}.${p.product.name}-${p.inventoryItem?.quantity}-${p.alert?.quantity};" }}")
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

    companion object {
        @JvmStatic
        fun newInstance() =
            FragmentInventory()
    }
}