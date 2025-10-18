package com.miam.app.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.miam.app.R
import java.util.concurrent.Executors

class ScanFragment : Fragment() {
    private var processing = false
    private val exec = Executors.newSingleThreadExecutor()

    private val reqPerm = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) startCamera(requireView()) else requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_scan, container, false)
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            startCamera(v) else reqPerm.launch(Manifest.permission.CAMERA)
        return v
    }

    private fun startCamera(v: View) {
        val previewView = v.findViewById<PreviewView>(R.id.previewView)
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
            val analysis = ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
            val opts = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_EAN_13, Barcode.FORMAT_EAN_8, Barcode.FORMAT_UPC_A, Barcode.FORMAT_UPC_E, Barcode.FORMAT_QR_CODE)
                .build()
            val scanner = BarcodeScanning.getClient(opts)

            analysis.setAnalyzer(exec) { imgProxy ->
                if (processing) { imgProxy.close(); return@setAnalyzer }
                processing = true
                val media = imgProxy.image
                if (media != null) {
                    val image = InputImage.fromMediaImage(media, imgProxy.imageInfo.rotationDegrees)
                    scanner.process(image)
                        .addOnSuccessListener { barcodes ->
                            val code = barcodes.firstOrNull()?.rawValue
                            if (!code.isNullOrBlank()) {
                                parentFragmentManager.commit {
                                    val f = AddProductFragment()
                                    f.arguments = Bundle().apply { putString("barcode", code) }
                                    replace(R.id.fragmentContainer, f)
                                    addToBackStack(null)
                                }
                            }
                        }
                        .addOnCompleteListener { processing = false; imgProxy.close() }
                } else { processing = false; imgProxy.close() }
            }

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)
        }, ContextCompat.getMainExecutor(requireContext()))
    }
}