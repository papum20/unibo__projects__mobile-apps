package com.papum.homecookscompanion.view.edit.scan

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.papum.homecookscompanion.R

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentScanReceipt.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentScanReceipt : Fragment(R.layout.fragment_scan_receipt) {


	override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

		if (OpenCVLoader.initLocal()) {
			Log.i("OpenCV", "OpenCV successfully loaded.")
		}

		// An example: Resize an image using OpenCV
		val bitmap =
			BitmapFactory.decodeFile(Companion.IMAGE_FILE_PATH) // load the image
		if (bitmap != null) {
			val resizedBitmap =
				getResizedBitmapCV(bitmap, 400, 600) // image resized using OpenCV

			// Other code goes here ...
		}

		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_scan_receipt, container, false)
	}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		view.findViewById<Button>(R.id.scan_btn_scan).setOnClickListener {

		}

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