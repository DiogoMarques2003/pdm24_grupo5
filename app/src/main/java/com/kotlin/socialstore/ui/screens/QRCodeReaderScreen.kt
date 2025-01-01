package com.kotlin.socialstore.ui.screens

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

// TODO: Add the square to read qrcode
@ExperimentalGetImage
@Composable
fun QRCodeReaderScreen(
    modifier: Modifier = Modifier,
    nextScreen: String,
    navController: NavController
) {
    val context = LocalContext.current

    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            (context as Activity),
            arrayOf(Manifest.permission.CAMERA),
            101
        )
    }

    Box(modifier) {
        val lifecycleOwner = LocalLifecycleOwner.current
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                val previewView = PreviewView(context)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = androidx.camera.core.Preview.Builder().build()
                    val cameraSelector = androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA

                    preview.setSurfaceProvider(previewView.surfaceProvider)

                    val barcodeScanner = BarcodeScanning.getClient()

                    val imageAnalyzer = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    imageAnalyzer.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                        processImageProxy(imageProxy, barcodeScanner, nextScreen, navController)
                    }

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalyzer
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, ContextCompat.getMainExecutor(context))
                previewView
            }
        )
    }
}

@ExperimentalGetImage
private fun processImageProxy(
    imageProxy: ImageProxy,
    barcodeScanner: BarcodeScanner,
    nextScreen: String,
    navController: NavController
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        barcodeScanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    if (barcode.format == Barcode.FORMAT_QR_CODE) {
                        barcode.rawValue?.let { qrCodeContent ->
                            navController.navigate("$nextScreen/$qrCodeContent")
                            imageProxy.close()
                            return@addOnSuccessListener
                        }
                    }
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}

/* Read QrCode:
val qrCodeResult = navController.currentBackStackEntry
    ?.savedStateHandle
    ?.getLiveData<String>("qrCodeResult")
    ?.observeAsState()

    ButtonElement(
        text = "Scan QR Code",
        onClick = { navController.navigate("qrcode_reader_screen") },
        modifier = Modifier.fillMaxWidth()
    )

    qrCodeResult?.value?.let { result ->
        Text(text = "QR Code Content: $result")
    }
 */