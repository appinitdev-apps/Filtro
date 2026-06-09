package com.appinitdev.filtro

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest // IMPORTACIÓN REQUERIDA
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.appinitdev.filtro.ui.screen.PhotoEditorScreen
import com.appinitdev.filtro.ui.theme.FiltroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // --- NUEVO: Recuperar la URI al iniciar ---
        val lastSavedUri = PreferencesManager.getLastUri(this)?.let { Uri.parse(it) }

        setContent {
            FiltroTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    // 1. Inicializamos con la URI recuperada, si existe
                    var selectedImageUri by remember { mutableStateOf<Uri?>(lastSavedUri) }

                    val pickMediaLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.PickVisualMedia()
                    ) { uri ->
                        if (uri != null) {
                            // --- GUARDAMOS LA URI cuando el usuario elige una nueva ---
                            PreferencesManager.saveLastUri(this@MainActivity, uri.toString())
                            selectedImageUri = uri
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageUri == null) {
                            Button(onClick = {
                                pickMediaLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }) {
                                Text(text = "Seleccionar Imagen de la Galería")
                            }
                        } else {
                            // Si existe 'lastSavedUri' o el usuario seleccionó una, entra aquí directamente
                            PhotoEditorScreen(
                                imageUri = selectedImageUri!!,
                                onBackClicked = {
                                    selectedImageUri=lastSavedUri
                                },
                                onSavedSuccessfully = { newUri ->
                                    // Actualizamos el estado con la nueva Uri recibida para forzar la recarga
                                    selectedImageUri = newUri
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}