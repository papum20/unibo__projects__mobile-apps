package com.papum.homecookscompanion.view.edit.scan

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.Repository
import java.io.IOException
import kotlin.math.max


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentScanReceipt.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentScanReceipt
	: Fragment(R.layout.fragment_scan_receipt),
	ScanAdapter.`IListenerOnClickReceiptEntry`
{

	private val receiptItems = mutableListOf<ScanModel>()

	// View Model
	private val viewModel: ScanViewModel by viewModels {
		ScanViewModelFactory(
			Repository(requireActivity().application)
		)
	}
	private val adapter = ScanAdapter(receiptItems, this)

	private val launcher: ActivityResultLauncher<String> =
		registerForActivityResult(
			ActivityResultContracts.GetContent(),
			ActivityResultCallback<Uri?>() { uri: Uri? ->
				Log.i("FILE", "$uri")
				processImage(uri)
			}
		)


	override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_scan_receipt, container, false)
	}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		/* Recycler */
		val recycler = view.findViewById<RecyclerView>(R.id.scan_recycler_view)
		recycler.adapter = adapter
		recycler.layoutManager = LinearLayoutManager(context)

		/* UI listeners */
		view.findViewById<Button>(R.id.scan_btn_scan).setOnClickListener { _ ->
			pickFileAndScan()
		}

		view.findViewById<Button>(R.id.scan_btn_confirm).setOnClickListener { _ ->
			// TODO: 
		}

    }


	/**
	 * Parse the recognized text, coupling the i-th found product with the i-th found price.
	 */
	private fun processImage(uri: Uri?) {

		if(uri == null) {
			Toast.makeText(context, "ERROR: Image wasn't picked", Toast.LENGTH_LONG)
				.show()
			return
		}

		val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

		try {
			val image = InputImage.fromFilePath(requireContext(), uri)

			recognizer.process(image)
				.addOnSuccessListener { visionText ->

					Log.i(LOG_TAG_SCAN, visionText.text)

					val products	= mutableListOf<String>()
					val prices		= mutableListOf<String>()

					for (block in visionText.textBlocks) {
						val blockText = block.text
						Log.i(LOG_TAG_SCAN, "block with ${block.lines.size} lines : $blockText")

						for (line in block.lines) {
							val lineText = line.text
							Log.i(LOG_TAG_SCAN, "line : '$lineText'")

							if(REGEX_ONLY_PRICE.matches(lineText))
								prices.add(
									// if , was used, replace with . for convenience
									lineText.replace(",",".")
								)
							else if(!REGEX_ONLY_TAXES.matches(lineText) and REGEX_ITEM.matches(lineText))
								products.add(lineText)
						}
					}

					receiptItems.clear()
					for(i in 0..max(products.size, prices.size))
						receiptItems.add(ScanModel(
							products.getOrNull(i) ?: "not found",
							prices.getOrNull(i) ?: "not found",
							null,
							prices.getOrNull(i) ?: "not found"
						))

					adapter.updateItems(receiptItems)
				}
				.addOnFailureListener { e ->
					// Task failed with an exception
				}


		} catch (e: IOException) {
			e.printStackTrace()
		}
	}

	private fun pickFileAndScan() {
		launcher.launch("image/*")
		//var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
		//chooseFile.setType("*/*")
		//chooseFile = Intent.createChooser(chooseFile, "Choose a file")
		//startActivityForResult(chooseFile, PICKFILE_RESULT_CODE)


	}


	/* ScanAdapter.IListenerOnClickSelectProduct */

	override fun onClickSelectProduct() {
		TODO("Not yet implemented")
	}

	override fun onClickRemove(position: Int) {
		adapter.deleteItem(position)
	}


    companion object {

		private const val LOG_TAG_SCAN = "SCAN"

		private val REGEX_ONLY_PRICE = Regex("^-?\\d+[.,]\\d{2}$")
		private val REGEX_ONLY_TAXES = Regex("^\\d+%")
		private val REGEX_ITEM = Regex("[^\\n]+")

		/**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         */
        @JvmStatic
        fun newInstance() =
            FragmentScanReceipt()
    }
}