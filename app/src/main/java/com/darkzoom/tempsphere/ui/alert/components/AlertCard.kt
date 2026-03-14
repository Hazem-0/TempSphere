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
import com.darkzoom.tempsphere.data.local.model.AlertModel
import com.darkzoom.tempsphere.data.local.model.RepeatMode
import com.darkzoom.tempsphere.ui.common.components.GlassCard
import com.darkzoom.tempsphere.ui.settings.components.GlassToggle
import com.darkzoom.tempsphere.ui.core.Theme.LocalAppTheme


@Composable
fun AlertCard(
    alert: AlertModel,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val themeColors = LocalAppTheme.current
    val isAlarm = alert.alertType == AlertModel.TYPE_ALARM
    val iconColor = if (isAlarm) themeColors.accentPrimary else themeColors.accentSecondary
    val icon = if (isAlarm) Icons.Rounded.Alarm else Icons.Rounded.NotificationsActive

    val cardTransparency = if (alert.isEnabled) 1f else 0.5f

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        contentPadding = 16.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .shadow(
                        elevation = if (alert.isEnabled) 8.dp else 0.dp,
                        shape = CircleShape,
                        spotColor = iconColor
                    )
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f * cardTransparency)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor.copy(alpha = cardTransparency),
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alert.timeText,
                    color = themeColors.textPrimary.copy(alpha = cardTransparency),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                )
                Text(
                    text = buildSubtitle(alert.alertType, alert.repeatMode),
                    color = themeColors.textSecondary.copy(alpha = cardTransparency),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    modifier = Modifier.padding(top = 3.dp)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                GlassToggle(
                    enabled = alert.isEnabled,
                    onToggle = onToggle,
                    color = iconColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteOutline,
                        contentDescription = "Delete alert",
                        tint = themeColors.pressureIcon,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private fun buildSubtitle(alertType: String, repeatMode: RepeatMode): String =
    "$alertType  ·  ${repeatMode.displayLabel()}"