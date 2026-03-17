package com.darkzoom.tempsphere.ui.places.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkzoom.tempsphere.R
import com.darkzoom.tempsphere.ui.core.Theme.AppThemeColors
import com.darkzoom.tempsphere.ui.places.viewmodel.MapPickerUiState

@Composable
 fun BottomConfirmCard(
    uiState: MapPickerUiState,
    theme: AppThemeColors,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(16.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        androidx.compose.ui.graphics.Color(0xFF0F0837).copy(alpha = 0.97f),
                        androidx.compose.ui.graphics.Color(0xFF1A1645).copy(alpha = 0.97f)
                    )
                )
            )
            .border(1.dp, theme.accentPrimary.copy(alpha = 0.3f), RoundedCornerShape(28.dp))
            .padding(24.dp)
    ) {
        when (uiState) {
            is MapPickerUiState.Resolving -> {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = theme.accentPrimary, strokeWidth = 2.dp)
                    Text(stringResource(R.string.resolving_location), color = theme.textSecondary, fontSize = 14.sp)
                }
            }
            is MapPickerUiState.Resolved -> {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(theme.accentPrimary.copy(alpha = 0.18f))
                                .border(1.dp, theme.accentPrimary.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.LocationOn, null, tint = theme.accentPrimary, modifier = Modifier.size(20.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(uiState.city, color = theme.textPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(
                                buildString {
                                    if (uiState.country.isNotBlank()) append("${uiState.country}  ·  ")
                                    append(String.format("%.4f°, %.4f°", uiState.latitude, uiState.longitude))
                                },
                                color = theme.textSecondary.copy(alpha = 0.5f),
                                fontSize = 11.sp
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, theme.glassBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = theme.textSecondary)
                        ) {
                            Text(stringResource(R.string.clear), fontSize = 14.sp)
                        }
                        Button(
                            onClick = onConfirm,
                            modifier = Modifier
                                .weight(1f)
                                .shadow(
                                    8.dp,
                                    RoundedCornerShape(14.dp),
                                    spotColor = theme.accentPrimary
                                )
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            theme.accentPrimary,
                                            theme.accentSecondary
                                        )
                                    )
                                ),
                            colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Rounded.Check, null, modifier = Modifier.size(16.dp))
                                Text(stringResource(R.string.save_place), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
            is MapPickerUiState.Saving -> {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = theme.accentPrimary, strokeWidth = 2.dp)
                    Text(stringResource(R.string.saving), color = theme.textSecondary, fontSize = 14.sp)
                }
            }
            else -> {}
        }
    }
}
