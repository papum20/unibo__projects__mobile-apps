package com.papum.homecookscompanion.view.shops

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
 * Use the [FragmentShops.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentShops :
	Fragment(R.layout.fragment_shops)
{

	private lateinit var navController: NavController
	private val viewModel: ShopsViewModel by viewModels {
		ShopsViewModelFactory(
			Repository(requireActivity().application)
		)
	}

	val adapter = ShopsAdapter(mutableListOf())


	override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shops, container, false)
    }

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		navController = findNavController()

		/* recycler */
		val recycler = view.findViewById<RecyclerView>(R.id.shops_recycler_view)
		recycler.adapter = adapter
		recycler.layoutManager = LinearLayoutManager(context)

		viewModel.getAllShops().observe(viewLifecycleOwner) { shops ->
			adapter.updateItems(shops.toMutableList())
		}

		/* UI listeners */

		view.findViewById<Button>(R.id.shops_recycler_btn_add).setOnClickListener {
			// TODO:
			/*
			navController.navigate(
				FragmentInventoryDirections.actionFragmentInventoryToFragmentScanReceipt()
			)
			 */

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

		private const val TAG = "INVENTORY"

        @JvmStatic
        fun newInstance() =
            FragmentShops()
    }
}