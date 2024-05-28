package com.papum.homecookscompanion.view.edit.recipe

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityProduct
import com.papum.homecookscompanion.utils.Const
import com.papum.homecookscompanion.view.products.ProductResultViewModel
import com.papum.homecookscompanion.view.products.ProductResultViewModelFactory
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter


class FragmentEditRecipe :
	Fragment(R.layout.fragment_edit_recipe),
	EditRecipeAdapter.IListenerEditRecipeItem
{

	private val args: FragmentEditRecipeArgs by navArgs()
	private lateinit var viewModel: EditRecipeViewModel
	private val viewModel_selectProduct: ProductResultViewModel by viewModels {
		ProductResultViewModelFactory(requireActivity())
	}

	val adapter = EditRecipeAdapter(mutableListOf(), this)
	private lateinit var navController: NavController

	
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_recipe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		/* args */
		val recipeId: Long = args.recipeId


		navController = findNavController()
		val recycler = view.findViewById<RecyclerView>(R.id.edit_recipe_recycler_view)
		recycler.adapter = adapter
		recycler.layoutManager = LinearLayoutManager(context)

		viewModel = ViewModelProvider(this,
				EditRecipeViewModelFactory(Repository(requireActivity().application), recipeId)
			)[EditRecipeViewModel::class.java]

		/* view */
		val tvName			= view.findViewById<TextView>( R.id.fragment_edit_recipe_name)
		val tvParent		= view.findViewById<TextView>( R.id.fragment_edit_recipe_parent)
		val tvKcal			= view.findViewById<TextView>( R.id.nutrients_kcal)
		val tvCarbohydrates	= view.findViewById<TextView>( R.id.nutrients_carbohydrates)
		val tvFats			= view.findViewById<TextView>( R.id.nutrients_fats)
		val tvProteins		= view.findViewById<TextView>( R.id.nutrients_proteins)
		val tvWeightRecipe	= view.findViewById<TextView>( R.id.fragment_edit_recipe_weight)
		val tvWeightDisplay	= view.findViewById<TextView>( R.id.nutrients_quantity)


		/* if it's editing a food (and not creating), setup */
		if(recipeId != Const.ID_PRODUCT_NULL) {
			viewModel.getRecipe_fromId(recipeId).observe(viewLifecycleOwner) { recipe ->
				Log.d(TAG, "recipe to edit fetched, it's $recipe")
				tvName.text				= recipe.name
				tvParent.text			= recipe.parent
				viewModel.initRecipe(recipe)
			}
		}

		/* UI listeners */

		view.findViewById<Button>(R.id.fragment_edit_recipe_btn_add).setOnClickListener {
			navController.navigate(
				FragmentEditRecipeDirections.actionFragmentEditRecipeToProductsWithResult()
			)
		}

		view.findViewById<Button>(R.id.fragment_edit_recipe_btn_cancel).setOnClickListener {
			Log.i(TAG, "not added")
			navController.navigateUp()
		}

		view.findViewById<Button>(R.id.fragment_edit_recipe_btn_share).setOnClickListener {
			Log.i(TAG, "sharing")
	/*		val sendIntent: Intent = Intent().apply {
				action = Intent.ACTION_SEND
				putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
				type = "text/plain"
			}

			val shareIntent = Intent.createChooser(sendIntent, "Title")
			startActivity(shareIntent)
	*/
		/*	val jsonData = JSONObject()
			jsonData.put("name", "test")
			// Add data to jsonData...

			val sendIntent: Intent = Intent().apply {
				action = Intent.ACTION_SEND
				putExtra(Intent.EXTRA_TEXT, jsonData.toString())
				type = "application/json"
			}

			val shareIntent = Intent.createChooser(sendIntent, null)
			startActivity(shareIntent)

		*/

			val cachePath = File(requireContext().cacheDir, "my_temp_files")
			cachePath.mkdirs()

			val tempFile = File(cachePath, "my_temp_file.txt")
			val fileOutputStream = FileOutputStream(tempFile)
			val outputWriter = OutputStreamWriter(fileOutputStream)
			outputWriter.write("This is my text to send.")
			outputWriter.close()

			val fileUri = FileProvider.getUriForFile(
				requireContext(),
				"${requireContext().packageName}.provider",
				tempFile
			)

			// Set up an Intent to send back to apps that request a file
			val resultIntent: Intent = Intent().apply {
				action = Intent.ACTION_SEND
				putExtra(Intent.EXTRA_STREAM, fileUri)
				type = "text/plain"
			}
			// Grant temporary read permission to the content URI
			resultIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

			val shareIntent = Intent.createChooser(resultIntent, null)
			startActivity(shareIntent)
			/*
			val sendIntent: Intent = Intent().apply {
				action = Intent.ACTION_SEND
				putExtra(Intent.EXTRA_STREAM, fileUri)
				type = "text/plain"
			}

			val shareIntent = Intent.createChooser(sendIntent, null)
			startActivity(shareIntent)
*/
			// Delete the temporary file
			tempFile.delete()
		}

		view.findViewById<Button>(R.id.fragment_edit_recipe_btn_save).setOnClickListener {
			val name			: String	= tvName.text.toString()
			val parent			: String	= tvParent.text.toString()

			/* check valid fields */
			if(name == "") {
				Toast.makeText(activity, "ERROR: Missing name", Toast.LENGTH_LONG).show()
			}
			/* edit food */
			else {
				viewModel.saveRecipe(
					name, parent
				)
				Log.i(TAG, "added $name $parent")
				Toast.makeText(activity, "Added: $name !", Toast.LENGTH_SHORT).show()
				navController.navigateUp()
			}
		}

		/* observers */

		// on ingredients fetched
		viewModel.fetchIngredients().observe(viewLifecycleOwner) { ingredients ->
			viewModel.initIngredients(ingredients.toMutableList())
			adapter.updateItems(viewModel.getIngredients())
			Log.d(TAG, "Updated ingredients with fetched ${ingredients.size} -> ${viewModel.getIngredients().size}")
		}

		// on nutrients updated
		viewModel.getRecipeNutrients().observe(viewLifecycleOwner) { nutrients ->
			tvKcal.text				= nutrients.kcal?.toString()			?: Const.DFLT_NUTRIENT_VALUE_DISPLAYED
			tvCarbohydrates.text	= nutrients.carbohydrates?.toString()	?: Const.DFLT_NUTRIENT_VALUE_DISPLAYED
			tvFats.text				= nutrients.fats?.toString()			?: Const.DFLT_NUTRIENT_VALUE_DISPLAYED
			tvProteins.text			= nutrients.proteins?.toString()		?: Const.DFLT_NUTRIENT_VALUE_DISPLAYED
		}

		// total weight
		viewModel.getRecipeWeight().observe(viewLifecycleOwner) { weight ->
			tvWeightRecipe.text = weight.toString()
		}

		// weight for displayed nutrients
		viewModel.getDisplayedWeight().observe(viewLifecycleOwner) { weight ->
			tvWeightDisplay.text = weight.toString()
		}

		// on product selected
		viewModel_selectProduct.selectedProduct.observe(viewLifecycleOwner) { selectedProduct ->
			selectedProduct?.let {
				val newIngredient = viewModel.addIngredient(it)
				viewModel_selectProduct.reset()
				adapter.addItem(newIngredient)
			}
		}

	}


	/* ListAdapter.IListenerListItem */

	override fun onClickInfo(product: EntityProduct) {
		navController.navigate(
			FragmentEditRecipeDirections.actionFragmentEditRecipeToEditFood(product.id)
		)
	}

	override fun onClickRemove(id: Long, position: Int) {
		viewModel.removeIngredient(id)
		adapter.deleteItem(position)
	}

	override fun onSetQuantity(id: Long, quantity: Float, position: Int) {
		viewModel.updateIngredientQuantity(id, quantity)?.let { newItem ->
			adapter.updateIngredient(position)
		}
	}



	companion object {

		private const val TAG = "RECIPE"


        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         */
        @JvmStatic
        fun newInstance() =
            FragmentEditRecipe()
    }
}