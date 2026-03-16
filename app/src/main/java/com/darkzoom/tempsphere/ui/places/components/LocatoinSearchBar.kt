package com.darkzoom.tempsphere.ui.places.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkzoom.tempsphere.ui.core.Theme.LocalAppTheme

@Composable
fun LocationSearchBar(
    query:            String,
    onQueryChange:    (String) -> Unit,
    onClearClick:     () -> Unit,
    suggestions:      List<String>,
    onSuggestionClick: (String) -> Unit,
    modifier:         Modifier = Modifier
) {
    val theme           = LocalAppTheme.current
    val showSuggestions = suggestions.isNotEmpty() && query.isNotEmpty()
    val borderColor     = if (showSuggestions) theme.accentPrimary.copy(alpha = 0.5f)
    else theme.glassBorder

    Column(modifier = modifier.fillMaxWidth()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(theme.glassBg)
                .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector        = Icons.Rounded.Search,
                contentDescription = "Search",
                tint               = theme.textSecondary.copy(alpha = 0.5f),
                modifier           = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))

            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        "Search cities, regions…",
                        color    = theme.textSecondary.copy(alpha = 0.45f),
                        fontSize = 15.sp
                    )
                }
                BasicTextField(
                    value         = query,
                    onValueChange = onQueryChange,
                    textStyle     = TextStyle(color = theme.textPrimary, fontSize = 15.sp),
                    cursorBrush   = SolidColor(theme.accentPrimary),
                    modifier      = Modifier.fillMaxWidth()
                )
            }

            if (query.isNotEmpty()) {
                Icon(
                    imageVector        = Icons.Rounded.Clear,
                    contentDescription = "Clear",
                    tint               = theme.textSecondary.copy(alpha = 0.5f),
                    modifier           = Modifier
                        .size(18.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication        = null,
                            onClick           = onClearClick
                        )
                )
            }
        }

        AnimatedVisibility(
            visible = showSuggestions,
            enter   = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
            exit    = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(theme.glassBg)
                    .border(1.dp, theme.accentPrimary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            ) {
                suggestions.forEachIndexed { index, suggestion ->
                    val parts   = suggestion.split(",")
                    val city    = parts.getOrNull(0)?.trim() ?: ""
                    val country = parts.getOrNull(1)?.trim() ?: ""

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication        = null
                            ) { onSuggestionClick(suggestion) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector        = Icons.Rounded.LocationOn,
                            contentDescription = null,
                            tint               = theme.accentPrimary,
                            modifier           = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(city,    color = theme.textPrimary,                       fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            if (country.isNotEmpty())
                                Text(country, color = theme.textSecondary.copy(alpha = 0.45f), fontSize = 11.sp)
                        }
                    }

                    if (index < suggestions.lastIndex) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(theme.glassBorder.copy(alpha = 0.5f))
                        )
                    }
                }
            }
        }
    }
}