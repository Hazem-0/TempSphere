package com.darkzoom.tempsphere.ui.settings.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun ExpandableRow(
    icon: ImageVector,
    iconColor: Color,
    label: String,
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    accentColor: Color,
    isOpen: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    isLast: Boolean = false
) {
    val rotation by animateFloatAsState(targetValue = if (isOpen) 90f else 0f, label = "chevron_rotation")

    Column(modifier = modifier) {
        SettingsRow(
            icon = icon,
            iconColor = iconColor,
            label = label,
            value = selected,
            valueColor = iconColor,
            isLast = isLast && !isOpen,
            rightEl = {
                Icon(
                    Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    modifier = Modifier.rotate(rotation)
                )
            },
            onClick = onToggle
        )

        AnimatedVisibility(
            visible = isOpen,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column {
                OptionPicker(
                    options = options,
                    selected = selected,
                    onSelect = onSelect,
                    accentColor = accentColor
                )
                if (!isLast) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}