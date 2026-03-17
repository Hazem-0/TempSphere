package com.darkzoom.tempsphere.ui.core.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkzoom.tempsphere.R
import com.darkzoom.tempsphere.ui.core.Theme.LocalAppTheme
import com.darkzoom.tempsphere.ui.common.components.GlassCard
import com.darkzoom.tempsphere.data.local.model.DailyWeather
import com.darkzoom.tempsphere.data.local.model.ForecastTab
import com.darkzoom.tempsphere.data.local.model.HourlyWeather
import com.darkzoom.tempsphere.ui.home.components.DailyForecastList
import com.darkzoom.tempsphere.ui.home.components.ForecastTabs
import com.darkzoom.tempsphere.ui.home.components.HourlyForecastRow
import com.darkzoom.tempsphere.ui.home.components.MetricCard

@Composable
fun GlassSection(
    activeTab: ForecastTab,
    onTabSelected: (ForecastTab) -> Unit,
    hourlyItems: List<HourlyWeather>,
    dailyItems: List<DailyWeather>,
    humidity: Int,
    windMs: Float,
    pressureHpa: Int,
    cloudinessPct: Int,
    unitSymbol: String,
    windUnit: String,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppTheme.current

    GlassCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(colors.glassBorder)
            )
        }

        ForecastTabs(
            activeTab = activeTab,
            onTabSelected = onTabSelected,
            modifier = Modifier.padding(top = 4.dp)
        )

        AnimatedContent(
            targetState = activeTab,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = stringResource(R.string.forecast_tab)
        ) { tab ->
            when (tab) {
                ForecastTab.HOURLY -> HourlyForecastRow(
                    items = hourlyItems,
                    unitSymbol = unitSymbol,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                ForecastTab.WEEKLY -> DailyForecastList(
                    items = dailyItems,
                    unitSymbol = unitSymbol,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(horizontal = 16.dp)
                .background(colors.glassBorder)
        )

        Text(
            text = stringResource(R.string.conditions),
            color = colors.textSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.8.sp,
            modifier = Modifier.padding(start = 20.dp, top = 14.dp, bottom = 0.dp)
        )

        MetricCard(
            humidity = humidity,
            windMs = windMs,
            pressureHpa = pressureHpa,
            cloudinessPct = cloudinessPct,
            windUnit = windUnit
        )
    }
}