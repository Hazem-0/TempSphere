package com.darkzoom.tempsphere.ui.places.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkzoom.tempsphere.data.local.model.SearchResult
import com.darkzoom.tempsphere.ui.core.Theme.AppThemeColors


@Composable
 fun SearchOverlay(
    query: String,
    onQueryChange: (String) -> Unit,
    isSearching: Boolean,
    results: List<SearchResult>,
    noResults: String?,
    theme: AppThemeColors,
    onClose: () -> Unit,
    onClear: () -> Unit,
    onResultClick: (SearchResult) -> Unit
) {
    val keyboard = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(androidx.compose.ui.graphics.Color(0xE506021A))
                    .border(1.dp, theme.glassBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = onClose) {
                    Icon(
                        Icons.Rounded.Close,
                        contentDescription = "Close",
                        tint = theme.textPrimary.copy(alpha = 0.85f)
                    )
                }
            }

            SearchTextField(
                query = query,
                onQueryChange = onQueryChange,
                isSearching = isSearching,
                hasResults = results.isNotEmpty(),
                theme = theme,
                onClear = onClear,
                onSearch = { keyboard?.hide() },
                modifier = Modifier.weight(1f)
            )
        }

        AnimatedVisibility(
            visible = results.isNotEmpty() || (!isSearching && noResults != null),
            enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
            exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
        ) {
            SearchResultsDropdown(
                results = results,
                noResults = noResults,
                theme = theme,
                onResultClick = onResultClick
            )
        }
    }
}

@Composable
 fun SearchTextField(
    query: String,
    onQueryChange: (String) -> Unit,
    isSearching: Boolean,
    hasResults: Boolean,
    theme: AppThemeColors,
    onClear: () -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                "Search city, region…",
                color = theme.textSecondary.copy(alpha = 0.5f),
                fontSize = 15.sp
            )
        },
        leadingIcon = {
            Icon(
                Icons.Rounded.Search,
                contentDescription = null,
                tint = theme.accentPrimary.copy(alpha = 0.8f),
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            AnimatedContent(
                targetState = when {
                    isSearching -> "loading"
                    query.isNotEmpty() -> "clear"
                    else -> "none"
                },
                label = "trailing"
            ) { s ->
                when (s) {
                    "loading" -> CircularProgressIndicator(
                        modifier = Modifier.size(18.dp).padding(2.dp),
                        color = theme.accentPrimary,
                        strokeWidth = 2.dp
                    )
                    "clear" -> IconButton(onClick = onClear) {
                        Icon(
                            Icons.Rounded.Close,
                            contentDescription = "Clear",
                            tint = theme.textSecondary.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    else -> {}
                }
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = androidx.compose.ui.graphics.Color(0xEE06021A),
            unfocusedContainerColor = androidx.compose.ui.graphics.Color(0xCC06021A),
            focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
            unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
            focusedTextColor = theme.textPrimary,
            unfocusedTextColor = theme.textPrimary,
            cursorColor = theme.accentPrimary
        ),
        shape = RoundedCornerShape(14.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
        modifier = modifier
            .height(52.dp)
            .border(
                1.dp,
                if (hasResults || query.isNotEmpty()) theme.accentPrimary.copy(alpha = 0.35f) else theme.glassBorder,
                RoundedCornerShape(14.dp)
            )
    )
}


@Composable
 fun SearchResultsDropdown(
    results: List<SearchResult>,
    noResults: String?,
    theme: AppThemeColors,
    onResultClick: (SearchResult) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp, start = 58.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.ui.graphics.Color(0xF2060218)
        ),
        border = BorderStroke(1.dp, theme.accentPrimary.copy(alpha = 0.22f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        if (results.isEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Rounded.Search, null, tint = theme.textSecondary.copy(alpha = 0.35f), modifier = Modifier.size(16.dp))
                Text(noResults ?: "", color = theme.textSecondary.copy(alpha = 0.5f), fontSize = 13.sp)
            }
        } else {
            LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                itemsIndexed(results) { index, result ->
                    ResultRow(result, theme) {
                        onResultClick(result)
                    }
                    if (index < results.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 14.dp),
                            thickness = 0.5.dp,
                            color = theme.glassBorder.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun ResultRow(result: SearchResult, theme: AppThemeColors, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(theme.accentPrimary.copy(alpha = 0.12f))
                .border(1.dp, theme.accentPrimary.copy(alpha = 0.22f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.LocationOn, null, tint = theme.accentPrimary, modifier = Modifier.size(14.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(result.primaryText, color = theme.textPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (result.secondaryText.isNotBlank())
                Text(result.secondaryText, color = theme.textSecondary.copy(alpha = 0.5f), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}