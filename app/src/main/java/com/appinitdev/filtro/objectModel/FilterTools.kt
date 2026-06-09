package com.appinitdev.filtro.objectModel

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.opengl.Matrix
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Layers
import com.appinitdev.filtro.EditorFilter
import com.appinitdev.filtro.FilterCategory
import com.appinitdev.filtro.FilterParameter
import com.appinitdev.filtro.R
import com.appinitdev.filtro.TextureItem
import com.appinitdev.gpuimage.filter.*

object FilterTools {

    val categories = listOf(
        FilterCategory("Estilo", Icons.Default.AutoAwesome),
        FilterCategory("Color", Icons.Default.ColorLens),
        FilterCategory("Efectos", Icons.Default.BlurOn),
        FilterCategory("Fusión", Icons.Default.Layers)
    )

    val textures = listOf(
        TextureItem("Texture 01", R.drawable.texture_01),
        TextureItem("Texture 02", R.drawable.texture_02),
        TextureItem("Texture 03", R.drawable.texture_03),
        TextureItem("Texture 04", R.drawable.texture_04),
        TextureItem("Texture 05", R.drawable.texture_05),
        TextureItem("Texture 06", R.drawable.texture_06),
        TextureItem("Texture 07", R.drawable.texture_07),
        TextureItem("Texture 08", R.drawable.texture_08),
        TextureItem("Texture 09", R.drawable.texture_09),
        TextureItem("Texture 10", R.drawable.texture_10),
    )



