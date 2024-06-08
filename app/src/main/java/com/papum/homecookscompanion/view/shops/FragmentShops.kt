package com.papum.homecookscompanion.view.shops

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.Repository


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

		/* permissions */
		requestPermissionsIfNecessary(PERMISSIONS_MAPS)

		navController = findNavController()

		/* recycler */
		val recycler = view.findViewById<RecyclerView>(R.id.shops_recycler_view)
		recycler.adapter = adapter
		recycler.layoutManager = LinearLayoutManager(context)

		viewModel.getAllShops().observe(viewLifecycleOwner) { shops ->
			adapter.updateItems(shops.toMutableList())
		}

		/* UI listeners */

		view.findViewById<ImageButton>(R.id.shops_recycler_btn_add).setOnClickListener {
			navController.navigate(
				FragmentShopsDirections.actionFragmentShopsToFragmentMapSelectPoint()
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


	/* Permissions */

	private val requestMultiplePermissionsLauncher = registerForActivityResult(
		ActivityResultContracts.RequestMultiplePermissions()
	) { permissions ->
		permissions.entries.forEach {
			Log.d(TAG, "Permission requested result: ${it.key}: ${it.value}")
		}
	}

	private fun requestPermissionsIfNecessary(permissions: Array<String>) {
		val permissionsToRequest = ArrayList<String>()
		permissions.forEach { permission ->
			if ( ContextCompat.checkSelfPermission(requireContext(), permission)
				!= PackageManager.PERMISSION_GRANTED) {
				permissionsToRequest.add(permission)
			}
		}
		if (permissionsToRequest.size > 0) {
			requestMultiplePermissionsLauncher.launch(permissionsToRequest.toTypedArray())
		}
	}




	companion object {

		private const val TAG = "INVENTORY"

		private val PERMISSIONS_MAPS = arrayOf(
			Manifest.permission.ACCESS_COARSE_LOCATION,
			Manifest.permission.ACCESS_FINE_LOCATION
		)

        @JvmStatic
        fun newInstance() =
            FragmentShops()
    }

}