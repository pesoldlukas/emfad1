package com.emfad.app.views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.emfad.app.models.MaterialPhysicsAnalysis
import com.emfad.app.models.MaterialType
import com.emfad.app.models.LayerAnalysis
import kotlin.math.*

@Composable
fun Material3DVisualization(
    analysis: MaterialPhysicsAnalysis,
    modifier: Modifier = Modifier
) {
    var rotationX by remember { mutableStateOf(0f) }
    var rotationY by remember { mutableStateOf(0f) }
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "3D-Materialvisualisierung",
                style = MaterialTheme.typography.h6
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            rotationX += dragAmount.y * 0.5f
                            rotationY += dragAmount.x * 0.5f
                        }
                    }
                    .pointerInput(Unit) {
                        detectTransformGestures { centroid, pan, zoom, rotation ->
                            scale = (scale * zoom).coerceIn(0.5f, 3f)
                            offset += pan
                        }
                    }
            ) {
                val width = size.width
                val height = size.height
                val centerX = width / 2 + offset.x
                val centerY = height / 2 + offset.y
                
                // Hintergrund
                drawRect(
                    color = Color.LightGray.copy(alpha = 0.2f),
                    size = size
                )
                
                // Transformiere den Zeichenbereich
                scale(scale) {
                    // Koordinatensystem
                    drawCoordinateSystem(width, height, rotationX, rotationY, centerX, centerY)
                    
                    // Schichtanalyse
                    analysis.layerAnalysis.forEach { layer ->
                        drawLayer(layer, rotationX, rotationY, centerX, centerY)
                    }
                    
                    // Materialvisualisierung
                    when (analysis.materialType) {
                        MaterialType.FERROUS_METAL -> drawFerrousMetal(
                            depth = analysis.depth,
                            size = analysis.size,
                            confidence = analysis.confidence,
                            rotationX = rotationX,
                            rotationY = rotationY,
                            centerX = centerX,
                            centerY = centerY
                        )
                        MaterialType.NON_FERROUS_METAL -> drawNonFerrousMetal(
                            depth = analysis.depth,
                            size = analysis.size,
                            confidence = analysis.confidence,
                            rotationX = rotationX,
                            rotationY = rotationY,
                            centerX = centerX,
                            centerY = centerY
                        )
                        MaterialType.CAVITY -> drawCavity(
                            depth = analysis.depth,
                            size = analysis.size,
                            confidence = analysis.confidence,
                            rotationX = rotationX,
                            rotationY = rotationY,
                            centerX = centerX,
                            centerY = centerY
                        )
                        else -> drawUnknownMaterial(
                            depth = analysis.depth,
                            size = analysis.size,
                            rotationX = rotationX,
                            rotationY = rotationY,
                            centerX = centerX,
                            centerY = centerY
                        )
                    }
                    
                    // Anomalien zeichnen
                    drawAnomalies(analysis, rotationX, rotationY, centerX, centerY)
                }
            }
            
            // Zoom-Steuerung
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { scale = (scale * 0.8f).coerceIn(0.5f, 3f) },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("-")
                }
                Button(
                    onClick = { scale = 1f },
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text("Reset")
                }
                Button(
                    onClick = { scale = (scale * 1.2f).coerceIn(0.5f, 3f) },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("+")
                }
            }
        }
    }
}

private fun DrawScope.drawCoordinateSystem(
    width: Float,
    height: Float,
    rotationX: Float,
    rotationY: Float,
    centerX: Float,
    centerY: Float
) {
    // Transformiere Koordinaten basierend auf Rotation
    val transform = Matrix().apply {
        rotate(rotationX, centerX, centerY)
        rotate(rotationY, centerX, centerY)
    }
    
    // X-Achse
    drawLine(
        color = Color.Gray,
        start = transform.map(Offset(0f, centerY)),
        end = transform.map(Offset(width, centerY)),
        strokeWidth = 2f
    )
    
    // Y-Achse
    drawLine(
        color = Color.Gray,
        start = transform.map(Offset(centerX, 0f)),
        end = transform.map(Offset(centerX, height)),
        strokeWidth = 2f
    )
    
    // Z-Achse (perspektivisch)
    drawLine(
        color = Color.Gray,
        start = transform.map(Offset(centerX, centerY)),
        end = transform.map(Offset(centerX + width * 0.3f, centerY - height * 0.3f)),
        strokeWidth = 2f
    )
}

