package com.darkzoom.tempsphere.ui.alert.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.darkzoom.tempsphere.ui.common.components.GlassCard
import com.darkzoom.tempsphere.ui.settings.components.OptionPicker
import com.darkzoom.tempsphere.ui.settings.components.SettingsRow
import com.darkzoom.tempsphere.ui.core.Theme.LocalAppTheme
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowAlertDialog(
    onDismiss: () -> Unit,
    onSave: (timeText: String, type: String, hour: Int, minute: Int) -> Unit
) {
    var selectedTime by remember { mutableStateOf("08:00 AM") }
    var selectedHour by remember { mutableIntStateOf(8) }
    var selectedMinute by remember { mutableIntStateOf(0) }
    var selectedType by remember { mutableStateOf("Notification") }
    var showTimePicker by remember { mutableStateOf(false) }

    val themeColors = LocalAppTheme.current

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedHour,
            initialMinute = selectedMinute,
            is24Hour = false
        )

        Dialog(onDismissRequest = { showTimePicker = false }) {
            GlassCard(modifier = Modifier.fillMaxWidth(), contentPadding = 16.dp) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Select Time",
                        color = themeColors.textPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    TimePicker(
                        state = timePickerState,
                        colors = TimePickerDefaults.colors(
                            clockDialColor = Color(0xFF161830).copy(alpha = 0.5f),
                            clockDialSelectedContentColor = themeColors.textPrimary,
                            clockDialUnselectedContentColor = themeColors.textSecondary,
                            selectorColor = themeColors.accentPrimary,
                            containerColor = Color.Transparent,
                            periodSelectorBorderColor = themeColors.accentPrimary,
                            periodSelectorSelectedContainerColor = themeColors.accentPrimary.copy(alpha = 0.2f),
                            periodSelectorUnselectedContainerColor = Color.Transparent,
                            periodSelectorSelectedContentColor = themeColors.accentPrimary,
                            periodSelectorUnselectedContentColor = themeColors.textSecondary,
                            timeSelectorSelectedContainerColor = themeColors.accentPrimary.copy(alpha = 0.2f),
                            timeSelectorUnselectedContainerColor = Color(0xFF161830).copy(alpha = 0.5f),
                            timeSelectorSelectedContentColor = themeColors.accentPrimary,
                            timeSelectorUnselectedContentColor = themeColors.textPrimary
                        )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showTimePicker = false }) {
                            Text("Cancel", color = themeColors.textSecondary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = {
                            selectedHour = timePickerState.hour
                            selectedMinute = timePickerState.minute
                            val amPm = if (selectedHour >= 12) "PM" else "AM"
                            val formattedHour = if (selectedHour == 0) 12 else if (selectedHour > 12) selectedHour - 12 else selectedHour
                            selectedTime = String.format(Locale.getDefault(), "%02d:%02d %s", formattedHour, selectedMinute, amPm)
                            showTimePicker = false
                        }) {
                            Text("OK", color = themeColors.accentPrimary)
                        }
                    }
                }
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        GlassCard(modifier = Modifier.fillMaxWidth(), contentPadding = 0.dp) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "New Weather Alert",
                        color = themeColors.textPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Rounded.Close, contentDescription = null, tint = themeColors.textSecondary)
                    }
                }

                HorizontalDivider(color = themeColors.glassBorder)

                Text(
                    text = "ALERT TIME",
                    color = themeColors.textSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp)
                )

                SettingsRow(
                    icon = Icons.Rounded.AccessTime,
                    iconColor = themeColors.accentPrimary,
                    label = "Time",
                    value = selectedTime,
                    isLast = true,
                    onClick = { showTimePicker = true }
                )

                Text(
                    text = "ALERT TYPE",
                    color = themeColors.textSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 12.dp)
                )

                OptionPicker(
                    options = listOf("Notification", "Alarm Sound"),
                    selected = selectedType,
                    onSelect = { selectedType = it },
                    accentColor = themeColors.accentSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Button(
                    onClick = { onSave(selectedTime, selectedType, selectedHour, selectedMinute) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = themeColors.accentPrimary.copy(alpha = 0.8f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Save Alert", color = themeColors.textPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}