package com.papum.homecookscompanion.view.edit.recipe

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityProduct
import com.papum.homecookscompanion.model.database.EntityRecipeWithIngredientsAndNutrients
import com.papum.homecookscompanion.utils.Const
import com.papum.homecookscompanion.utils.files.FileFormatterRecipe
import com.papum.homecookscompanion.view.products.ProductResultViewModel
import com.papum.homecookscompanion.view.products.ProductResultViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


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

	/* view */
	private var tvName:				TextView? = null
	private var tvParent:			TextView? = null
	private var tvKcal:				TextView? = null
	private var tvCarbohydrates:	TextView? = null
	private var tvFats:				TextView? = null
	private var tvProteins:			TextView? = null
	private var tvWeightRecipe:		TextView? = null
	private var tvWeightDisplay:	TextView? = null



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
		tvName			= view.findViewById(R.id.fragment_edit_recipe_name)
		tvParent		= view.findViewById(R.id.fragment_edit_recipe_parent)
		tvKcal			= view.findViewById(R.id.nutrients_kcal)
		tvCarbohydrates	= view.findViewById(R.id.nutrients_carbohydrates)
		tvFats			= view.findViewById(R.id.nutrients_fats)
		tvProteins		= view.findViewById(R.id.nutrients_proteins)
		tvWeightRecipe	= view.findViewById(R.id.fragment_edit_recipe_weight)
		tvWeightDisplay	= view.findViewById(R.id.nutrients_quantity)


		/* if it's editing a food (and not creating), setup */
		if(recipeId != Const.ID_PRODUCT_NULL) {
			viewModel.getRecipe_fromId(recipeId).observe(viewLifecycleOwner) { recipe ->
				if(recipe != null) {
					Log.d(TAG, "recipe to edit fetched, it's $recipe")
					tvName?.text = recipe.name
					tvParent?.text = recipe.parent
					viewModel.initRecipe(recipe)
				}
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

		view.findViewById<Button>(R.id.fragment_edit_recipe_btn_import).setOnClickListener {
			importFile()
		}

		view.findViewById<Button>(R.id.fragment_edit_recipe_btn_share).setOnClickListener {
			viewLifecycleOwner.lifecycleScope.launch {
				shareFile()
			}
		}

		view.findViewById<Button>(R.id.fragment_edit_recipe_btn_save).setOnClickListener {
			val name			: String	= tvName?.text.toString()
			val parent			: String	= tvParent?.text.toString()

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
			if ( viewModel.initIngredients(ingredients.toMutableList()) ) {
				Log.d(TAG, "fetched ingredients, new size: ${ingredients.size}")
			}
		}

		// on ingredients fetched
		viewModel.ingredients.observe(viewLifecycleOwner) { ingredients ->
			adapter.updateItems(ingredients)
			Log.d(TAG, "Updated ingredients, new size: ${ingredients.size}")
		}

		// on nutrients updated
		viewModel.getRecipeNutrients().observe(viewLifecycleOwner) { nutrients ->
			tvKcal?.text			= nutrients.kcal?.toString()			?: Const.DFLT_NUTRIENT_VALUE_DISPLAYED
			tvCarbohydrates?.text	= nutrients.carbohydrates?.toString()	?: Const.DFLT_NUTRIENT_VALUE_DISPLAYED
			tvFats?.text			= nutrients.fats?.toString()			?: Const.DFLT_NUTRIENT_VALUE_DISPLAYED
			tvProteins?.text		= nutrients.proteins?.toString()		?: Const.DFLT_NUTRIENT_VALUE_DISPLAYED
		}

		// total weight
		viewModel.getRecipeWeight().observe(viewLifecycleOwner) { weight ->
			tvWeightRecipe?.text = weight.toString()
		}

		// weight for displayed nutrients
		viewModel.getDisplayedWeight().observe(viewLifecycleOwner) { weight ->
			tvWeightDisplay?.text = weight.toString()
		}

		// on successful import
		viewModel.importResult.observe(viewLifecycleOwner) { result ->
			if(result != null) {
				Log.d(TAG, "Import recipe successful: $result")
				if (result == EditRecipeViewModel.Companion.ImportResult.SUCCESS)
					showSuccessReadRecipe()
				else if(result == EditRecipeViewModel.Companion.ImportResult.DUPLICATE)
					showSuccessReadRecipeDuplicate()

				adapter.updateItems(viewModel.getIngredients())

				viewModel.getRecipe()?.let { importedRecipe ->
					tvName?.text = importedRecipe.name
					tvParent?.text = importedRecipe.parent
				}
			}
		}

		// on product selected
		viewModel_selectProduct.selectedProduct.observe(viewLifecycleOwner) { selectedProduct ->
			selectedProduct?.let {
				viewModel.addIngredient(it)
				viewModel_selectProduct.reset()
			}
		}

	}



	private fun _importFile(uri: Uri?) {

		Log.i(TAG, "Selected file: $uri")

		if(uri == null) {
			showErrorMissingFile()
			return
		}

		val importedRecipe = FileFormatterRecipe.readRecipe( requireContext().contentResolver, uri )
		if(importedRecipe == null) {
			showErrorReadRecipe()
			return
		}

		viewLifecycleOwner.lifecycleScope.launch {
			viewModel.importRecipe(importedRecipe)

			viewModel.getRecipe()?.let { newRecipe ->
				Log.d(TAG, "new vm recipe is ${newRecipe}")
				viewModel.saveRecipe(newRecipe.name, newRecipe.parent)
				navController.navigateUp()
			} ?: run {
				showErrorMissingRecipe()
			}
		}
	}

	/**
	 * Launch file picker, read the recipe from the file, update the current values
	 * and update the view elements.
	 */
	private fun importFile() {
		launcherImportFile.launch(FileFormatterRecipe.MIME_TYPE)
	}

	private suspend fun shareFile() {

		if(viewModel.getRecipe() == null) {
			showErrorMissingRecipe()
			return
		}

		val recipe = viewModel.getRecipe()!!

		val filesPath = File(requireContext().cacheDir, getString(R.string.path_cache))
		filesPath.mkdirs()

		val tempFile = File(filesPath, FileFormatterRecipe.getFileName(recipe))

		withContext(Dispatchers.IO) {

			tempFile.createNewFile()
			val uri = FileProvider.getUriForFile(
				requireContext(),
				getString(R.string.file_provider_authority),
				tempFile
			)
			Log.d(TAG, "sharing file with uri $uri")

			FileFormatterRecipe.writeRecipe(
				tempFile, EntityRecipeWithIngredientsAndNutrients(
					recipe, viewModel.getIngredients(), viewModel.fetchNutrients()
				)
			)

			// Set up an Intent to send back to apps that request a file
			val resultIntent: Intent = Intent().apply {
				action = Intent.ACTION_SEND
				putExtra(Intent.EXTRA_STREAM, uri)
				type = FileFormatterRecipe.MIME_TYPE
			}
			// Grant temporary read permission to the content URI
			resultIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

			val shareIntent =
				Intent.createChooser(resultIntent, getString(R.string.intent_share_recipe))
			startActivity(shareIntent)
		}

	}


	private val launcherImportFile: ActivityResultLauncher<String> =
		registerForActivityResult(
			ActivityResultContracts.GetContent()
		) { uri: Uri? ->
			_importFile(uri)
		}


	/* display */

	private fun showErrorMissingFile() {
		Log.e(TAG, "file wasn't picked")
		Toast.makeText(context, "ERROR: File wasn't picked", Toast.LENGTH_LONG)
			.show()
	}

	fun showErrorMissingIngredients() {
		Toast.makeText(activity, "ERROR: Missing ingredients", Toast.LENGTH_LONG).show()
	}

	private fun showErrorMissingRecipe() {
		Log.e(TAG, "viewModel.recipe is null")
		Toast.makeText(activity, "ERROR: Missing recipe, please create and save one first", Toast.LENGTH_LONG)
			.show()
	}

	private fun showErrorReadRecipe() {
		Log.e(TAG, "error in reading file")
		Toast.makeText(activity, "ERROR: Couldn't read recipe from file", Toast.LENGTH_LONG)
			.show()
	}

	private fun showSuccessReadRecipeDuplicate() {
		Log.i(TAG, "Recipe parsed successfully, but had to change name to avoid conflict")
		Toast.makeText(activity,
			"Recipe added with a new name, as it's already used",
			Toast.LENGTH_LONG)
			.show()
	}
	private fun showSuccessReadRecipe() {
		Log.i(TAG, "Recipe parsed successfully")
		Toast.makeText(activity, "Recipe read successfully!", Toast.LENGTH_LONG)
			.show()
	}

	/* ListAdapter.IListenerListItem */

	override fun onClickInfo(product: EntityProduct) {
		navController.navigate(
			FragmentEditRecipeDirections.actionFragmentEditRecipeToEditFood(product.id)
		)
	}

	override fun onClickRemove(id: Long, position: Int) {
		viewModel.removeIngredient(id)
	}

	override fun onSetQuantity(id: Long, quantity: Float, position: Int) {
		viewModel.updateIngredientQuantity(id, quantity)
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