    fun getAllFilters(context: Context, textureItem: TextureItem): List<EditorFilter> {
        val blendTexture = try {
            if (textureItem.uriString != null) {
                // Cargar desde la galería usando su URI string
                val uri = android.net.Uri.parse(textureItem.uriString)
                context.contentResolver.openInputStream(uri).use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }
            } else {
                // Cargar desde los drawables locales (Por defecto usa papel si resId es null)
                BitmapFactory.decodeResource(
                    context.resources,
                    textureItem.resId ?: R.drawable.texture_01
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Respaldo seguro en caso de error de lectura o permisos
            BitmapFactory.decodeResource(context.resources, R.drawable.texture_01)
        }
        return listOf(
            // ==================== CATEGORÍA: COLOR ====================
            EditorFilter(
                "Contrast",
                "Color",
                GPUImageContrastFilter(),
                listOf(FilterParameter("Fuerza", 50f))
            ),
            EditorFilter(
                "Gamma",
                "Color",
                GPUImageGammaFilter(),
                listOf(FilterParameter("Gamma", 50f))
            ),
            EditorFilter(
                "Brightness",
                "Color",
                GPUImageBrightnessFilter(),
                listOf(FilterParameter("Nivel", 50f))
            ),
            EditorFilter(
                "Saturation",
                "Color",
                GPUImageSaturationFilter(),
                listOf(FilterParameter("Fuerza", 50f))
            ),
            EditorFilter(
                "Exposure",
                "Color",
                GPUImageExposureFilter(),
                listOf(FilterParameter("Exposición", 50f))
            ),
            EditorFilter(
                "Highlights and Shadows", "Color", GPUImageHighlightShadowFilter(), listOf(
                    FilterParameter("Shades", 0f),
                    FilterParameter("High Lights", 100f)
                )
            ),
            EditorFilter(
                "Monocromo",
                "Color",
                GPUImageMonochromeFilter(),
                listOf(FilterParameter("Intensity", 100f))
            ), // [cite: 21, 71]
            EditorFilter(
                "Opacity",
                "Color",
                GPUImageOpacityFilter(),
                listOf(FilterParameter("Opacity", 100f))
            ), // [cite: 21, 71]
            EditorFilter(
                "RGB", "Color", GPUImageRGBFilter(), listOf(
                    FilterParameter("Rojo (Red)", 100f), // [cite: 21, 72]
                    FilterParameter("Verde (Green)", 100f),
                    FilterParameter("Azul (Blue)", 100f)
                )
            ),
            EditorFilter(
                "White Balance", "Color", GPUImageWhiteBalanceFilter(), listOf(
                    FilterParameter("Temperature", 50f), // [cite: 22, 73]
                    FilterParameter("Tint", 50f) // [cite: 22]
                )
            ),
            EditorFilter(
                "Hue",
                "Color",
                GPUImageHueFilter(),
                listOf(FilterParameter("Matiz", 0f))
            ), // [cite: 17, 58]
            EditorFilter("Luminance", "Color", GPUImageLuminanceFilter()), // Sin sliders [cite: 24]
            EditorFilter(
                "Luminance Threshold",
                "Color",
                GPUImageLuminanceThresholdFilter(),
                listOf(FilterParameter("Threshold", 50f))
            ), // [cite: 24, 75]
            EditorFilter(
                "Color Balance", "Color", GPUImageColorBalanceFilter(), listOf(
                    FilterParameter("Red (Mid)", 0f), // [cite: 41, 83]
                    FilterParameter("Green (Mid)", 0f),
                    FilterParameter("Blue (Mid)", 0f)
                )
            ),
            EditorFilter(
                "Levels Min",
                "Color",
                GPUImageLevelsFilter(),
                listOf(FilterParameter("Medium Setting", 50f))
            ), // [cite: 41, 84]
            EditorFilter(
                "Vibrance",
                "Color",
                GPUImageVibranceFilter(),
                listOf(FilterParameter("Intensity", 50f))
            ), // [cite: 42, 88]

            // ==================== CATEGORÍA: ESTILO ====================
            EditorFilter(
                "Sepia",
                "Estilo",
                GPUImageSepiaToneFilter(),
                listOf(FilterParameter("Intensity", 50f))
            ), // [cite: 18, 61]
            EditorFilter("Grayscale", "Estilo", GPUImageGrayscaleFilter()), // [cite: 18]
            EditorFilter("Invert", "Estilo", GPUImageColorInvertFilter()), // [cite: 17]
            EditorFilter(
                "Posterize",
                "Estilo",
                GPUImagePosterizeFilter(),
                listOf(FilterParameter("Levels", 100f))
            ), // [cite: 19, 66]
            EditorFilter(
                "Grouped Filters",
                "Estilo",
                GPUImageFilterGroup(
                    listOf(
                        GPUImageContrastFilter(),
                        GPUImageDirectionalSobelEdgeDetectionFilter(),
                        GPUImageGrayscaleFilter()
                    )
                )
            ), // [cite: 19, 20]
            EditorFilter(
                "Lookup (Amatorka)",
                "Estilo",
                GPUImageLookupFilter().apply {
                    bitmap = BitmapFactory.decodeResource(
                        context.resources,
                        android.R.drawable.ic_menu_gallery
                    )
                }), // [cite: 38]
            EditorFilter("Sketch", "Estilo", GPUImageSketchFilter()), // [cite: 39]
            EditorFilter("Toon", "Estilo", GPUImageToonFilter()), // [cite: 39]
            EditorFilter("Smooth Toon", "Estilo", GPUImageSmoothToonFilter()), // [cite: 39]
            EditorFilter(
                "Solarize",
                "Estilo",
                GPUImageSolarizeFilter(),
                listOf(FilterParameter("Threshold", 0f))
            ), // [cite: 42, 87]
            EditorFilter("Halftone", "Estilo", GPUImageHalftoneFilter()), // [cite: 41]
            EditorFilter(
                "Crosshatch", "Estilo", GPUImageCrosshatchFilter(), listOf(
                    FilterParameter("Espaciado", 50f), // [cite: 38, 77]
                    FilterParameter("Grosor de Línea", 50f)
                )
            ),
            EditorFilter("CGA Color Space", "Estilo", GPUImageCGAColorspaceFilter()), // [cite: 39]

            // ==================== CATEGORÍA: EFECTOS ====================
            EditorFilter(
                "Sobel Edge Detection",
                "Efectos",
                GPUImageSobelEdgeDetectionFilter(),
                listOf(FilterParameter("Line Thickness", 50f))
            ), // [cite: 18, 62]
            EditorFilter(
                "Threshold Edge Detection",
                "Efectos",
                GPUImageThresholdEdgeDetectionFilter(),
                listOf(
                    FilterParameter("Line Thickness", 50f), // [cite: 18, 63]
                    FilterParameter("Threshold/Sensitivity", 90f)
                )
            ),
            EditorFilter(
                "3x3 Convolution",
                "Efectos",
                GPUImage3x3ConvolutionFilter()
            ), // Fijo según matriz [cite: 18, 64]
            EditorFilter(
                "Emboss",
                "Efectos",
                GPUImageEmbossFilter(),
                listOf(FilterParameter("Intensity", 50f))
            ), // [cite: 19, 65]
            EditorFilter(
                "Pixelation",
                "Efectos",
                GPUImagePixelationFilter(),
                listOf(FilterParameter("Pixel Size", 10f))
            ), // [cite: 17, 57]
            EditorFilter(
                "Sharpness",
                "Efectos",
                GPUImageSharpenFilter(),
                listOf(FilterParameter("Sharpness", 50f))
            ), // [cite: 18, 56]
            EditorFilter(
                "Vignette", "Efectos", GPUImageVignetteFilter(), listOf(
                    FilterParameter("Start", 30f), // [cite: 22, 74]
                    FilterParameter("End", 75f),
                    FilterParameter("X", 50f), // El índice 2 que faltaba
                    FilterParameter("Y", 50f)  // El índice 3 que faltaba
                )
            ),
            EditorFilter(
                "Gaussian Blur",
                "Efectos",
                GPUImageGaussianBlurFilter(),
                listOf(FilterParameter("Blur Radio", 50f))
            ), // [cite: 38, 76]
            EditorFilter("Box Blur", "Efectos", GPUImageBoxBlurFilter()), // [cite: 39]
            EditorFilter("Dilation", "Efectos", GPUImageDilationFilter()), // [cite: 39]
            EditorFilter("Kuwahara", "Efectos", GPUImageKuwaharaFilter()), // [cite: 39]
            EditorFilter("RGB Dilation", "Efectos", GPUImageRGBDilationFilter()), // [cite: 39]
            EditorFilter(
                "Bulge Distortion", "Efectos", GPUImageBulgeDistortionFilter(), listOf(
                    FilterParameter("Sphere Radius", 20f), // [cite: 40, 78]
                    FilterParameter("Scale/Deformation\n", 65f),
                    FilterParameter("X", 50f),           // Índice 2 <- AGREGAR
                    FilterParameter("Y", 50f)            // Índice 3 <- AGREGAR

                )
            ),
            EditorFilter(
                "Glass Sphere", "Efectos", GPUImageGlassSphereFilter(),
                listOf(
                    FilterParameter(
                        "Radio", 50f,
                    ), FilterParameter("X", 50f), FilterParameter("Y", 50f)
                )
            ), // [cite: 40, 79]
            EditorFilter(
                "Haze", "Efectos", GPUImageHazeFilter(), listOf(
                    FilterParameter("Distance", 50f), // [cite: 40, 80]
                    FilterParameter("Pendiente/Slope", 50f)
                )
            ),
            EditorFilter("Laplacian", "Efectos", GPUImageLaplacianFilter()), // [cite: 40]
            EditorFilter(
                "Non Maximum Suppression",
                "Efectos",
                GPUImageNonMaximumSuppressionFilter()
            ), // [cite: 40]
          /*  EditorFilter(
                "Sphere Refraction",
                "Efectos",
                GPUImageSphereRefractionFilter(),
                listOf(FilterParameter("Radio", 50f))
            ),*/ // [cite: 40, 81]
            EditorFilter(
                "Swirl",
                "Efectos",
                GPUImageSwirlFilter(),
                listOf(FilterParameter("Angle", 50f),FilterParameter("X", 50f),   // Índice 1 <- AGREGAR
                    FilterParameter("Y", 50f))
            ), // [cite: 40, 81]
            EditorFilter(
                "Weak Pixel Inclusion",
                "Efectos",
                GPUImageWeakPixelInclusionFilter()
            ), // [cite: 41]
            EditorFilter("False Color", "Efectos", GPUImageFalseColorFilter()), // [cite: 41]
            EditorFilter(
                "Bilateral Blur",
                "Efectos",
                GPUImageBilateralBlurFilter(),
                listOf(FilterParameter("Normalización Dist.", 50f))
            ), // [cite: 41, 85]
            EditorFilter("Zoom Blur", "Efectos", GPUImageZoomBlurFilter()), // [cite: 41]
            EditorFilter(
                "Transform (2-D)",
                "Efectos",
                GPUImageTransformFilter(),
                listOf(FilterParameter("Rotación 360", 0f))
            ), // [cite: 16, 86]

            // ==================== CATEGORÍA: FUSIÓN ====================


            EditorFilter(
                "Difference",
                "Fusión",
                GPUImageDifferenceBlendFilter().apply { bitmap = blendTexture },
            ),
//EditorFilter("Source Over", "Fusión", GPUImageSourceOverBlendFilter().apply { bitmap = blendTexture },listOf(FilterParameter("Opacidad", 50f))),
            EditorFilter(
                "Color Burn",
                "Fusión",
                GPUImageColorBurnBlendFilter().apply { bitmap = blendTexture }),
            EditorFilter(
                "Color Dodge",
                "Fusión",
                GPUImageColorDodgeBlendFilter().apply { bitmap = blendTexture }),
            EditorFilter(
                "Darken",
                "Fusión",
                GPUImageDarkenBlendFilter().apply { bitmap = blendTexture }),

// 1. DISSOLVE: Mantiene su slider de mezcla (Index 0)
            EditorFilter(
                "Dissolve",
                "Fusión",
                GPUImageDissolveBlendFilter().apply { bitmap = blendTexture },
                listOf(FilterParameter("Mix", 50f))
            ),

            EditorFilter(
                "Exclusion",
                "Fusión",
                GPUImageExclusionBlendFilter().apply { bitmap = blendTexture },
            ),
            EditorFilter(
                "Hard Light",
                "Fusión",
                GPUImageHardLightBlendFilter().apply { bitmap = blendTexture }),
            EditorFilter(
                "Lighten",
                "Fusión",
                GPUImageLightenBlendFilter().apply { bitmap = blendTexture }),
            EditorFilter(
                "Add",
                "Fusión",
                GPUImageAddBlendFilter().apply { bitmap = blendTexture }),
            EditorFilter(
                "Divide",
                "Fusión",
                GPUImageDivideBlendFilter().apply { bitmap = blendTexture }),
            EditorFilter(
                "Multiply",
                "Fusión",
                GPUImageMultiplyBlendFilter().apply { bitmap = blendTexture }),
            EditorFilter(
                "Overlay",
                "Fusión",
                GPUImageOverlayBlendFilter().apply { bitmap = blendTexture }),
            EditorFilter(
                "Screen",
                "Fusión",
                GPUImageScreenBlendFilter().apply { bitmap = blendTexture }),

// 2. ALPHA: ¡CORREGIDO! Le añadimos su slider de Opacidad (Index 0)
            EditorFilter(
                "Alpha",
                "Fusión",
                GPUImageAlphaBlendFilter().apply { bitmap = blendTexture },
                listOf(FilterParameter("Opacidad", 50f))
            ),

            EditorFilter(
                "Color",
                "Fusión",
                GPUImageColorBlendFilter().apply { bitmap = blendTexture }),
            EditorFilter(
                "Hue",
                "Fusión",
                GPUImageHueBlendFilter().apply { bitmap = blendTexture }),
            EditorFilter(
                "Saturation",
                "Fusión",
                GPUImageSaturationBlendFilter().apply { bitmap = blendTexture }),
            EditorFilter(
                "Luminosity",
                "Fusión",
                GPUImageLuminosityBlendFilter().apply { bitmap = blendTexture }),
            EditorFilter(
                "Linear Burn",
                "Fusión",
                GPUImageLinearBurnBlendFilter().apply { bitmap = blendTexture }),
            EditorFilter(
                "Soft Light",
                "Fusión",
                GPUImageSoftLightBlendFilter().apply { bitmap = blendTexture }),
            EditorFilter(
                "Subtract",
                "Fusión",
                GPUImageSubtractBlendFilter().apply { bitmap = blendTexture }),

// 3. CHROMA KEY: ¡CORREGIDO! Requiere dos sliders (Sensibilidad -> Index 0, Suavizado -> Index 1)
            EditorFilter(
                "Chroma Key",
                "Fusión",
                GPUImageChromaKeyBlendFilter().apply { bitmap = blendTexture },
                listOf(FilterParameter("Sensitivity", 40f), FilterParameter("Suavizado", 10f))
            ),

            // EditorFilter("Fusión: Normal", "Fusión", GPUImageNormalBlendFilter().apply { bitmap = blendTexture })
        )
    }

    fun adjustFilter(filter: GPUImageFilter, parameters: List<FilterParameter>) {
        fun getVal(index: Int, start: Float, end: Float): Float {
            if (index >= parameters.size) return start
            val f = parameters[index].currentValue / 100.0f
            return (end - start) * f + start
        }

        fun getValInt(index: Int, start: Int, end: Int): Int {
            if (index >= parameters.size) return start
            val f = parameters[index].currentValue / 100.0f
            return ((end - start) * f).toInt() + start
        }

        when (filter) {
            is GPUImageContrastFilter -> filter.setContrast(getVal(0, 0.0f, 2.0f)) // [cite: 59]
            is GPUImageGammaFilter -> filter.setGamma(getVal(0, 0.0f, 3.0f)) // [cite: 60]
            is GPUImageBrightnessFilter -> filter.setBrightness(
                getVal(
                    0,
                    -1.0f,
                    1.0f
                )
            ) // [cite: 61]
            is GPUImageSaturationFilter -> filter.setSaturation(getVal(0, 0.0f, 2.0f)) // [cite: 68]
            is GPUImageExposureFilter -> filter.setExposure(getVal(0, -10.0f, 10.0f)) // [cite: 69]
            is GPUImageHighlightShadowFilter -> {
                filter.setShadows(getVal(0, 0.0f, 1.0f)) // [cite: 70]
                filter.setHighlights(getVal(1, 0.0f, 1.0f))
            }

            is GPUImageMonochromeFilter -> filter.setIntensity(getVal(0, 0.0f, 1.0f)) // [cite: 71]
            is GPUImageOpacityFilter -> filter.setOpacity(getVal(0, 0.0f, 1.0f)) // [cite: 71]
            is GPUImageRGBFilter -> {
                filter.setRed(getVal(0, 0.0f, 1.0f)) // [cite: 72]
                filter.setGreen(getVal(1, 0.0f, 1.0f))
                filter.setBlue(getVal(2, 0.0f, 1.0f))
            }

            is GPUImageWhiteBalanceFilter -> {
                filter.setTemperature(getVal(0, 2000.0f, 8000.0f)) // [cite: 73]
                filter.setTint(getVal(1, -100f, 100f))
            }

            is GPUImageHueFilter -> filter.setHue(getVal(0, 0.0f, 360.0f)) // [cite: 58]
            is GPUImageLuminanceThresholdFilter -> filter.setThreshold(
                getVal(
                    0,
                    0.0f,
                    1.0f
                )
            ) // [cite: 75]
            is GPUImageColorBalanceFilter -> {
                filter.setMidtones(
                    floatArrayOf(
                        getVal(0, 0.0f, 1.0f), // [cite: 83]
                        getVal(1, 0.0f, 1.0f),
                        getVal(2, 0.0f, 1.0f)
                    )
                )
            }

            is GPUImageLevelsFilter -> filter.setMin(
                0.0f,
                getVal(0, 0.0f, 1.0f),
                1.0f
            ) // [cite: 84]
            is GPUImageVibranceFilter -> filter.setVibrance(getVal(0, -1.2f, 1.2f)) // [cite: 88]
            is GPUImageSepiaToneFilter -> filter.setIntensity(getVal(0, 0.0f, 2.0f)) // [cite: 61]
            is GPUImagePosterizeFilter -> filter.setColorLevels(getValInt(0, 1, 50)) // [cite: 66]
            is GPUImageSolarizeFilter -> filter.setThreshold(getVal(0, 0.0f, 1.0f)) // [cite: 87]
            is GPUImageCrosshatchFilter -> {
                filter.setCrossHatchSpacing(getVal(0, 0.0f, 0.06f)) // [cite: 77]
                filter.setLineWidth(getVal(1, 0.0f, 0.006f))
            }

            is GPUImageSobelEdgeDetectionFilter -> filter.setLineSize(
                getVal(
                    0,
                    0.0f,
                    5.0f
                )
            ) // [cite: 62]
            is GPUImageThresholdEdgeDetectionFilter -> {
                filter.setLineSize(getVal(0, 0.0f, 5.0f)) // [cite: 63]
                filter.setThreshold(getVal(1, 0.0f, 1.0f))
            }

            is GPUImageEmbossFilter -> filter.intensity = getVal(0, 0.0f, 4.0f) // [cite: 65]
            is GPUImagePixelationFilter -> filter.setPixel(getVal(0, 1.0f, 100.0f)) // [cite: 57]
            is GPUImageSharpenFilter -> filter.setSharpness(getVal(0, -4.0f, 4.0f)) // [cite: 56]
            is GPUImageVignetteFilter -> {
                filter.setVignetteStart(getVal(0, 0.0f, 1.0f)) // [cite: 74]
                filter.setVignetteEnd(getVal(1, 0.0f, 1.0f))

                val cx = getVal(2, 0.0f, 1.0f)
                val cy = getVal(3, 0.0f, 1.0f)
                filter.setVignetteCenter(PointF(cx, cy))


            }

            is GPUImageGaussianBlurFilter -> filter.setBlurSize(getVal(0, 0.0f, 1.0f)) // [cite: 76]
            is GPUImageBulgeDistortionFilter -> {
                filter.setRadius(getVal(0, 0.0f, 1.0f)) // [cite: 78]
                filter.setScale(getVal(1, -1.0f, 1.0f))
                val cx = getVal(2, 0.0f, 1.0f)
                val cy = getVal(3, 0.0f, 1.0f)
                filter.setCenter(PointF(cx, cy))
            }

            is GPUImageGlassSphereFilter -> {
                filter.setRadius(getVal(0, 0.0f, 1.0f)) //
                // --- AGREGAR ESTO ---
                val cx = getVal(1, 0.0f, 1.0f)
                val cy = getVal(2, 0.0f, 1.0f)
                filter.setCenter(PointF(cx, cy))
            }

            is GPUImageHazeFilter -> {
                filter.setDistance(getVal(0, -0.3f, 0.3f)) // [cite: 80]
                filter.setSlope(getVal(1, -0.3f, 0.3f))
            }

            is GPUImageSphereRefractionFilter -> filter.setRadius(
                getVal(
                    0,
                    0.0f,
                    1.0f
                )
            ) // [cite: 81]
            is GPUImageSwirlFilter ->{
                filter.setAngle(getVal(0, 0.0f, 2.0f)) // Mantiene tu configuración original

                // --- AGREGAR ESTO ---
                val cx = getVal(1, 0.0f, 1.0f)
                val cy = getVal(2, 0.0f, 1.0f)
                filter.setCenter(PointF(cx, cy))
            } // [cite: 81]
            is GPUImageBilateralBlurFilter -> filter.setDistanceNormalizationFactor(
                getVal(
                    0,
                    0.0f,
                    15.0f
                )
            ) // [cite: 85]
            is GPUImageTransformFilter -> {
                val transform = FloatArray(16)
                Matrix.setRotateM(
                    transform,
                    0,
                    (360 * getVal(0, 0f, 100f) / 100),
                    0f,
                    0f,
                    1.0f
                ) // [cite: 86]
                filter.transform3D = transform // [cite: 87]
            }

            is GPUImageMixBlendFilter -> {
                // Controla tanto Dissolve como Alpha Blend usando el slider en el Index 0
                filter.setMix(getVal(0, 0.0f, 1.0f))
            }

            is GPUImageChromaKeyBlendFilter -> {
                // Controla la sensibilidad (Index 0) y el suavizado (Index 1)
                filter.setThresholdSensitivity(getVal(0, 0.0f, 1.0f))
                filter.setSmoothing(getVal(1, 0.0f, 1.0f))
            }

            is GPUImageDissolveBlendFilter -> filter.setMix(getVal(0, 0.0f, 1.0f)) // [cite: 75]
        }
    }
}