package com.darkzoom.tempsphere.ui.settings.components

import com.darkzoom.tempsphere.ui.common.components.GlassCard
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GlassSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier.padding(bottom = 20.dp)) {
        Text(
            text = title.uppercase(),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.45f),
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.2.sp
            ),
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        GlassCard(contentPadding = 0.dp) {
            content()
        }
    }
}