private fun DrawScope.drawFerrousMetal(
    depth: Float,
    size: Float,
    confidence: Float,
    rotationX: Float,
    rotationY: Float,
    centerX: Float,
    centerY: Float
) {
    val color = Color.Red.copy(alpha = confidence)
    drawMaterialObject(depth, size, color, rotationX, rotationY, centerX, centerY)
}

private fun DrawScope.drawNonFerrousMetal(
    depth: Float,
    size: Float,
    confidence: Float,
    rotationX: Float,
    rotationY: Float,
    centerX: Float,
    centerY: Float
) {
    val color = Color.Yellow.copy(alpha = confidence)
    drawMaterialObject(depth, size, color, rotationX, rotationY, centerX, centerY)
}

private fun DrawScope.drawCavity(
    depth: Float,
    size: Float,
    confidence: Float,
    rotationX: Float,
    rotationY: Float,
    centerX: Float,
    centerY: Float
) {
    val color = Color.Blue.copy(alpha = confidence)
    drawMaterialObject(depth, size, color, rotationX, rotationY, centerX, centerY, isCavity = true)
}

private fun DrawScope.drawUnknownMaterial(
    depth: Float,
    size: Float,
    rotationX: Float,
    rotationY: Float,
    centerX: Float,
    centerY: Float
) {
    val color = Color.Gray.copy(alpha = 0.5f)
    drawMaterialObject(depth, size, color, rotationX, rotationY, centerX, centerY)
}

private fun DrawScope.drawMaterialObject(
    depth: Float,
    size: Float,
    color: Color,
    rotationX: Float,
    rotationY: Float,
    centerX: Float,
    centerY: Float,
    isCavity: Boolean = false
) {
    val width = size.width
    val height = size.height
    
    // Skalierungsfaktoren
    val depthScale = height / 2000f
    val sizeScale = width / 1000f
    
    // 3D-Koordinaten berechnen
    val x = centerX
    val y = centerY - (depth * depthScale)
    val z = depth * depthScale * 0.5f
    
    // Transformiere Koordinaten
    val transform = Matrix().apply {
        rotate(rotationX, centerX, centerY)
        rotate(rotationY, centerX, centerY)
    }
    
    if (isCavity) {
        // Hohlraum als 3D-Ellipse
        val path = Path().apply {
            addOval(Rect(
                left = x - size * sizeScale / 2,
                top = y - size * sizeScale / 4,
                right = x + size * sizeScale / 2,
                bottom = y + size * sizeScale / 4
            ))
        }
        
        drawPath(
            path = transform.map(path),
            color = color,
            style = Stroke(width = 3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f)))
        )
    } else {
        // 3D-Würfel mit allen Seiten
        val vertices = listOf(
            Offset(x - size * sizeScale / 2, y),
            Offset(x + size * sizeScale / 2, y),
            Offset(x + size * sizeScale / 2, y - z),
            Offset(x - size * sizeScale / 2, y - z),
            Offset(x - size * sizeScale / 2, y + z),
            Offset(x + size * sizeScale / 2, y + z)
        )
        
        // Vorderseite
        drawPath(
            path = Path().apply {
                moveTo(vertices[0])
                lineTo(vertices[1])
                lineTo(vertices[2])
                lineTo(vertices[3])
                close()
            }.let { transform.map(it) },
            color = color,
            style = Fill
        )
        
        // Rückseite
        drawPath(
            path = Path().apply {
                moveTo(vertices[4])
                lineTo(vertices[5])
                lineTo(vertices[2])
                lineTo(vertices[3])
                close()
            }.let { transform.map(it) },
            color = color.copy(alpha = 0.7f),
            style = Fill
        )
        
        // Kanten
        vertices.forEachIndexed { i, vertex ->
            val nextVertex = vertices[(i + 1) % vertices.size]
            drawLine(
                color = color.copy(alpha = 0.5f),
                start = transform.map(vertex),
                end = transform.map(nextVertex),
                strokeWidth = 2f
            )
        }
    }
}

