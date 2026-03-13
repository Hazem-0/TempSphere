package com.darkzoom.tempsphere.ui.alert.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkzoom.tempsphere.ui.common.components.GlassCard
import com.darkzoom.tempsphere.ui.settings.components.GlassToggle
import com.darkzoom.tempsphere.ui.core.Theme.LocalAppTheme

@Composable
fun AlertCard(
    timeText: String,
    alertType: String,
    isEnabled: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val themeColors = LocalAppTheme.current
    val iconColor = if (alertType == "Alarm Sound") themeColors.accentPrimary else themeColors.accentSecondary
    val icon = if (alertType == "Alarm Sound") Icons.Rounded.Alarm else Icons.Rounded.NotificationsActive

    GlassCard(modifier = modifier.fillMaxWidth(), contentPadding = 16.dp) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .shadow(elevation = 8.dp, shape = CircleShape, spotColor = iconColor)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = timeText,
                    color = themeColors.textPrimary,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                )
                Text(
                    text = alertType,
                    color = themeColors.textSecondary,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                GlassToggle(
                    enabled = isEnabled,
                    onToggle = onToggle,
                    color = iconColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Rounded.DeleteOutline,
                        contentDescription = null,
                        tint = themeColors.pressureIcon,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}