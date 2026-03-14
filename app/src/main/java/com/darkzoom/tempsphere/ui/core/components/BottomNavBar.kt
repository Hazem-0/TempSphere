package com.darkzoom.tempsphere.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.darkzoom.tempsphere.ui.core.components.Screen
import com.darkzoom.tempsphere.ui.core.Theme.LocalAppTheme

@Composable
fun BottomNavBar(navController: NavController) {
    val theme = LocalAppTheme.current
    val screens = listOf(
        Screen.Home,
        Screen.Alerts,
        Screen.Settings
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xD106021A))
            .border(
                width = 1.dp,
                color = theme.glassBorder
            )
            .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            screens.forEach { screen ->
                val isActive = currentRoute == screen.route

                CustomNavItem(
                    screen = screen,
                    isActive = isActive,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CustomNavItem(
    screen: Screen,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val theme = LocalAppTheme.current

    val activeColor = theme.accentPrimary
    val inactiveColor = theme.textSecondary.copy(alpha = 0.5f)
    val textColor = if (isActive) activeColor else inactiveColor

    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .background(
                if (isActive) Brush.linearGradient(
                    colors = listOf(
                        theme.accentPrimary.copy(alpha = 0.22f),
                        theme.accentSecondary.copy(alpha = 0.13f)
                    )
                ) else Brush.linearGradient(
                    colors = listOf(Color.Transparent, Color.Transparent)
                )
            )
            .border(
                width = 1.dp,
                color = if (isActive) theme.accentPrimary.copy(alpha = 0.28f) else Color.Transparent,
                shape = RoundedCornerShape(18.dp)
            )
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .defaultMinSize(minWidth = 58.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(
            imageVector = screen.icon,
            contentDescription = screen.title,
            tint = if (isActive) activeColor else inactiveColor,
            modifier = Modifier.size(22.dp)
        )

        Text(
            text = screen.title,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
            letterSpacing = 0.2.sp
        )
    }
}