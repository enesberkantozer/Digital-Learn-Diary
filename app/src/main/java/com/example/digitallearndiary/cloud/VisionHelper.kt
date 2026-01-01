package com.example.digitallearndiary.cloud

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.example.digitallearndiary.basicData.UygulamaModeli
import java.io.File
import java.util.Date

@Composable
public fun rememberVisionScanner(viewModel: UygulamaModeli): VisionScannerActions {
    val context = LocalContext.current
    var geciciUri by remember { mutableStateOf<Uri?>(null) }

    // Galeri Launcher
    val galeriLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
        it?.let { viewModel.notuTara(context, it) }
    }

    // Kamera Launcher
    val kameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { basarili ->
        if (basarili) geciciUri?.let { viewModel.notuTara(context, it) }
    }

    return remember {
        VisionScannerActions(
            openGallery = { galeriLauncher.launch(arrayOf("image/*")) },
            openCamera = {
                val file = File(context.externalCacheDir, "ocr_${Date().time}.jpg")
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                geciciUri = uri
                kameraLauncher.launch(uri)
            }
        )
    }
}

public data class VisionScannerActions(val openGallery: () -> Unit, val openCamera: () -> Unit)