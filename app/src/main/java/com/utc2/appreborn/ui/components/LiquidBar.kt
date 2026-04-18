package com.utc2.appreborn.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.utc2.appreborn.R

// =======================
// DATA CLASS
// =======================
data class NavItem(
    val id: Int,
    val icon: Int
)

// =======================
// SHAPE
// =======================
fun getLiquidShape(offset: Float, radius: Float) = GenericShape { size, _ ->

    val curveWidth = radius * 2.2f
    val dipDepth = radius * 1.5f

    moveTo(0f, 0f)
    lineTo(offset - curveWidth, 0f)

    cubicTo(
        offset - curveWidth * 0.5f, 0f,
        offset - radius * 1.2f, dipDepth,
        offset, dipDepth
    )

    cubicTo(
        offset + radius * 1.2f, dipDepth,
        offset + curveWidth * 0.5f, 0f,
        offset + curveWidth, 0f
    )

    lineTo(size.width, 0f)
    lineTo(size.width, size.height)
    lineTo(0f, size.height)
    close()
}

// =======================
// SETUP
// =======================
fun setupLiquidBottomBar(
    composeView: ComposeView,
    onItemSelected: (Int) -> Unit
) {
    composeView.setContent {
        MaterialTheme {
            LiquidBottomNavigation(onItemSelected)
        }
    }
}

// =======================
// MAIN UI
// =======================
@Composable
fun LiquidBottomNavigation(onItemSelected: (Int) -> Unit) {

    // 🔥 DÙNG ICON HỆ THỐNG → KHÔNG BAO GIỜ CRASH
    val items = listOf(
        NavItem(R.id.nav_home, R.drawable.ic_house),
        NavItem(R.id.nav_schedule, R.drawable.ic_calendar),
        NavItem(R.id.nav_register, R.drawable.ic_book_open),
        NavItem(R.id.nav_result, R.drawable.ic_graduation_cap),
        NavItem(R.id.nav_profile, R.drawable.ic_user)
    )

    var selectedIndex by remember { mutableIntStateOf(0) }

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val density = LocalDensity.current
    val itemWidthPx = with(density) { (screenWidth / items.size).toPx() }

    val animX by animateFloatAsState(
        targetValue = selectedIndex * itemWidthPx + itemWidthPx / 2,
        animationSpec = spring(
            dampingRatio = 0.7f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {

        // nền
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .align(Alignment.BottomCenter)
                .background(
                    color = Color.Black,
                    shape = getLiquidShape(animX, 75f)
                )
        )

        // nút tròn
        Box(
            modifier = Modifier
                .size(55.dp)
                .offset(
                    x = with(density) { animX.toDp() } - 27.5.dp,
                    y = 5.dp
                )
                .background(Color.Black, CircleShape),
            contentAlignment = Alignment.Center
        ) {

            val transition = updateTransition(selectedIndex, label = "")

            val scale by transition.animateFloat(
                transitionSpec = { tween(300) },
                label = ""
            ) { 1.2f }

            Icon(
                painter = painterResource(id = items[selectedIndex].icon),
                contentDescription = null,
                tint = Color.Yellow,
                modifier = Modifier
                    .size(26.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
            )
        }

        // menu
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .align(Alignment.BottomCenter)
        ) {

            items.forEachIndexed { index, item ->

                val isSelected = selectedIndex == index

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            selectedIndex = index
                            onItemSelected(item.id)
                        },
                    contentAlignment = Alignment.Center
                ) {

                    if (!isSelected) {
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}