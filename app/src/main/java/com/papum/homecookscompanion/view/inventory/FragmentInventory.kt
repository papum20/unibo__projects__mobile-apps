package com.papum.homecookscompanion.view.inventory

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.view.products.ProductsAdapter
import com.papum.homecookscompanion.view.products.ProductsViewModel
import com.papum.homecookscompanion.view.products.ProductsViewModelFactory


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentInventory.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentInventory : Fragment(R.layout.page_fragment_inventory) {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.page_fragment_inventory, container, false)
    }

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val viewModel: InventoryViewModel by viewModels {
			InventoryViewModelFactory(
				Repository(requireActivity().application)
			)
		}

		val adapter = InventoryAdapter(listOf())

		val recycler = view.findViewById<RecyclerView>(R.id.inventory_recycler_view)
		recycler.adapter = adapter
		recycler.layoutManager = LinearLayoutManager(context)



		viewModel.getAllProducts().observe(viewLifecycleOwner) { newdata ->
			adapter.let {
				it.items = newdata
				it.notifyDataSetChanged()
				//Log.d("PRODUCTS_ACTIVITY_UPDATE", "${it.items}; ${it.itemCount}")
				Log.d("INVENTORY_ACTIVITY_UPDATE", "products: ${it.itemCount}")
			}
		}

	}

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentInventory()
    }
}