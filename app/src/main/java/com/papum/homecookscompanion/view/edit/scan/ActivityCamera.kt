package com.papum.homecookscompanion.view.edit.scan

import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.papum.homecookscompanion.R
import org.opencv.android.CameraActivity
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2
import org.opencv.android.OpenCVLoader
import org.opencv.core.Mat


class ActivityCamera : CameraActivity(), CvCameraViewListener2 {

	private var mOpenCvCameraView: CameraBridgeViewBase? = null


	init {
		Log.i(TAG, "Instantiated new " + this.javaClass)
	}


	public override fun onCreate(savedInstanceState: Bundle?) {
		Log.i(TAG, "called onCreate")
		super.onCreate(savedInstanceState)
		if (OpenCVLoader.initLocal()) {
			Log.i(TAG, "OpenCV loaded successfully")
		} else {
			Log.e(TAG, "OpenCV initialization failed!")
			Toast.makeText(this, "OpenCV initialization failed!", Toast.LENGTH_LONG)
				.show()
			return
		}
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
		setContentView(R.layout.fragment_scan_camera)
		mOpenCvCameraView =
			findViewById<View>(R.id.tutorial_java_surface_view) as CameraBridgeViewBase
		mOpenCvCameraView!!.visibility = SurfaceView.VISIBLE
		mOpenCvCameraView!!.setCvCameraViewListener(this)
	}

	public override fun onPause() {
		super.onPause()
		if (mOpenCvCameraView != null) mOpenCvCameraView!!.disableView()
	}

	public override fun onResume() {
		super.onResume()
		if (mOpenCvCameraView != null) mOpenCvCameraView!!.enableView()
	}

	override fun getCameraViewList(): List<CameraBridgeViewBase> {
		return listOf<CameraBridgeViewBase>(mOpenCvCameraView!!)
	}

	public override fun onDestroy() {
		super.onDestroy()
		if (mOpenCvCameraView != null) mOpenCvCameraView!!.disableView()
	}

	override fun onCameraViewStarted(width: Int, height: Int) {}
	override fun onCameraViewStopped() {}
	override fun onCameraFrame(inputFrame: CvCameraViewFrame): Mat {
		return inputFrame.rgba()
	}

	companion object {
		private const val TAG = "OCVSample::Activity"
	}
}