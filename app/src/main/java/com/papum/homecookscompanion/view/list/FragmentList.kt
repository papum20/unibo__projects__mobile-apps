package com.papum.homecookscompanion.view.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityList
import com.papum.homecookscompanion.model.database.EntityProduct


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentList.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentList :
	Fragment(R.layout.page_fragment_list),
	ListAdapter.IListenerListItem
{

	private lateinit var navController: NavController
	private val viewModel: ListViewModel by viewModels {
		ListViewModelFactory(
			Repository(requireActivity().application)
		)
	}

	val adapter = ListAdapter(mutableListOf(), this)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.page_fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

		navController = findNavController()

        val recycler = view.findViewById<RecyclerView>(R.id.list_recycler_view)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(context)

		viewModel.getAllProductsInList().observe(viewLifecycleOwner) { products ->
			adapter.updateItems(products.toMutableList())
		}

    }


	/* ListAdapter.IListenerListItem */

	override fun onClickInfo(product: EntityProduct) {
		navController.navigate(
			FragmentListDirections.actionFragmentListToEditFood(product.id)
		)
	}

	override fun onClickRemove(listItem: EntityList) {
		viewModel.removeProductFromList(listItem)
		adapter.deleteItem(listItem.idProduct)
	}

	override fun onSetQuantity(id: Long, listItem: EntityList?, quantity: Float) {
		val _listItem = listItem ?: viewModel.addToList(id, quantity)
		val newListItem = viewModel.updateListItem(_listItem, quantity)
		adapter.updateItemInList(newListItem)
	}


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         */
        @JvmStatic
        fun newInstance() =
            FragmentList()
    }
}