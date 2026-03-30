package com.utc2.appreborn.ui.components

import androidx.compose.animation.animateColor
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
// DATA CLASS (chuẩn)
// =======================
data class NavItem(
    val id: Int,
    val icon: Int
)

// =======================
// Shape liquid
// =======================
fun getLiquidShape(offset: Float, radius: Float) = GenericShape { size, _ ->
    val topY = 0f // Bắt đầu từ mép trên của thanh điều hướng
    val curveWidth = radius * 2.2f // Độ rộng của vùng ảnh hưởng chất lỏng
    val dipDepth = radius * 1.5f // Độ sâu của lỗ lõm xuống

    moveTo(0f, topY)

    // 1. Vẽ đường thẳng đến vùng bắt đầu của hiệu ứng liquid
    lineTo(offset - curveWidth, topY)

    // 2. Vẽ đường cong mượt bên trái (S-curve)
    cubicTo(
        x1 = offset - curveWidth * 0.5f, y1 = topY,
        x2 = offset - radius * 1.2f, y2 = dipDepth,
        x3 = offset, y3 = dipDepth
    )

    // 3. Vẽ đường cong mượt bên phải (S-curve ngược)
    cubicTo(
        x1 = offset + radius * 1.2f, y1 = dipDepth,
        x2 = offset + curveWidth * 0.5f, y2 = topY,
        x3 = offset + curveWidth, y3 = topY
    )

    // 4. Vẽ tiếp sang lề phải và đóng vùng shape
    lineTo(size.width, topY)
    lineTo(size.width, size.height)
    lineTo(0f, size.height)
    close()
}
// =======================
// Bridge cho Java
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
// UI chính
// =======================

@Composable
fun LiquidBottomNavigation(
    onItemSelected: (Int) -> Unit
) {
    val items = remember {
        listOf(
            NavItem(R.id.nav_home, R.drawable.ic_house),
            NavItem(R.id.nav_schedule, R.drawable.ic_calendar),
            NavItem(R.id.nav_register, R.drawable.ic_book_open),
            NavItem(R.id.nav_result, R.drawable.ic_graduation_cap),
            NavItem(R.id.nav_profile, R.drawable.ic_user)
        )
    }

    var selectedIndex by remember { mutableIntStateOf(0) }

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidth = configuration.screenWidthDp.dp
    val itemWidthPx = with(density) { (screenWidth / items.size).toPx() }

    // Animate vị trí ngang của quả bóng và lỗ lõm
    val animX by animateFloatAsState(
        targetValue = selectedIndex * itemWidthPx + itemWidthPx / 2,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow),
        label = "ball_x"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp) // Tăng nhẹ chiều cao để quả bóng có không gian bay
    ) {
        // --- Lớp 1: Nền Liquid màu Đen ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .align(Alignment.BottomCenter)
                .background(Color.Black, shape = getLiquidShape(animX, 75f))
        )

        // --- Lớp 2: Quả bóng duy nhất (The Floating Ball) ---
        Box(
            modifier = Modifier
                .size(55.dp)
                .offset(
                    x = with(density) { (animX).toDp() } - 27.5.dp, // Căn giữa ball
                    y = 5.dp // Vị trí quả bóng bay phía trên
                )
                .background(Color.Black, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Icon trên quả bóng (thay đổi theo selectedIndex)
            // Trigger animation mỗi lần đổi tab
            var trigger by remember { mutableStateOf(false) }

            LaunchedEffect(selectedIndex) {
                trigger = false
                trigger = true
            }

// Transition animation
            val transition = updateTransition(targetState = trigger, label = "icon_anim")

// Scale: nhỏ -> to
            val iconScale by transition.animateFloat(
                transitionSpec = {
                    tween(durationMillis = 400)
                },
                label = "scale"
            ) { state ->
                if (state) 1.2f else 0.6f
            }

// Color: trắng -> vàng
            val iconColor by transition.animateColor(
                transitionSpec = {
                    tween(durationMillis = 400)
                },
                label = "color"
            ) { state ->
                if (state) Color.Yellow else Color.White
            }

// Icon (đã animate)
            Icon(
                painter = painterResource(id = items[selectedIndex].icon),
                contentDescription = null,
                modifier = Modifier
                    .size(26.dp)
                    .graphicsLayer {
                        scaleX = iconScale
                        scaleY = iconScale
                    },
                tint = iconColor
            )
        }

        // --- Lớp 3: Các Icon bên dưới (Ẩn khi được chọn) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .align(Alignment.BottomCenter)
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = selectedIndex == index

                // Hiệu ứng ẩn/hiện và dịch chuyển icon bên dưới
                val iconAlpha by animateFloatAsState(targetValue = if (isSelected) 0f else 1f)
                val iconOffset by animateDpAsState(targetValue = if (isSelected) (-20).dp else 0.dp)

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
                    if (!isSelected) { // Tối ưu: Chỉ vẽ icon nếu nó không bị ẩn hoàn toàn
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .offset(y = iconOffset),
                            tint = Color.White.copy(alpha = iconAlpha)
                        )
                    }
                }
            }
        }
    }
}
