package com.darkzoom.tempsphere.ui.places.view

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkzoom.tempsphere.data.local.model.SearchResult
import com.darkzoom.tempsphere.ui.core.Theme.AppThemeColors
import com.darkzoom.tempsphere.ui.core.Theme.LocalAppTheme
import com.darkzoom.tempsphere.ui.places.components.LocationCard
import com.darkzoom.tempsphere.ui.places.components.LocationSearchBar
import com.darkzoom.tempsphere.ui.places.viewmodel.PlacesUiState
import com.darkzoom.tempsphere.ui.places.viewmodel.PlacesViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@Composable
fun PlacesView(
    viewModel:          PlacesViewModel,
    onLocationClick:    (Int) -> Unit,
    onAddLocationClick: () -> Unit
) {
    val theme   = LocalAppTheme.current
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {

        when (val state = uiState) {
            is PlacesUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = theme.accentPrimary)
                }
            }
            is PlacesUiState.Empty -> EmptyContent(theme)
            is PlacesUiState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(state.message, color = theme.textSecondary, fontSize = 14.sp)
                        TextButton(onClick = { viewModel.refreshAll() }) {
                            Text("Retry", color = theme.accentPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
            is PlacesUiState.Success -> {
                SuccessContent(
                    state           = state,
                    viewModel       = viewModel,
                    onLocationClick = onLocationClick,
                    theme           = theme
                )
            }
        }

        FloatingActionButton(
            onClick        = onAddLocationClick,
            modifier       = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 20.dp, bottom = 20.dp)
                .shadow(16.dp, CircleShape, spotColor = theme.accentPrimary),
            containerColor = Color.Transparent,
            contentColor   = Color.White,
            shape          = CircleShape,
            elevation      = FloatingActionButtonDefaults.elevation(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(theme.accentPrimary, theme.accentSecondary)))
                    .border(1.dp, theme.accentPrimary.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Add, "Add place", tint = Color.White, modifier = Modifier.size(24.dp))
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SuccessContent(
    state:           PlacesUiState.Success,
    viewModel: PlacesViewModel,
    onLocationClick: (Int) -> Unit,
    theme:           AppThemeColors
) {
    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh    = { viewModel.refreshAll() },
        modifier     = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier       = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Text(
                        "My Places",
                        color         = theme.textPrimary,
                        fontSize      = 26.sp,
                        fontWeight    = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )
                    val count = state.savedLocations.size
                    Text(
                        "$count location${if (count != 1) "s" else ""} saved",
                        color    = theme.textSecondary.copy(alpha = 0.6f),
                        fontSize = 13.sp
                    )
                }
            }

            item {
                LocationSearchBar(
                    query             = state.searchQuery,
                    onQueryChange     = viewModel::onSearchQueryChange,
                    onClearClick      = viewModel::clearSearch,
                    suggestions       = state.suggestions,
                    onSuggestionClick = { viewModel.clearSearch() },
                    modifier          = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                )
            }

            items(items = state.savedLocations, key = { it.id }) { location ->
                SwipeToDeleteCard(onDelete = { viewModel.removeFavourite(location.id) }) {
                    LocationCard(
                        location = location,
                        onClick  = { onLocationClick(location.id) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}


@Composable
private fun EmptyContent(theme: AppThemeColors) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier            = Modifier.padding(horizontal = 40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(theme.accentPrimary.copy(alpha = 0.12f))
                    .border(1.dp, theme.accentPrimary.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.LocationOn, null, tint = theme.accentPrimary, modifier = Modifier.size(32.dp))
            }
            Text("No saved places yet", color = theme.textPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Text(
                "Tap + to add your favourite cities",
                color     = theme.textSecondary.copy(alpha = 0.55f),
                fontSize  = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
private fun SwipeToDeleteCard(
    onDelete: () -> Unit,
    content:  @Composable () -> Unit
) {
    val scope        = rememberCoroutineScope()
    val offsetX      = remember { Animatable(0f) }
    var itemWidthPx  by remember { mutableIntStateOf(0) }

    val fraction = if (itemWidthPx > 0)
        (-offsetX.value / itemWidthPx).coerceIn(0f, 1f)
    else 0f

    val threshold = itemWidthPx * 0.40f
    val isPast    = offsetX.value < -threshold

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onSizeChanged { itemWidthPx = it.width }
    ) {


        if (fraction > 0f) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        if (isPast) Color(0xFFDC2626)
                        else Color(0xFF991B1B)
                    ),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector        = Icons.Rounded.Delete,
                    contentDescription = "Delete",
                    tint               = Color.White,
                    modifier           = Modifier
                        .padding(end = 26.dp)
                        .scale(if (isPast) 1.18f else 1f)
                        .size(24.dp)
                )
            }
        }


        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .pointerInput(itemWidthPx) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                if (offsetX.value < -threshold && itemWidthPx > 0) {
                                    offsetX.animateTo(
                                        targetValue   = -itemWidthPx.toFloat(),
                                        animationSpec = tween(durationMillis = 250)
                                    )
                                    onDelete()
                                } else {
                                    offsetX.animateTo(
                                        targetValue   = 0f,
                                        animationSpec = tween(durationMillis = 300)
                                    )
                                }
                            }
                        },
                        onDragCancel = {
                            scope.launch {
                                offsetX.animateTo(0f, tween(300))
                            }
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            scope.launch {
                                val newValue = offsetX.value + dragAmount

                                offsetX.snapTo(
                                    newValue.coerceIn(
                                        minimumValue = if (itemWidthPx > 0) -itemWidthPx.toFloat() else -Float.MAX_VALUE,
                                        maximumValue = 0f
                                    )
                                )
                            }
                        }
                    )
                }
        ) {
            content()
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