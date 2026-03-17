package com.darkzoom.tempsphere.ui.places.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.darkzoom.tempsphere.R
import com.darkzoom.tempsphere.ui.core.Theme.AppThemeColors

@Composable
 fun BoxScope.MyLocationFab(theme: AppThemeColors, onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .navigationBarsPadding()
            .padding(end = 16.dp, bottom = 160.dp)
            .size(44.dp)
            .border(1.dp, theme.accentPrimary.copy(alpha = 0.35f), CircleShape),
        shape = CircleShape,
        containerColor = androidx.compose.ui.graphics.Color(0xE806021A),
        contentColor = theme.accentPrimary,
        elevation = FloatingActionButtonDefaults.elevation(4.dp)
    ) {
        Icon(Icons.Rounded.MyLocation,
            stringResource(R.string.my_location), modifier = Modifier.size(20.dp))
    }
}