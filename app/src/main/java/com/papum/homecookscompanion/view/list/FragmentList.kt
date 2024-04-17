package com.papum.homecookscompanion.view.list

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityProduct


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentList.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentList : Fragment(R.layout.page_fragment_list) {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.page_fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

		val view_model: ListViewModel by viewModels {
			ListViewModelFactory(
				Repository(requireActivity().application)
			)
		}

		/*
        val listItems = listOf(
                "bread",
                "milk",
                "eggs"
            )
            .map { ListItem(it) }.toMutableList()
		*/

		val adapter = ListAdapter(listOf())

        val recycler = view.findViewById<RecyclerView>(R.id.list_recycler_view)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(context)

		view_model.getAllProducts().observe(viewLifecycleOwner) { newdata ->
			adapter.let {
				it.items = newdata
				it.notifyDataSetChanged()
				Log.d("LIST_ACTIVITY_UPDATE", "${it.items}; ${it.itemCount}")
			}
		}

		view_model.insertProduct(EntityProduct(0, "a", "b"))
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentList()
    }
}