package com.papum.homecookscompanion.view.edit.scan

import android.graphics.Rect
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
import com.google.mlkit.vision.text.Text.Line
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.Repository
import java.io.IOException
import kotlin.math.max
import kotlin.math.min


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

					val textsWithHeights = mutableListOf<Pair<Rect, String>>()
					val recognizedLines = mutableListOf<Line>()

					for (block in visionText.textBlocks) {
						val blockText = block.text
						Log.i(LOG_TAG_SCAN, "block with ${block.lines.size} lines; midY: ${block.boundingBox?.centerY()}; points: ${block.cornerPoints?.joinToString() }}; text: $blockText")

						for (line in block.lines) {
							val lineText = line.text
							Log.i(LOG_TAG_SCAN, "line with midY: ${line.boundingBox?.centerY()}; points: ${line.cornerPoints?.joinToString() }; text: '$lineText'")
							//textsWithHeights.add(Pair(line.boundingBox!!, "${line.cornerPoints!!.joinToString()} - $lineText"))
							textsWithHeights.add(Pair(line.boundingBox!!, lineText))
							recognizedLines.add(line)

							if(REGEX_ONLY_PRICE.matches(lineText))
								prices.add(
									// if , was used, replace with . for convenience
									lineText.replace(",",".")
								)
							else if(!REGEX_ONLY_TAXES.matches(lineText) and REGEX_ITEM.matches(lineText))
								products.add(lineText)
						}
					}

					val textsWithHeights_sorted = textsWithHeights.sortedBy { it.first.centerY() }
					//Log.i(LOG_TAG_SCAN, textsWithHeights_sorted.joinToString("\n"))

					val lines_ = mutableListOf<List<Pair<Rect, String>>>()
					var i_ = 0
					while(i_ < textsWithHeights_sorted.size) {
						var j = i_ + 1	// sublist(i, j) excluding j is always on same line
						// && instead of 'and' is important: it's short-circuit/lazy
						while( (j + 1 <= textsWithHeights_sorted.size) &&
							areBlocksAtSameY_(textsWithHeights_sorted.subList(i_, j+1))
						) {
							j++
						}

						lines_.add( textsWithHeights_sorted.subList(i_, j) )
						i_ = j
					}

					Log.i(LOG_TAG_SCAN, lines_.joinToString("\n") { it.joinToString("\t") { "${it.first}, ${it.second}, ${it.first.centerY()}" } })



					val recognizedLines_sorted = recognizedLines.sortedBy { it.boundingBox?.centerY() }
					Log.i(LOG_TAG_SCAN, recognizedLines_sorted.joinToString{"${it.text}\n"})

					val lines = mutableListOf<List<Line>>()
					var i = 0
					while(i < recognizedLines_sorted.size) {
						var j = i + 1	// sublist(i, j) excluding j is always on same line
						// && instead of 'and' is important: it's short-circuit/lazy
						while( (j + 1 <= recognizedLines_sorted.size) &&
							areBlocksAtSameY(recognizedLines_sorted.subList(i, j+1))
						) {
							j++
						}

						lines.add( recognizedLines_sorted.subList(i, j) )
						i = j
					}

					Log.i(LOG_TAG_SCAN, lines.joinToString("\n") {
							it.joinToString("\t") {
								"ITEM: ${it.boundingBox}, ${it.boundingBox!!.centerY()}, ${it.text}"
							}
						}
					)




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

	/**
	 * Given, for a block, its corners and edges, we use the average slope of its horizontal edges
	 * and its centerY to determine a straight line; so, we check that, for each pair of blocks
	 * in the input list, the line of the first passes through both the vertical edges of the second.
	 */
	private fun areBlocksAtSameY(blocksWithTexts: List<Line>): Boolean {

		// check elements not null (note that || is lazy, "or" not)
		for(i in blocksWithTexts.indices) {
			if( (blocksWithTexts[i].cornerPoints == null) ||
				(blocksWithTexts[i].boundingBox == null) ||
				(blocksWithTexts[i].cornerPoints!!.size < 4)
			)
				return false
		}

		// straight lines
		val slopes = mutableListOf<Double>()
		for(i in blocksWithTexts.indices) {
			with(blocksWithTexts[i].cornerPoints!!) {
				val slope_top = (get(1).y - get(0).y).toDouble() / (get(1).x - get(0).x)
				val slope_bottom = (get(3).y - get(2).y).toDouble() / (get(3).x - get(2).x)
				slopes.add( (slope_top + slope_bottom) / 2 )
			}
		}

		// for each pair, check both ways
		for(i in 0..(blocksWithTexts.size - 2) ) {
			val j = i+1
			//for(j in i+1..<blocksWithTexts.size) {

				val first: Line
				val second: Line
				val first_slope: Double
				val second_slope: Double
				if(blocksWithTexts[i].boundingBox!!.centerX() <= blocksWithTexts[j].boundingBox!!.centerX()) {
					first = blocksWithTexts[i]
					second = blocksWithTexts[j]
					first_slope = slopes[i]
					second_slope = slopes[j]
				} else {
					first = blocksWithTexts[j]
					second = blocksWithTexts[i]
					first_slope = slopes[j]
					second_slope = slopes[i]
				}

				val first_x_start	= (first.cornerPoints!![0].x	+ first.cornerPoints!![3].x) / 2
				val first_x_end		= (first.cornerPoints!![1].x	+ first.cornerPoints!![2].x) / 2
				val second_x_start	= (second.cornerPoints!![0].x	+ second.cornerPoints!![3].x) / 2
				val second_x_end	= (second.cornerPoints!![1].x	+ second.cornerPoints!![2].x) / 2

				val firstLine_at_secondStart_y = (
						first_slope		* (second_x_start - first.boundingBox!!.centerX())	+ first.boundingBox!!.centerY()
					).toInt()
				val firstLine_at_secondEnd_y = (
						first_slope		* (second_x_end - first.boundingBox!!.centerX())	+ first.boundingBox!!.centerY()
					).toInt()
				val secondLine_at_firstStart_y = (
						second_slope	* (first_x_start - second.boundingBox!!.centerX())	+ second.boundingBox!!.centerY()
					).toInt()
				val secondLine_at_firstEnd_y = (
						second_slope	* (first_x_end - second.boundingBox!!.centerX())	+ second.boundingBox!!.centerY()
					).toInt()

				Log.d("SCANNITEM", "texts: ${first.text}, ${second.text};\n" +
					"slopes: $first_slope, $second_slope;\n" +
					"first_at_second_y: $firstLine_at_secondStart_y, $firstLine_at_secondEnd_y;\n" +
					"second_at_first_y: $secondLine_at_firstStart_y, $secondLine_at_firstEnd_y;\n" +
					"first_corners: ${first.cornerPoints!!.joinToString()}\n" +
					"second_corners: ${second.cornerPoints!!.joinToString()}\n" +
					"first_center: ${first.boundingBox!!.centerX()}, ${first.boundingBox!!.centerY()}\n" +
					"second_center: ${second.boundingBox!!.centerX()}, ${second.boundingBox!!.centerY()}"
				)

				if( (firstLine_at_secondStart_y	!in first.cornerPoints!![0]!!.y .. first.cornerPoints!![3]!!.y)		or
					//(firstLine_at_secondEnd_y	!in first.cornerPoints!![1]!!.y .. first.cornerPoints!![2]!!.y)		or
					//(secondLine_at_firstStart_y	!in second.cornerPoints!![0]!!.y .. second.cornerPoints!![3]!!.y) 	or
					(secondLine_at_firstEnd_y	!in second.cornerPoints!![1]!!.y .. second.cornerPoints!![2]!!.y)
				)
					return false

			//}
		}
		return true
	}

	private fun areBlocksAtSameY_(blocksWithTexts: List<Pair<Rect, String>>): Boolean {

		for(i in blocksWithTexts.indices) {
			for(j in i+1..<blocksWithTexts.size) {
				val yi_min = blocksWithTexts[j].first.top
				val yi_max = blocksWithTexts[j].first.bottom
				val yj_min = blocksWithTexts[j].first.top
				val yj_max = blocksWithTexts[j].first.bottom
				if( !blocksWithTexts[i].first.centerY().let { it in yi_min..yi_max } or
					!blocksWithTexts[j].first.centerY().let { it in yj_min..yj_max }
				)
					return false
			}
		}
		return true
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