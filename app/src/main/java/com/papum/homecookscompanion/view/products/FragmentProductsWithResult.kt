package com.papum.homecookscompanion.view.products

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityProduct
import com.papum.homecookscompanion.view.edit.food.FragmentEditFoodArgs


class FragmentProductsWithResult :
	Fragment(R.layout.page_fragment_products),
	ProductsWithResultAdapter.IListenerOnClickProduct
{

	private val args: FragmentProductsWithResultArgs by navArgs()
	private val viewModel_products: ProductsViewModel by viewModels {
		ProductsViewModelFactory(
			Repository(requireActivity().application)
		)
	}
	private val viewModel_result: ProductResultViewModel by viewModels {
		ProductResultViewModelFactory(requireActivity())
	}
	private lateinit var navController: NavController


	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_products_with_result, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		navController = findNavController()

		val filter: Int = args.filter
		viewModel_products.filter.value = filter

		/* Recycler */
		val adapter = ProductsWithResultAdapter(listOf(), this)

		val recycler = view.findViewById<RecyclerView>(R.id.productsWithResult_recycler_view)
		recycler.adapter = adapter
		recycler.layoutManager = LinearLayoutManager(context)

		// first fetch all
		viewModel_products.getProducts().observe(viewLifecycleOwner) { products ->
			adapter.updateItems(products)
		}

		/* UI Listeners */

		// update on search
		view.findViewById<EditText>(R.id.productsWithResult_editText_search)
			.doOnTextChanged { text, start, before, count ->
				viewModel_products.getProducts().observe(viewLifecycleOwner) { products ->
					adapter.updateItems(products)
				}
			}

	}


	/* IListenerOnClickProduct */

	override fun onClickInfo(product: EntityProduct) {
		if(product.isRecipe)
			Toast.makeText(
				requireContext(), "You can't open a recipe from here", Toast.LENGTH_LONG
			).show()
		else
			navController.navigate(
				FragmentProductsWithResultDirections.actionFragmentProductsWithResultToEditFood(product.id)
			)
	}

	override fun onClickSelect(product: EntityProduct) {
		Log.d(TAG, "selected $product")
		viewModel_result.selectProduct(product)
		navController.navigateUp()
	}



	companion object {

		private const val TAG = "SELECT"


	}


}