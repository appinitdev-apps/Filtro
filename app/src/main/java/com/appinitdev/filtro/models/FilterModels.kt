package com.appinitdev.filtro

import androidx.compose.ui.graphics.vector.ImageVector
import com.appinitdev.gpuimage.filter.GPUImageFilter

data class FilterParameter(
    val label: String,
    val initialPercentage: Float = 50f,
    var currentValue: Float = initialPercentage
)

data class EditorFilter(
    val name: String,
    val category: String,
    val filterInstance: GPUImageFilter,
    val parameters: List<FilterParameter> = emptyList()
)

data class FilterCategory(
    val name: String,
    val icon: ImageVector
)

data class TextureItem(
    val name: String,
    val resId: Int? = null,      // Para las texturas locales predefinidas
    val uriString: String? = null // Para las imágenes de la galería (guardado como String)
)