private fun DrawScope.drawLayer(
    layer: LayerAnalysis,
    rotationX: Float,
    rotationY: Float,
    centerX: Float,
    centerY: Float
) {
    val width = size.width
    val height = size.height
    
    // Transformiere Koordinaten
    val transform = Matrix().apply {
        rotate(rotationX, centerX, centerY)
        rotate(rotationY, centerX, centerY)
    }
    
    // Schichtebene zeichnen
    val y = centerY - (layer.depth * (height / 2000f))
    val path = Path().apply {
        moveTo(0f, y)
        lineTo(width, y)
        lineTo(width, y - 20f)
        lineTo(0f, y - 20f)
        close()
    }
    
    // Schichtfläche
    drawPath(
        path = transform.map(path),
        color = Color.Gray.copy(alpha = 0.2f),
        style = Fill
    )
    
    // Schichtkanten
    drawPath(
        path = transform.map(path),
        color = Color.Gray.copy(alpha = 0.5f),
        style = Stroke(width = 1f)
    )
    
    // Schichtbeschriftung
    drawContext.canvas.nativeCanvas.apply {
        val transformedY = transform.map(Offset(10f, y - 5f)).y
        drawText(
            "${layer.material} (${layer.depth}mm)",
            10f,
            transformedY,
            android.graphics.Paint().apply {
                color = android.graphics.Color.GRAY
                textSize = 24f
            }
        )
    }
}

private fun DrawScope.drawAnomalies(
    analysis: MaterialPhysicsAnalysis,
    rotationX: Float,
    rotationY: Float,
    centerX: Float,
    centerY: Float
) {
    val width = size.width
    val height = size.height
    
    // Transformiere Koordinaten
    val transform = Matrix().apply {
        rotate(rotationX, centerX, centerY)
        rotate(rotationY, centerX, centerY)
    }
    
    // Anomalien basierend auf Magnetgradient und Leitfähigkeit
    val anomalyIntensity = analysis.magneticGradient * analysis.conductivity / 1e6f
    
    if (anomalyIntensity > 0.1f) {
        val radius = anomalyIntensity * 50f
        val path = Path().apply {
            addCircle(centerX, centerY - analysis.depth * (height / 2000f), radius)
        }
        
        // Anomalie-Halo
        drawPath(
            path = transform.map(path),
            color = Color.Red.copy(alpha = 0.2f),
            style = Fill
        )
        
        // Anomalie-Kontur
        drawPath(
            path = transform.map(path),
            color = Color.Red.copy(alpha = 0.5f),
            style = Stroke(width = 2f)
        )
    }
}

private fun Matrix.map(path: Path): Path {
    return Path().apply {
        path.forEach { segment ->
            when (segment) {
                is PathSegment.MoveTo -> moveTo(map(segment.point))
                is PathSegment.LineTo -> lineTo(map(segment.point))
                is PathSegment.QuadTo -> quadTo(map(segment.point1), map(segment.point2))
                is PathSegment.CurveTo -> curveTo(
                    map(segment.point1),
                    map(segment.point2),
                    map(segment.point3)
                )
                is PathSegment.Close -> close()
            }
        }
    }
}

private fun Matrix.map(point: Offset): Offset {
    val x = point.x - centerX
    val y = point.y - centerY
    val cosX = cos(rotationX)
    val sinX = sin(rotationX)
    val cosY = cos(rotationY)
    val sinY = sin(rotationY)
    
    val newX = x * cosY - y * sinX * sinY
    val newY = y * cosX
    
    return Offset(newX + centerX, newY + centerY)
} 