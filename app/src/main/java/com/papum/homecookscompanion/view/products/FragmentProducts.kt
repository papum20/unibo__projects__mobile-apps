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
import java.util.Date


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentProducts.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentProducts :
	Fragment(R.layout.page_fragment_products),
	ProductsAdapter.IListenerOnClickProduct,
	FragmentDialogAddToList.IListenerDialog,
	FragmentDialogAddToInventory.IListenerDialog,
	FragmentDialogAddToPlan.IListenerDialog
{

	// View Model
	private val viewModel: ProductsViewModel by viewModels {
		ProductsViewModelFactory(
			Repository(requireActivity().application)
		)
	}


	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.page_fragment_products, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		/* Recycler */
		val adapter = ProductsAdapter(listOf(), this)

		val recycler = view.findViewById<RecyclerView>(R.id.products_recycler_view)
		recycler.adapter = adapter
		recycler.layoutManager = LinearLayoutManager(context)

		// first fetch all
		viewModel.getAllProducts().observe(viewLifecycleOwner) { products ->
			Log.d("PRODUCTS", "products ids: ${products.map{ p -> "${p.id}.${p.name};" }}")
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

	override fun onClickAddToInventory(product: EntityProduct) {
		FragmentDialogAddToInventory.newInstance(this, product)
			.show(parentFragmentManager, "ADD_LIST")
	}

	override fun onClickAddToList(product: EntityProduct) {
		FragmentDialogAddToList.newInstance(this, product)
			.show(parentFragmentManager, "ADD_INVENTORY")

	}

	override fun onClickAddToPlan(product: EntityProduct) {
		FragmentDialogAddToPlan.newInstance(this, product)
			.show(parentFragmentManager, "ADD_PLAN")
	}

	/* FragmentDialogAddToList.IListenerDialog */

	override fun onClickAddToInventory(dialog: DialogFragment, productId: Long, quantity: Float) {
		Log.d("PRODUCTS_ADD_INVENTORY",  "id $productId to ${quantity}")
		viewModel.addToInventory(productId, quantity)
	}

	override fun onClickAddToList(dialog: DialogFragment, productId: Long, quantity: Float) {
		Log.d("PRODUCTS_ADD_LIST",  "id $productId to ${quantity}")
		viewModel.addToList(productId, quantity)
	}

	override fun onClickAddToPlan(dialog: DialogFragment, productId: Long, date: Date, quantity: Float) {
		Log.d("PRODUCTS_ADD_PLAN",  "id $productId to ${quantity}")
		viewModel.addToPlan(productId, date, quantity)
	}

	override fun onClickAddToInventoryCancel(dialog: DialogFragment) {

	}

	override fun onClickAddToListCancel(dialog: DialogFragment) {

	}

	override fun onClickAddToPlanCancel(dialog: DialogFragment) {

	}

}