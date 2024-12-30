package com.kotlin.socialstore.ui.elements

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.kotlin.socialstore.R

@Composable
fun QrCodePopup(content: String, showDialog: MutableState<Boolean>) {
    val isLoading = remember { mutableStateOf(true) }
    val qrCodeBitmap = remember { mutableStateOf<Bitmap?>(null) }

    // Generate the QR code when the popup is initialized
    LaunchedEffect(Unit) {
        qrCodeBitmap.value = generateQrCode(content, isLoading)
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            text = {
                if (isLoading.value) {
                    CircularProgressIndicator()
                } else {
                    qrCodeBitmap.value?.let { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                        )
                    } ?: Text(text = stringResource(R.string.error_generate_qrcode))
                }
            },
            confirmButton = {
                ButtonElement(
                    "Close",
                    { showDialog.value = false },
                    Modifier.fillMaxWidth()
                )
            }
        )
    }
}

// Function to generate QR code
private fun generateQrCode(content: String, isLoading: MutableState<Boolean>): Bitmap? {
    val writer = QRCodeWriter()
    return try {
        isLoading.value = true
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 500, 500)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val fillColor = if (bitMatrix[x, y]) Color.Black else Color.White
                bitmap.setPixel(x, y, fillColor.toArgb())
            }
        }

        bitmap
    } catch (e: WriterException) {
        e.printStackTrace()
        null
    } finally {
        isLoading.value = false
    }
}