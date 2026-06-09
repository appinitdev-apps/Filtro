//package com.appinitdev.filtro.ui.screen
//
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.net.Uri
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.viewinterop.AndroidView
//import com.appinitdev.filtro.TextureItem
//import com.appinitdev.filtro.objectModel.FilterTools
//
//import androidx.activity.result.PickVisualMediaRequest
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.compose.foundation.Image
//import androidx.compose.ui.graphics.asImageBitmap
//import androidx.compose.ui.layout.ContentScale
//import com.appinitdev.filtro.EditorFilter
//import com.appinitdev.gpuimage.GPUImage
//import com.appinitdev.gpuimage.filter.GPUImageFilter
//import com.appinitdev.gpuimage.GPUImageView
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//
//@Composable
//fun PhotoEditorScreen01(imageUri: Uri) {
//    val context = LocalContext.current
//    var selectedTexture by remember { mutableStateOf(FilterTools.textures[0]) }
//
//    val allFiltersList = remember(context, selectedTexture) {
//        val nativeFilters = FilterTools.getAllFilters(context, selectedTexture)
//
//        // Generamos dinámicamente un filtro "Ninguno" para cada categoría existente
//        val noneFilters = FilterTools.categories.map { category ->
//            EditorFilter(
//                name = "Ninguno",
//                category = category.name,
//                filterInstance = GPUImageFilter() // Filtro base que no hace nada
//            )
//        }
//
//        // Colocamos los filtros "Ninguno" al inicio para que aparezcan primero
//        noneFilters + nativeFilters
//    }
//
//    var selectedCategory by remember { mutableStateOf(FilterTools.categories[0].name) }
//    var activeEditorFilter by remember(allFiltersList) { mutableStateOf(allFiltersList[0]) }
//    var gpuImageViewInstance by remember { mutableStateOf<GPUImageView?>(null) }
//
//    val filteredFilters = remember(selectedCategory, allFiltersList) {
//        allFiltersList.filter { it.category == selectedCategory }
//    }
//
//    val galleryLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.PickVisualMedia()
//    ) { uri ->
//        if (uri != null) {
//            try {
//                val flag = android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
//                context.contentResolver.takePersistableUriPermission(uri, flag)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
//            val customTexture = TextureItem(
//                name = "Personalizada",
//                uriString = uri.toString()
//            )
//            selectedTexture = customTexture
//        }
//    }
//
//    var baseThumbnail by remember { mutableStateOf<Bitmap?>(null) }
//    LaunchedEffect(imageUri) {
//        baseThumbnail = withContext(Dispatchers.IO) {
//            try {
//                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
//                context.contentResolver.openInputStream(imageUri).use { BitmapFactory.decodeStream(it, null, options) }
//
//                // Reducimos la resolución drásticamente (ej. máximo ~120px) para que procese instantáneo
//                val targetSize = 120
//                var scale = 1
//                while (options.outWidth / scale / 2 >= targetSize && options.outHeight / scale / 2 >= targetSize) {
//                    scale *= 2
//                }
//
//                val finalOptions = BitmapFactory.Options().apply { inSampleSize = scale }
//                context.contentResolver.openInputStream(imageUri).use { BitmapFactory.decodeStream(it, null, finalOptions) }
//            } catch (e: Exception) {
//                null
//            }
//        }
//    }
//
//    LaunchedEffect(allFiltersList) {
//        val matchingFilter = allFiltersList.find { it.name == activeEditorFilter.name }
//        if (matchingFilter != null) {
//            activeEditorFilter = matchingFilter
//            gpuImageViewInstance?.requestRender()
//        }
//    }
//
//    // CONTENEDOR PRINCIPAL: Permite superponer capas (Z-Index)
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.Black) // Fondo negro base
//    ) {
//        // --- CAPA 1: VISOR GPU (Ocupa el 100% de la pantalla, al fondo) ---
//        AndroidView(
//            modifier = Modifier.fillMaxSize(),
//            factory = { ctx ->
//                GPUImageView(ctx).apply {
//                    setImage(imageUri)
//                    gpuImageViewInstance = this
//                }
//            },
//            update = { view ->
//                view.filter = activeEditorFilter.filterInstance
//                FilterTools.adjustFilter(view.filter, activeEditorFilter.parameters)
//                view.requestRender()
//            }
//        )
//
//        // --- CAPA 2: PANEL DE CONTROL INFERIOR TRASLÚCIDO (Por encima de la imagen) ---
//        Surface(
//            modifier = Modifier
//                .fillMaxWidth()
//                .align(Alignment.BottomCenter), // Lo ancla a la parte inferior del Box
//            color = Color(0xFF1C1C1C).copy(alpha = 0.85f), // Opacidad del 85% para ver la foto detrás
//            shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)
//        ) {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 16.dp, top = 16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//
//                // PANEL MULTI-SLIDER DINÁMICO RECONFIGURABLE
//                if (activeEditorFilter.parameters.isNotEmpty()) {
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(horizontal = 24.dp, vertical = 4.dp),
//                        verticalArrangement = Arrangement.spacedBy(10.dp)
//                    ) {
//                        activeEditorFilter.parameters.forEachIndexed { index, param ->
//                            Column {
//                                Row(
//                                    modifier = Modifier.fillMaxWidth(),
//                                    horizontalArrangement = Arrangement.SpaceBetween
//                                ) {
//                                    Text(
//                                        text = param.label,
//                                        color = Color.White,
//                                        style = MaterialTheme.typography.bodySmall
//                                    )
//                                    Text(
//                                        text = "${param.currentValue.toInt()}%",
//                                        color = MaterialTheme.colorScheme.primary,
//                                        style = MaterialTheme.typography.bodySmall
//                                    )
//                                }
//                                Slider(
//                                    value = param.currentValue,
//                                    valueRange = 0f..100f,
//                                    onValueChange = { newValue ->
//                                        activeEditorFilter = activeEditorFilter.copy(
//                                            parameters = activeEditorFilter.parameters.toMutableList()
//                                                .apply {
//                                                    this[index] = param.copy(currentValue = newValue)
//                                                }
//                                        )
//                                        gpuImageViewInstance?.let { view ->
//                                            FilterTools.adjustFilter(
//                                                view.filter,
//                                                activeEditorFilter.parameters
//                                            )
//                                            view.requestRender()
//                                        }
//                                    }
//                                )
//                            }
//                        }
//                    }
//                }
//
//                // --- SELECTOR DE TEXTURAS DINÁMICO (Categoría Fusión) ---
//                if (selectedCategory == "Fusión") {
//                    Text(
//                        text = "Textura de fusión:",
//                        color = Color.Gray,
//                        style = MaterialTheme.typography.labelMedium,
//                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
//                    )
//
//                    LazyRow(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(vertical = 4.dp),
//                        contentPadding = PaddingValues(horizontal = 16.dp),
//                        horizontalArrangement = Arrangement.spacedBy(10.dp)
//                    ) {
//                        // --- BOTÓN 1: SELECCIONAR DE GALERÍA ---
//                        item {
//                            val isCustomActive = selectedTexture.uriString != null
//                            Box(
//                                modifier = Modifier
//                                    .size(68.dp)
//                                    .clip(RoundedCornerShape(8.dp))
//                                    .background(Color(0xFF252525))
//                                    .border(
//                                        width = if (isCustomActive) 2.dp else 1.dp,
//                                        color = if (isCustomActive) MaterialTheme.colorScheme.primary else Color(0xFF444444),
//                                        shape = RoundedCornerShape(8.dp)
//                                    )
//                                    .clickable {
//                                        galleryLauncher.launch(
//                                            PickVisualMediaRequest(
//                                                ActivityResultContracts.PickVisualMedia.ImageOnly
//                                            )
//                                        )
//                                    },
//                                contentAlignment = Alignment.Center
//                            ) {
//                                if (isCustomActive && selectedTexture.uriString != null) {
//                                    androidx.compose.foundation.Image(
//                                        painter = coil.compose.rememberAsyncImagePainter(model = selectedTexture.uriString),
//                                        contentDescription = "Galería",
//                                        modifier = Modifier.fillMaxSize(),
//                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
//                                    )
//                                } else {
//                                    Icon(
//                                        imageVector = Icons.Default.Add,
//                                        contentDescription = "Cargar de galería",
//                                        tint = Color.White
//                                    )
//                                }
//
//                                Box(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .background(Color.Black.copy(alpha = 0.6f))
//                                        .align(Alignment.BottomCenter)
//                                        .padding(vertical = 2.dp),
//                                    contentAlignment = Alignment.Center
//                                ) {
//                                    Text(
//                                        text = if (isCustomActive) "Galería" else "Subir",
//                                        color = if (isCustomActive) MaterialTheme.colorScheme.primary else Color.LightGray,
//                                        style = MaterialTheme.typography.bodySmall,
//                                        fontSize = 10.sp
//                                    )
//                                }
//                            }
//                        }
//
//                        // --- LAS TEXTURAS NATIVAS RESTANTES ---
//                        items(FilterTools.textures) { item ->
//                            val isTextureSelected = selectedTexture == item && selectedTexture.uriString == null
//
//                            Box(
//                                modifier = Modifier
//                                    .size(68.dp)
//                                    .clip(RoundedCornerShape(8.dp))
//                                    .background(Color(0xFF1E1E1E))
//                                    .border(
//                                        width = if (isTextureSelected) 2.dp else 1.dp,
//                                        color = if (isTextureSelected) MaterialTheme.colorScheme.primary else Color(0xFF333333),
//                                        shape = RoundedCornerShape(8.dp)
//                                    )
//                                    .clickable {
//                                        selectedTexture = item
//                                    },
//                                contentAlignment = Alignment.BottomCenter
//                            ) {
//                                androidx.compose.foundation.Image(
//                                    painter = androidx.compose.ui.res.painterResource(id = item.resId ?: com.appinitdev.filtro.R.drawable.texture_01),
//                                    contentDescription = item.name,
//                                    modifier = Modifier.fillMaxSize(),
//                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
//                                )
//
//                                Box(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .background(Color.Black.copy(alpha = 0.6f))
//                                        .padding(vertical = 2.dp),
//                                    contentAlignment = Alignment.Center
//                                ) {
//                                    Text(
//                                        text = item.name,
//                                        color = if (isTextureSelected) MaterialTheme.colorScheme.primary else Color.White,
//                                        style = MaterialTheme.typography.bodySmall,
//                                        fontSize = 10.sp,
//                                        maxLines = 1,
//                                        overflow = TextOverflow.Ellipsis
//                                    )
//                                }
//                            }
//                        }
//                    }
//
//                    Divider(color = Color(0xFF2B2B2B).copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 6.dp))
//                }
//
//                Divider(color = Color(0xFF2B2B2B).copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 6.dp))
//
//                // --- SELECCIÓN DEL FILTRO ---
//                LazyRow(
//                    contentPadding = PaddingValues(horizontal = 16.dp),
//                    horizontalArrangement = Arrangement.spacedBy(14.dp),
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    items(filteredFilters) { item ->
//                        val isSelected = item.name == activeEditorFilter.name ||
//                                (item.name == "Ninguno" && activeEditorFilter.category != selectedCategory)
//
//                        FilterPreviewItem(
//                            item = item,
//                            baseThumbnail = baseThumbnail,
//                            isSelected = isSelected,
//                            onClick = { activeEditorFilter = item }
//                        )
//                        //                        Column(
////                            horizontalAlignment = Alignment.CenterHorizontally,
////                            modifier = Modifier.width(80.dp).clickable { activeEditorFilter = item }
////                        ) {
////                            Box(
////                                modifier = Modifier
////                                    .size(56.dp)
////                                    .clip(CircleShape)
////                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF2A2A2A).copy(alpha = 0.9f))
////                                    .border(if (isSelected) 2.dp else 0.dp, Color.White, CircleShape),
////                                contentAlignment = Alignment.Center
////                            ) {
////                                Text(
////                                    text = item.name.take(2).uppercase(),
////                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else Color.LightGray,
////                                    style = MaterialTheme.typography.titleSmall
////                                )
////                            }
////                            Spacer(modifier = Modifier.height(6.dp))
////                            Text(text = item.name, color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
////                        }
//                    }
//                }
//
//                Divider(color = Color(0xFF2B2B2B).copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 6.dp))
//
//                // --- PESTAÑAS DE CATEGORÍA ---
//                TabRow(
//                    selectedTabIndex = FilterTools.categories.map { it.name }.indexOf(selectedCategory),
//                    containerColor = Color.Transparent,
//                    contentColor = MaterialTheme.colorScheme.primary
//                ) {
//                    FilterTools.categories.forEach { category ->
//                        val isCatSelected = selectedCategory == category.name
//                        Tab(
//                            selected = isCatSelected,
//                            onClick = { selectedCategory = category.name },
//                            icon = { Icon(category.icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = if (isCatSelected) MaterialTheme.colorScheme.primary else Color.Gray) },
//                            text = { Text(text = category.name, style = MaterialTheme.typography.labelMedium, color = if (isCatSelected) Color.White else Color.Gray) }
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
//
//
//
//@Composable
//fun FilterPreviewItem(
//    item: EditorFilter,
//    baseThumbnail: Bitmap?,
//    isSelected: Boolean,
//    onClick: () -> Unit
//) {
//    val context = LocalContext.current
//
//    // produceState ejecuta el renderizado en un hilo alterno (Dispatchers.Default) de manera limpia
//    val filteredBitmapState = produceState<Bitmap?>(initialValue = null, item, baseThumbnail) {
//        if (baseThumbnail != null) {
//            value = withContext(Dispatchers.Default) {
//                try {
//                    if (item.name == "Ninguno") {
//                        baseThumbnail // Si es ninguno, no gasta CPU y devuelve la original limpia
//                    } else {
//                        // Usamos la versión estática de GPUImage para procesar Bitmaps sin inflar vistas
//                        val gpuImage = GPUImage(context).apply {
//                            setImage(baseThumbnail)
//                            setFilter(item.filterInstance)
//                        }
//                        gpuImage.bitmapWithFilterApplied
//                    }
//                } catch (e: Exception) {
//                    null // Si algo falla, retorna null para pintar el placeholder de texto
//                }
//            }
//        }
//    }
//
//    val filteredBitmap = filteredBitmapState.value
//
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier
//            .width(80.dp)
//            .clickable { onClick() }
//    ) {
//        Box(
//            modifier = Modifier
//                .size(56.dp)
//                .clip(CircleShape)
//                .background(Color(0xFF2A2A2A))
//                .border(if (isSelected) 2.dp else 0.dp, Color.White, CircleShape),
//            contentAlignment = Alignment.Center
//        ) {
//            if (filteredBitmap != null) {
//                // Renderiza la imagen ya filtrada de forma nativa y fluida
//                Image(
//                    bitmap = filteredBitmap.asImageBitmap(),
//                    contentDescription = item.name,
//                    modifier = Modifier.fillMaxSize(),
//                    contentScale = ContentScale.Crop
//                )
//            } else {
//                // Mientras el hilo de fondo trabaja, el usuario ve las letras iniciales (Cero congelamientos de UI)
//                Text(
//                    text = item.name.take(2).uppercase(),
//                    color = Color.LightGray,
//                    style = MaterialTheme.typography.titleSmall
//                )
//            }
//        }
//        Spacer(modifier = Modifier.height(6.dp))
//        Text(
//            text = item.name,
//            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
//            style = MaterialTheme.typography.labelSmall,
//            maxLines = 1,
//            overflow = TextOverflow.Ellipsis
//        )
//    }
//}