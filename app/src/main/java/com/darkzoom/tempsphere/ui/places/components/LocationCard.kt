package com.darkzoom.tempsphere.ui.places.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Navigation
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkzoom.tempsphere.R
import com.darkzoom.tempsphere.data.local.model.SavedLocation
import com.darkzoom.tempsphere.ui.common.components.WeatherIllustration

@Composable
fun LocationCard(
    location: SavedLocation,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val borderColor = if (location.isCurrent)
        Color(0xFFA78BFA).copy(alpha = 0.45f)
    else
        Color.White.copy(alpha = 0.12f)

    val shadowColor = if (location.isCurrent)
        Color(0xFF7C3AED).copy(alpha = 0.35f)
    else
        Color.Black.copy(alpha = 0.4f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = if (location.isCurrent) 12.dp else 8.dp,
                    shape = RoundedCornerShape(24.dp),
                    spotColor = shadowColor
                )
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.linearGradient(location.gradientColors))
                .border(1.dp, borderColor, RoundedCornerShape(24.dp))
                .clickable(onClick = onClick)
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.padding(end = 60.dp)) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${location.city}, ${location.country}",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.offset(y = (-4).dp)
                ) {
                    Text(
                        text = "${location.temp}",
                        color = Color.White,
                        fontSize = 56.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-2).sp
                    )
                    Text(
                        text = "°",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }

                Text(
                    text = location.description,
                    color = Color.White.copy(alpha = 0.65f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.high_low, location.high, location.low),
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "  ·  ",
                        color = Color.White.copy(alpha = 0.35f),
                        fontSize = 12.sp
                    )
                    Text(
                        text = location.time,
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                }
            }

            if (location.isCurrent) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFA78BFA).copy(alpha = 0.25f))
                        .border(
                            1.dp,
                            Color(0xFFA78BFA).copy(alpha = 0.4f),
                            RoundedCornerShape(50)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Navigation,
                        contentDescription = null,
                        tint = Color(0xFFC4B5FD),
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Current",
                        color = Color(0xFFC4B5FD),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        WeatherIllustration(
            type = location.type,
            size = 80.dp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-12).dp, y = (-28).dp)
        )
    }
}