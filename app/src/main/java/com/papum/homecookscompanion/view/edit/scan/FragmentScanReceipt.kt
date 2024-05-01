package com.papum.homecookscompanion.view.edit.scan

import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
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
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.papum.homecookscompanion.R
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import java.io.IOException


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentScanReceipt.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentScanReceipt : Fragment(R.layout.fragment_scan_receipt) {

	val mLauncher: ActivityResultLauncher<String> =
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

		if (OpenCVLoader.initLocal()) {
			Log.i("OpenCV", "OpenCV successfully loaded.")
		}

		/*
		// An example: Resize an image using OpenCV
		val bitmap =
			BitmapFactory.decodeFile(Companion.IMAGE_FILE_PATH) // load the image
		if (bitmap != null) {
			val resizedBitmap =
				getResizedBitmapCV(bitmap, 400, 600) // image resized using OpenCV

			// Other code goes here ...
		}
		*/

		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_scan_receipt, container, false)
	}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		view.findViewById<Button>(R.id.scan_btn_scan).setOnClickListener {
			getFile()
			/*
			val intent: Intent = Intent()
			intent.setComponent(
				ComponentName(
				requireContext(),
				ActivityCamera::class.java
			)
			)
			startActivity(intent)
			*/
		}

    }

	private fun processImage(uri: Uri?) {

		if(uri == null) {
			Toast.makeText(context, "ERROR: Image wasn't picked", Toast.LENGTH_LONG)
				.show()
			return
		}

		val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
		val image: InputImage

		try {
			image = InputImage.fromFilePath(requireContext(), uri)

			val result = recognizer.process(image)
				.addOnSuccessListener { visionText ->

					val resultText = visionText.text
					Log.i("SCAN", resultText)
					for (block in visionText.textBlocks) {
						val blockText = block.text
						val blockCornerPoints = block.cornerPoints
						val blockFrame = block.boundingBox
						Log.i("SCAN", blockText)
						Log.i("SCAN", blockCornerPoints.toString())
						Log.i("SCAN", blockFrame.toString())
						for (line in block.lines) {
							val lineText = line.text
							val lineCornerPoints = line.cornerPoints
							val lineFrame = line.boundingBox
							Log.i("SCANLINE", lineText)
							Log.i("SCANLINE", lineCornerPoints.toString())
							Log.i("SCANLINE", lineFrame.toString())
							for (element in line.elements) {
								val elementText = element.text
								val elementCornerPoints = element.cornerPoints
								val elementFrame = element.boundingBox
								Log.i("SCANEL", elementText)
								Log.i("SCANEL", elementCornerPoints.toString())
								Log.i("SCANEL", elementFrame.toString())
							}
						}
					}
				}
				.addOnFailureListener { e ->
					// Task failed with an exception
					// ...
				}


		} catch (e: IOException) {
			e.printStackTrace()
		}
	}

	private fun getFile() {
		mLauncher.launch("image/*")
		//var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
		//chooseFile.setType("*/*")
		//chooseFile = Intent.createChooser(chooseFile, "Choose a file")
		//startActivityForResult(chooseFile, PICKFILE_RESULT_CODE)


	}

	private fun getResizedBitmapCV(
		inputBitmap: Bitmap,
		newWidth: Int,
		newHeight: Int
	): Bitmap {
		// Convert the input Bitmap to a Mat
		val inputMat = Mat()
		Utils.bitmapToMat(inputBitmap, inputMat)

		// Create a new Mat for the resized image
		val resizedMat = Mat()
		Imgproc.resize(inputMat, resizedMat, Size(newWidth.toDouble(), newHeight.toDouble()))

		// Convert the resized Mat back to a Bitmap
		val resizedBitmap =
			Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
		Utils.matToBitmap(resizedMat, resizedBitmap)
		inputMat.release()
		resizedMat.release()
		return resizedBitmap
	}

    companion object {

		// Replace with the actual path to your image file
		private const val IMAGE_FILE_PATH = "/path/to/your/image.jpg"

		/**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         */
        @JvmStatic
        fun newInstance() =
            FragmentScanReceipt()
    }
}