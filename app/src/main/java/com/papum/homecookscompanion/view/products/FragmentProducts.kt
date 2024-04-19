package com.papum.homecookscompanion.view.products

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityProduct


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentProducts.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentProducts :
	Fragment(R.layout.page_fragment_products),
	ProductsAdapter.IListenerOnClickProduct,
	FragmentDialogAddToList.IListenerDialog
{


	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.page_fragment_products, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		// View Model
		val viewModel: ProductsViewModel by viewModels {
			ProductsViewModelFactory(
				Repository(requireActivity().application)
			)
		}

		/* Recycler */
		val adapter = ProductsAdapter(listOf(), this)

		val recycler = view.findViewById<RecyclerView>(R.id.products_recycler_view)
		recycler.adapter = adapter
		recycler.layoutManager = LinearLayoutManager(context)

		// first fetch all
		viewModel.getAllProducts().observe(viewLifecycleOwner) { products ->
			adapter.updateItems(products)
		}

		/* UI Listeners */

		// update on search
		val searchEditText: EditText = view.findViewById(R.id.products_editText_search)
		searchEditText.doOnTextChanged { text, start, before, count ->
			viewModel.getAllProducts_fromSubstr_caseInsensitive(text.toString()).observe(viewLifecycleOwner) { products ->
				Log.i("PRODUCTS_SEARCH", "found ${products.size} matches for \"$text\"")
				adapter.updateItems(products)
			}
		}

	}


	/* IListenerOnClickProduct */

	override fun onClickAddToInventoryClick(product: EntityProduct) {
		FragmentDialogAddToList.newInstance(this, product)
			.show(parentFragmentManager, "ADD_LIST")
	}

	override fun onClickAddToListClick(product: EntityProduct) {
		FragmentDialogAddToList.newInstance(this, product)
			.show(parentFragmentManager, "ADD_INVENTORY")

	}

	override fun onClickAddToPlan(product: EntityProduct) {
		FragmentDialogAddToList.newInstance(this, product)
			.show(parentFragmentManager, "ADD_PLAN")
	}

	/* FragmentDialogAddToList.IListenerDialog */

	override fun onClickAdd(dialog: DialogFragment, quantity: Float) {
		Log.d("PRODUCTS_ADD_LIST", quantity.toString())
	}

	override fun onClickCancel(dialog: DialogFragment) {

	}

}