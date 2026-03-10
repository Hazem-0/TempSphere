package com.darkzoom.tempsphere.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Air
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.Compress
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkzoom.tempsphere.ui.common.Theme.LocalAppTheme

private data class MetricTileData(
    val label: String,
    val value: String,
    val icon: ImageVector,
    val accentColor: Color
)

@Composable
fun MetricCard(
    humidity: Int,
    windMs: Float,
    pressureHpa: Int,
    cloudinessPct: Int,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppTheme.current
    val tiles = listOf(
        MetricTileData("Humidity",   "$humidity%",       Icons.Rounded.WaterDrop, colors.humidityIcon),
        MetricTileData("Wind",       "$windMs m/s",      Icons.Rounded.Air,       colors.windIcon),
        MetricTileData("Pressure",   "$pressureHpa hPa", Icons.Rounded.Compress,  colors.pressureIcon),
        MetricTileData("Cloudiness", "$cloudinessPct%",  Icons.Rounded.Cloud,     colors.cloudIcon),
    )

    Row(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricTile(tiles[0])
            MetricTile(tiles[2])
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricTile(tiles[1])
            MetricTile(tiles[3])
        }
    }
}

@Composable
private fun MetricTile(data: MetricTileData) {
    val colors = LocalAppTheme.current
    val shape = RoundedCornerShape(16.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.glassBg)
            .border(1.dp, colors.glassBorder, shape)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(11.dp))
                .drawBehind {
                    drawCircle(data.accentColor.copy(alpha = 0.28f), size.minDimension * 0.85f)
                }
                .background(data.accentColor.copy(alpha = 0.15f))
                .border(1.dp, data.accentColor.copy(alpha = 0.22f), RoundedCornerShape(11.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = data.icon,
                contentDescription = data.label,
                tint = data.accentColor,
                modifier = Modifier.size(17.dp)
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = data.label,
                color = colors.textSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
            )
            Text(
                text = data.value,
                color = colors.textPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}