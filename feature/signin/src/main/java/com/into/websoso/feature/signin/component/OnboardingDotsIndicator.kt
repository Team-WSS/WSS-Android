package com.into.websoso.feature.signin.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.into.websoso.core.designsystem.theme.Gray100
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import kotlin.math.absoluteValue

@Composable
internal fun OnboardingDotsIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    ShiftDotsIndicator(
        pagerState = pagerState,
        dotSpacing = 6.dp,
        dotSize = 8.dp,
        dotShape = RoundedCornerShape(size = 6.dp),
        dotColors = DotColors(
            selectedColor = Primary100,
            unselectedColor = Gray100,
        ),
        indicatorWidth = 16.dp,
        indicatorShape = RoundedCornerShape(size = 18.dp),
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
private fun DotsIndicatorPreview() {
    WebsosoTheme {
        val pagerState = rememberPagerState { 4 }
        OnboardingDotsIndicator(
            pagerState = pagerState,
        )
    }
}

@Composable
private fun ShiftDotsIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    dotSpacing: Dp = Dp.Unspecified,
    dotSize: Dp = Dp.Unspecified,
    dotShape: Shape = CircleShape,
    dotColors: DotColors = DotColors(),
    indicatorWidth: Dp = Dp.Unspecified,
    indicatorShape: Shape = RectangleShape,
) {
    val currentPage = pagerState.currentPage
    val offsetFraction = pagerState.currentPageOffsetFraction

    val dotStates = remember(currentPage, offsetFraction) {
        List(pagerState.pageCount) { index ->
            val distance = (index - currentPage) - offsetFraction
            val clamped = distance.absoluteValue.coerceIn(0f, 1f)
            val isSelected = clamped < 0.5f

            DotState(
                width = lerp(indicatorWidth, dotSize, clamped),
                color = lerp(dotColors.selectedColor, dotColors.unselectedColor, clamped),
                isSelected = isSelected,
            )
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dotSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        dotStates.forEach { dotState ->
            val animatedWidth by animateDpAsState(
                targetValue = dotState.width,
                label = "dotWidth",
            )
            val animatedColor by animateColorAsState(
                targetValue = dotState.color,
                label = "dotColor",
            )

            DotStyle(
                height = dotSize,
                width = animatedWidth,
                color = animatedColor,
                shape = if (dotState.isSelected) indicatorShape else dotShape,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ShiftDotsIndicatorPreview() {
    WebsosoTheme {
        val pagerState = rememberPagerState { 4 }
        ShiftDotsIndicator(
            pagerState = pagerState,
            dotSize = 4.dp,
            indicatorWidth = 10.dp,
        )
    }
}

@Composable
private fun DotStyle(
    width: Dp,
    height: Dp,
    color: Color,
    shape: Shape,
) {
    Box(
        modifier = Modifier
            .size(
                height = height,
                width = width,
            )
            .background(
                shape = shape,
                color = color,
            ),
    )
}

@Preview(showBackground = true)
@Composable
private fun DotStylePreview() {
    WebsosoTheme {
        DotStyle(
            height = 8.dp,
            width = 8.dp,
            color = Color.Red,
            shape = CircleShape,
        )
    }
}

private data class DotState(
    val width: Dp,
    val color: Color,
    val isSelected: Boolean,
)

private data class DotColors(
    val selectedColor: Color = Color.White,
    val unselectedColor: Color = Color.Black,
)
