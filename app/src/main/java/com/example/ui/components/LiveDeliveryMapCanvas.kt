package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LiveCourierTelemetry
import com.example.ui.theme.MedicalEmeraldAccent
import com.example.ui.theme.MedicalTealDark
import com.example.ui.theme.MedicalTealPrimary
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.VerifiedBadgeGreen

@Composable
fun LiveDeliveryMapCanvas(
    telemetry: LiveCourierTelemetry,
    pharmacyName: String,
    deliveryAddress: String,
    modifier: Modifier = Modifier
) {
    var isSatelliteView by remember { mutableStateOf(false) }

    // Pulsing animation for courier sonar radar
    val infiniteTransition = rememberInfiniteTransition(label = "RadarPulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 38f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulseRadius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulseAlpha"
    )

    // Dash phase animation for live route line
    val dashPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "DashPhase"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .testTag("live_delivery_map_canvas"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSatelliteView) Color(0xFF1E293B) else Color(0xFFF1F6F4)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Vector Map Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Draw map background elements
                drawMapBackground(
                    width = canvasWidth,
                    height = canvasHeight,
                    isSatellite = isSatelliteView
                )

                // Define delivery route points (Pharmacy at top-left -> Dakar roads -> Destination at bottom-right)
                val startPoint = Offset(canvasWidth * 0.18f, canvasHeight * 0.25f)
                val waypoint1 = Offset(canvasWidth * 0.35f, canvasHeight * 0.40f)
                val waypoint2 = Offset(canvasWidth * 0.58f, canvasHeight * 0.45f)
                val waypoint3 = Offset(canvasWidth * 0.72f, canvasHeight * 0.65f)
                val endPoint = Offset(canvasWidth * 0.84f, canvasHeight * 0.78f)

                // Draw planned road route
                val routePath = Path().apply {
                    moveTo(startPoint.x, startPoint.y)
                    cubicTo(
                        waypoint1.x, waypoint1.y,
                        waypoint2.x, waypoint2.y,
                        waypoint3.x, waypoint3.y
                    )
                    lineTo(endPoint.x, endPoint.y)
                }

                // Road base glow
                drawPath(
                    path = routePath,
                    color = MedicalTealPrimary.copy(alpha = 0.25f),
                    style = Stroke(
                        width = 14f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // Active animated route line
                drawPath(
                    path = routePath,
                    color = MedicalTealPrimary,
                    style = Stroke(
                        width = 6f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 10f), dashPhase)
                    )
                )

                // Draw Pharmacy Marker (Start)
                drawCircle(
                    color = Color(0xFF004D40),
                    radius = 16f,
                    center = startPoint
                )
                drawCircle(
                    color = Color.White,
                    radius = 12f,
                    center = startPoint
                )
                drawCircle(
                    color = VerifiedBadgeGreen,
                    radius = 8f,
                    center = startPoint
                )

                // Draw Destination Marker (Home)
                drawCircle(
                    color = Color(0xFFE65100),
                    radius = 18f,
                    center = endPoint
                )
                drawCircle(
                    color = Color.White,
                    radius = 13f,
                    center = endPoint
                )
                drawCircle(
                    color = Color(0xFFE65100),
                    radius = 9f,
                    center = endPoint
                )

                // Calculate current courier position along the bezier trajectory
                val t = telemetry.progress.coerceIn(0f, 1f)
                val courierPos = calculateBezierPoint(startPoint, waypoint1, waypoint2, waypoint3, endPoint, t)

                // Animated Sonar Radar circle
                drawCircle(
                    color = MedicalTealPrimary.copy(alpha = pulseAlpha),
                    radius = pulseRadius,
                    center = courierPos
                )

                // Courier marker bubble
                drawCircle(
                    color = Color.White,
                    radius = 18f,
                    center = courierPos
                )
                drawCircle(
                    color = MedicalTealPrimary,
                    radius = 14f,
                    center = courierPos
                )
                drawCircle(
                    color = Color(0xFFFFD54F),
                    radius = 5f,
                    center = courierPos
                )
            }

            // Top HUD Overlay: Live GPS badge and Map Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Live Status Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xDD004D40))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E676))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "GPS DIRECT • ${telemetry.speedKmh} KM/H",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Map View Switcher
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xCCFFFFFF))
                        .border(1.dp, Color(0x33004D40), RoundedCornerShape(12.dp))
                        .padding(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { isSatelliteView = !isSatelliteView },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = "Changer vue",
                            tint = MedicalTealPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Start & End Label tags on map
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp, top = 48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xDDFFFFFF))
                    .border(1.dp, Color(0xFFB2DFDB), RoundedCornerShape(8.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalPharmacy,
                        contentDescription = null,
                        tint = VerifiedBadgeGreen,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = pharmacyName.take(18) + if (pharmacyName.length > 18) "…" else "",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF004D40)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 68.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xDDFFFFFF))
                    .border(1.dp, Color(0xFFFFCC80), RoundedCornerShape(8.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = Color(0xFFE65100),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Votre adresse",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    )
                }
            }

            // Bottom Floating Telemetry Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xF5004D40))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Distance & Street
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0x33FFFFFF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Navigation,
                                contentDescription = null,
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (telemetry.distanceRemainingMeters > 0)
                                    "À ${telemetry.distanceRemainingMeters} m • ${telemetry.currentStreet}"
                                else
                                    "Coursier arrivé à votre porte",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1
                            )
                            Text(
                                text = "Sac isotherme sécurisé : ${telemetry.temperatureCelsius}°C (Chaîne du froid OK)",
                                fontSize = 9.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                    // ETA Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(VerifiedBadgeGreen)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        val minutes = telemetry.etaSeconds / 60
                        val seconds = telemetry.etaSeconds % 60
                        Text(
                            text = if (telemetry.etaSeconds > 0) "${minutes}m ${seconds}s" else "Livré",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

private fun calculateBezierPoint(
    p0: Offset,
    p1: Offset,
    p2: Offset,
    p3: Offset,
    p4: Offset,
    t: Float
): Offset {
    // 2-segment interpolation
    return if (t < 0.5f) {
        val subT = t * 2f
        val x = (1 - subT) * (1 - subT) * p0.x + 2 * (1 - subT) * subT * p1.x + subT * subT * p2.x
        val y = (1 - subT) * (1 - subT) * p0.y + 2 * (1 - subT) * subT * p1.y + subT * subT * p2.y
        Offset(x, y)
    } else {
        val subT = (t - 0.5f) * 2f
        val x = (1 - subT) * (1 - subT) * p2.x + 2 * (1 - subT) * subT * p3.x + subT * subT * p4.x
        val y = (1 - subT) * (1 - subT) * p2.y + 2 * (1 - subT) * subT * p3.y + subT * subT * p4.y
        Offset(x, y)
    }
}

private fun DrawScope.drawMapBackground(width: Float, height: Float, isSatellite: Boolean) {
    val roadColor = if (isSatellite) Color(0xFF334155) else Color(0xFFE2E8F0)
    val parkColor = if (isSatellite) Color(0xFF1E3A2F) else Color(0xFFE8F5E9)
    val oceanColor = if (isSatellite) Color(0xFF0F172A) else Color(0xFFE0F7FA)

    // Coastal / Sea area on west side (typical Dakar peninsula)
    drawRect(
        color = oceanColor,
        topLeft = Offset(0f, 0f),
        size = androidx.compose.ui.geometry.Size(width * 0.12f, height)
    )

    // Green Park Zone (e.g., Fann Corniche / Parc de Hann zone)
    drawRoundRect(
        color = parkColor,
        topLeft = Offset(width * 0.45f, height * 0.15f),
        size = androidx.compose.ui.geometry.Size(width * 0.25f, height * 0.35f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
    )

    // Grid of Secondary Roads (Dakar avenues)
    val roadStroke = Stroke(width = 4f, cap = StrokeCap.Round)
    // Horizontal avenues
    drawLine(roadColor, Offset(0f, height * 0.25f), Offset(width, height * 0.25f), strokeWidth = 5f)
    drawLine(roadColor, Offset(0f, height * 0.45f), Offset(width, height * 0.45f), strokeWidth = 6f)
    drawLine(roadColor, Offset(0f, height * 0.70f), Offset(width, height * 0.70f), strokeWidth = 5f)
    drawLine(roadColor, Offset(0f, height * 0.88f), Offset(width, height * 0.88f), strokeWidth = 4f)

    // Vertical / Diagonal boulevards
    drawLine(roadColor, Offset(width * 0.20f, 0f), Offset(width * 0.20f, height), strokeWidth = 5f)
    drawLine(roadColor, Offset(width * 0.40f, 0f), Offset(width * 0.40f, height), strokeWidth = 4f)
    drawLine(roadColor, Offset(width * 0.65f, 0f), Offset(width * 0.65f, height), strokeWidth = 6f)
    drawLine(roadColor, Offset(width * 0.85f, 0f), Offset(width * 0.85f, height), strokeWidth = 5f)
    drawLine(roadColor, Offset(width * 0.10f, height * 0.90f), Offset(width * 0.90f, height * 0.10f), strokeWidth = 4f)
}
