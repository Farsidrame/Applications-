package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Pharmacy
import com.example.data.model.UserGpsLocation
import com.example.ui.theme.DutyPharmacyOrange
import com.example.ui.theme.MedicalEmeraldAccent
import com.example.ui.theme.MedicalTealDark
import com.example.ui.theme.MedicalTealLight
import com.example.ui.theme.MedicalTealPrimary
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryMuted
import com.example.ui.theme.VerifiedBadgeGreen
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class ProximityPharmacyEntry(
    val pharmacy: Pharmacy,
    val calculatedDistanceKm: Double,
    val relativeX: Float, // Normalized -1.0 to 1.0 on radar canvas
    val relativeY: Float
)

@Composable
fun NearbyRadarMapCanvas(
    userLocation: UserGpsLocation,
    pharmacies: List<Pharmacy>,
    selectedPharmacy: Pharmacy?,
    onSelectPharmacy: (Pharmacy) -> Unit,
    searchRadiusKm: Double,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "RadarSweep")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulseRadius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulseAlpha"
    )

    // Calculate normalized positions for pharmacies relative to user coords
    val radiusKm = if (searchRadiusKm <= 0) 15.0 else searchRadiusKm
    val maxRadiusLat = radiusKm / 111.0 // ~111km per latitude degree
    val maxRadiusLon = radiusKm / (111.0 * cos(Math.toRadians(userLocation.latitude)).coerceAtLeast(0.1))

    val entries = remember(pharmacies, userLocation, searchRadiusKm) {
        pharmacies.map { pharm ->
            val dLat = pharm.latitude - userLocation.latitude
            val dLon = pharm.longitude - userLocation.longitude

            // Calculate haversine distance
            val r = 6371.0
            val lat1Rad = Math.toRadians(userLocation.latitude)
            val lat2Rad = Math.toRadians(pharm.latitude)
            val dLatRad = Math.toRadians(dLat)
            val dLonRad = Math.toRadians(dLon)
            val a = sin(dLatRad / 2) * sin(dLatRad / 2) +
                    cos(lat1Rad) * cos(lat2Rad) * sin(dLonRad / 2) * sin(dLonRad / 2)
            val c = 2 * kotlin.math.atan2(sqrt(a), sqrt(1 - a))
            val distKm = ((r * c) * 10.0).let { kotlin.math.round(it) / 10.0 }

            val normX = (dLon / maxRadiusLon).toFloat().coerceIn(-0.85f, 0.85f)
            val normY = (-dLat / maxRadiusLat).toFloat().coerceIn(-0.85f, 0.85f) // invert Y for screen coords

            ProximityPharmacyEntry(
                pharmacy = pharm,
                calculatedDistanceKm = distKm,
                relativeX = normX,
                relativeY = normY
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0F2623)) // Dark medical navy/teal radar background
            .testTag("nearby_radar_map_canvas")
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(entries) {
                    detectTapGestures { tapOffset ->
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        val maxPixelRadius = (minOf(centerX, centerY) * 0.85f)

                        // Find closest pharmacy pin tapped
                        val tappedEntry = entries.minByOrNull { entry ->
                            val pinX = centerX + entry.relativeX * maxPixelRadius
                            val pinY = centerY + entry.relativeY * maxPixelRadius
                            val dx = tapOffset.x - pinX
                            val dy = tapOffset.y - pinY
                            dx * dx + dy * dy
                        }

                        if (tappedEntry != null) {
                            val pinX = centerX + tappedEntry.relativeX * maxPixelRadius
                            val pinY = centerY + tappedEntry.relativeY * maxPixelRadius
                            val distanceSq = (tapOffset.x - pinX) * (tapOffset.x - pinX) + (tapOffset.y - pinY) * (tapOffset.y - pinY)
                            // Tap radius within 36dp
                            if (distanceSq < 50f * 50f) {
                                onSelectPharmacy(tappedEntry.pharmacy)
                            }
                        }
                    }
                }
        ) {
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            val maxPixelRadius = (minOf(centerX, centerY) * 0.85f)

            // Draw radar grid rings
            val ringCount = 4
            for (i in 1..ringCount) {
                val r = maxPixelRadius * (i.toFloat() / ringCount)
                drawCircle(
                    color = Color(0xFF1E4D45).copy(alpha = 0.5f),
                    radius = r,
                    center = Offset(centerX, centerY),
                    style = Stroke(width = 1.2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                )
            }

            // Draw crosshairs
            drawLine(
                color = Color(0xFF1E4D45).copy(alpha = 0.4f),
                start = Offset(centerX - maxPixelRadius, centerY),
                end = Offset(centerX + maxPixelRadius, centerY),
                strokeWidth = 1f
            )
            drawLine(
                color = Color(0xFF1E4D45).copy(alpha = 0.4f),
                start = Offset(centerX, centerY - maxPixelRadius),
                end = Offset(centerX, centerY + maxPixelRadius),
                strokeWidth = 1f
            )

            // Draw pulsating wave from center
            drawCircle(
                color = Color(0xFF00E676).copy(alpha = pulseAlpha),
                radius = maxPixelRadius * pulseRadius,
                center = Offset(centerX, centerY),
                style = Stroke(width = 2.5f)
            )

            // Draw User Center Location Pin
            drawCircle(
                color = Color(0xFF00E676).copy(alpha = 0.3f),
                radius = 16f,
                center = Offset(centerX, centerY)
            )
            drawCircle(
                color = Color(0xFF00E676),
                radius = 7f,
                center = Offset(centerX, centerY)
            )
            drawCircle(
                color = Color.White,
                radius = 3f,
                center = Offset(centerX, centerY)
            )

            // Draw Pharmacy Pins
            entries.forEach { entry ->
                val pinX = centerX + entry.relativeX * maxPixelRadius
                val pinY = centerY + entry.relativeY * maxPixelRadius
                val isSelected = entry.pharmacy.id == selectedPharmacy?.id

                val pinColor = if (entry.pharmacy.isDutyPharmacy) {
                    DutyPharmacyOrange
                } else {
                    Color(0xFF26A69A)
                }

                if (isSelected) {
                    // Highlight selected halo
                    drawCircle(
                        color = Color.White.copy(alpha = 0.5f),
                        radius = 20f,
                        center = Offset(pinX, pinY)
                    )
                    drawCircle(
                        color = MedicalTealPrimary,
                        radius = 14f,
                        center = Offset(pinX, pinY)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 5f,
                        center = Offset(pinX, pinY)
                    )
                } else {
                    drawCircle(
                        color = pinColor.copy(alpha = 0.35f),
                        radius = 12f,
                        center = Offset(pinX, pinY)
                    )
                    drawCircle(
                        color = pinColor,
                        radius = 6.5f,
                        center = Offset(pinX, pinY)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 2.5f,
                        center = Offset(pinX, pinY)
                    )
                }
            }
        }

        // Overlay Legend / Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF071714).copy(alpha = 0.75f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    Icons.Default.MyLocation,
                    contentDescription = null,
                    tint = Color(0xFF00E676),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "GPS: ${userLocation.district}",
                    fontSize = 11.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF071714).copy(alpha = 0.75f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Rayon: ${radiusKm.toInt()} km • ${entries.size} trouvée(s)",
                    fontSize = 11.sp,
                    color = Color(0xFF80CBC4),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Bottom Radar Indicator
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF26A69A))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Certifiée", fontSize = 10.sp, color = Color(0xFFE0F2F1))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(DutyPharmacyOrange)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("De Garde 24h", fontSize = 10.sp, color = Color(0xFFE0F2F1))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00E676))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Vous", fontSize = 10.sp, color = Color(0xFF00E676), fontWeight = FontWeight.Bold)
            }
        }
    }
}
