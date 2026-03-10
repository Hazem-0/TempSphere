package com.darkzoom.tempsphere.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkzoom.tempsphere.R
import com.darkzoom.tempsphere.ui.core.Theme.LocalAppTheme
import com.darkzoom.tempsphere.data.remote.model.ForecastTab

@Composable
fun ForecastTabs(
    activeTab: ForecastTab,
    onTabSelected: (ForecastTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppTheme.current

    Row(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ForecastTab.entries.forEach { tab ->
            val isActive = tab == activeTab
            val label    = if (tab == ForecastTab.HOURLY) stringResource(R.string.hourly) else stringResource(
                R.string.days
            )
            val shape    = RoundedCornerShape(50)

            Box(
                modifier = Modifier
                    .clip(shape)
                    .then(
                        if (isActive)
                            Modifier.background(
                                Brush.linearGradient(
                                    listOf(
                                        colors.accentPrimary.copy(alpha = 0.35f),
                                        colors.accentSecondary.copy(alpha = 0.20f)
                                    )
                                )
                            )
                        else
                            Modifier.background(Color.Transparent)
                    )
                    .border(
                        width = 1.dp,
                        color = if (isActive) colors.accentPrimary.copy(alpha = 0.4f) else colors.glassBorder,
                        shape = shape
                    )
                    .clickable { onTabSelected(tab) }
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text       = label,
                    color      = if (isActive) colors.accentPrimary else colors.textSecondary,
                    fontSize   = 13.sp,
                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}