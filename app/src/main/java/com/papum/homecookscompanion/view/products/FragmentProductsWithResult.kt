package com.papum.homecookscompanion.view.products

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityProduct


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentProductsWithResult.newInstance] factory method to
 * create an instance of this fragment.
 *
 * Returns the result with its viewModel, instantiate it like
 * viewModel_selectProduct = ViewModelProvider(requireActivity())[ProductResultViewModel::class.java]
 */
class FragmentProductsWithResult :
	Fragment(R.layout.page_fragment_products),
	ProductsWithResultAdapter.IListenerOnClickProduct
{

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

		/* Recycler */
		val adapter = ProductsWithResultAdapter(listOf(), this)

		val recycler = view.findViewById<RecyclerView>(R.id.productsWithResult_recycler_view)
		recycler.adapter = adapter
		recycler.layoutManager = LinearLayoutManager(context)

		// first fetch all
		viewModel_products.getAllProducts().observe(viewLifecycleOwner) { products ->
			adapter.updateItems(products)
		}

		/* UI Listeners */

		// update on search
		view.findViewById<EditText>(R.id.productsWithResult_editText_search)
			.doOnTextChanged { text, start, before, count ->
				viewModel_products.getAllProducts_fromSubstr_caseInsensitive(text.toString()).observe(viewLifecycleOwner) { products ->
					adapter.updateItems(products)
				}
			}

	}


	/* IListenerOnClickProduct */

	override fun onClickInfo(product: EntityProduct) {
		navController.navigate(
			FragmentProductsDirections.actionFragmentProductsToEditFood(product.id)
		)
	}

	override fun onClickSelect(product: EntityProduct) {
		viewModel_result.selectProduct(product)
		navController.navigateUp()
	}


}