package com.papum.homecookscompanion.view.products

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.Repository


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentProducts.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentProducts : Fragment(R.layout.page_fragment_products) {


	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.page_fragment_products, container, false)
	}

	@SuppressLint("NotifyDataSetChanged")	// all fetched products change
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		// View Model
		val viewModel: ProductsViewModel by viewModels {
			ProductsViewModelFactory(
				Repository(requireActivity().application)
			)
		}

		/* Recycler */
		val adapter = ProductsAdapter(listOf())

		val recycler = view.findViewById<RecyclerView>(R.id.products_recycler_view)
		recycler.adapter = adapter
		recycler.layoutManager = LinearLayoutManager(context)

		// first fetch all
		viewModel.getAllProducts().observe(viewLifecycleOwner) { products ->
			adapter.items = products
			adapter.notifyDataSetChanged()
			//Log.d("PRODUCTS_ACTIVITY_UPDATE", "${it.items}; ${it.itemCount}")
			Log.d("PRODUCTS_ACTIVITY_UPDATE", "products: ${adapter.itemCount}")
		}

		/* UI Listeners */

		// update on search
		val searchEditText: EditText = view.findViewById(R.id.products_editText_search)
		searchEditText.doOnTextChanged { text, start, before, count ->
			viewModel.getAllProducts_fromSubstr_caseInsensitive(text.toString()).observe(viewLifecycleOwner) { products ->
				adapter.items = products
				Log.i("PRODUCTS_SEARCH", "found ${products.size} matches for \"$text\"")
				adapter.notifyDataSetChanged()
				//Log.d("PRODUCTS_ACTIVITY_UPDATE", "${it.items}; ${it.itemCount}")
				Log.d("PRODUCTS_ACTIVITY_UPDATE", "products: ${adapter.itemCount}")
			}
		}

	}

	companion object {
		/**
		 * Use this factory method to create a new instance of
		 * this fragment using the provided parameters.
		 */
		@JvmStatic
		fun newInstance(param1: String, param2: String) =
			FragmentProducts()
	}
}