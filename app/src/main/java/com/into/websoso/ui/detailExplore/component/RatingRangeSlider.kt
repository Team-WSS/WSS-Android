package com.into.websoso.ui.detailExplore.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.Primary50
import com.into.websoso.core.designsystem.theme.White
import kotlin.math.absoluteValue
import kotlin.math.round

@Composable
fun RatingRangeSlider(
    min: Float,
    max: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    stepSize: Float,
    onValueChange: (Float, Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val totalRange = valueRange.endInclusive - valueRange.start
    val tickCount = ((totalRange / stepSize).toInt() + 1).coerceAtLeast(2)

    val density = LocalDensity.current
    val thumbRadiusPx = with(density) { 8.dp.toPx() }

    var sliderWidthPx by remember { mutableFloatStateOf(0f) }
    var activeThumb by remember { mutableIntStateOf(0) }

    val trackWidthPx = (sliderWidthPx - thumbRadiusPx * 2).coerceAtLeast(0f)
    val startFraction = ((min - valueRange.start) / totalRange).coerceIn(0f, 1f)
    val endFraction = ((max - valueRange.start) / totalRange).coerceIn(0f, 1f)
    val startCenterPx = thumbRadiusPx + trackWidthPx * startFraction
    val endCenterPx = thumbRadiusPx + trackWidthPx * endFraction

    val latestMin by rememberUpdatedState(min)
    val latestMax by rememberUpdatedState(max)
    val latestStartCenter by rememberUpdatedState(startCenterPx)
    val latestEndCenter by rememberUpdatedState(endCenterPx)

    fun snap(rawValue: Float): Float {
        val stepped = round(rawValue / stepSize) * stepSize
        return stepped.coerceIn(valueRange.start, valueRange.endInclusive)
    }

    fun valueAtX(x: Float): Float {
        if (trackWidthPx <= 0f) return valueRange.start
        val fraction = ((x - thumbRadiusPx) / trackWidthPx).coerceIn(0f, 1f)
        return snap(valueRange.start + fraction * totalRange)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(16.dp)
            .onSizeChanged { sliderWidthPx = it.width.toFloat() }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val touchX = offset.x
                        activeThumb = if ((touchX - latestStartCenter).absoluteValue <=
                            (touchX - latestEndCenter).absoluteValue
                        ) 1 else 2
                        val newValue = valueAtX(touchX)
                        if (activeThumb == 1) {
                            onValueChange(newValue.coerceAtMost(latestMax), latestMax)
                        } else {
                            onValueChange(latestMin, newValue.coerceAtLeast(latestMin))
                        }
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        val newValue = valueAtX(change.position.x)
                        if (activeThumb == 1) {
                            onValueChange(newValue.coerceAtMost(latestMax), latestMax)
                        } else {
                            onValueChange(latestMin, newValue.coerceAtLeast(latestMin))
                        }
                    },
                )
            },
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Primary50),
        )
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
                .height(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(tickCount) {
                Box(
                    modifier = Modifier
                        .size(width = 1.dp, height = 2.dp)
                        .background(Primary100, RoundedCornerShape(1.dp)),
                )
            }
        }
        val activeStartDp = with(density) { startCenterPx.toDp() }
        val activeWidthDp = with(density) { (endCenterPx - startCenterPx).coerceAtLeast(0f).toDp() }
        Box(
            modifier = Modifier
                .offset(x = activeStartDp)
                .width(activeWidthDp)
                .height(4.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Primary100),
        )
        ThumbCircle(offsetXDp = with(density) { (startCenterPx - thumbRadiusPx).toDp() })
        ThumbCircle(offsetXDp = with(density) { (endCenterPx - thumbRadiusPx).toDp() })
    }
}

@Composable
private fun ThumbCircle(offsetXDp: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .offset(x = offsetXDp)
            .shadow(elevation = 2.dp, shape = CircleShape, clip = false)
            .size(16.dp)
            .background(White, CircleShape),
    )
}