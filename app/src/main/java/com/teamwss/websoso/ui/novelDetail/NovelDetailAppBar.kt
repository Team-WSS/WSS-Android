package com.teamwss.websoso.ui.novelDetail

import android.animation.ArgbEvaluator
import android.content.Context
import android.util.AttributeSet
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.AppBarLayout
import com.teamwss.websoso.R
import com.teamwss.websoso.common.util.toFloatPxFromDp
import kotlin.math.abs
import kotlin.math.min

class NovelDetailAppBar(context: Context, attrs: AttributeSet?) : AppBarLayout(context, attrs) {

    private var navigateBackBtn: TextView? = null
    private val argbEvaluator = ArgbEvaluator()

    init {
        setupAppBarTitle()
        setupAppBarOnOffListener()
    }

    private fun setupAppBarTitle() {
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                navigateBackBtn = findViewById(R.id.tv_novel_detail_app_bar_title)
                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun setupAppBarOnOffListener() {
        addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            updateImageViewColorByOffset(appBarLayout, verticalOffset)
        }
    }

    private fun updateImageViewColorByOffset(appBarLayout: AppBarLayout, verticalOffset: Int) {
        val totalScrollRange = appBarLayout.totalScrollRange.toFloat()
        val offsetForColorChange = TOOLBAR_BUTTON_COLOR_CHANGE_OFFSET.toFloatPxFromDp()
        val scrollPointForColorChange = totalScrollRange - offsetForColorChange

        val currentOffsetRatio =
            calculateCurrentOffsetRatio(verticalOffset, scrollPointForColorChange)
        val currentColor = calculateColorBasedOnScrollPosition(currentOffsetRatio)

        updateTextViewColor(currentColor)
    }

    private fun calculateCurrentOffsetRatio(
        verticalOffset: Int,
        scrollPointForColorChange: Float,
    ): Float {
        val currentOffset = abs(verticalOffset) / scrollPointForColorChange

        return if (currentOffset > SCROLL_START_THRESHOLD) {
            min(MAX_SCROLL_OFFSET, (currentOffset - SCROLL_START_THRESHOLD) / (1 - SCROLL_START_THRESHOLD))
        } else 0f
    }

    private fun calculateColorBasedOnScrollPosition(offsetRatio: Float): Int {
        val colorWhenScrollAtTop = ContextCompat.getColor(this.context, R.color.black)
        val colorWhenScrollAtBottom = ContextCompat.getColor(this.context, R.color.transparent)

        return argbEvaluator.evaluate(
            offsetRatio, colorWhenScrollAtBottom, colorWhenScrollAtTop
        ) as Int
    }

    private fun updateTextViewColor(color: Int) {
        navigateBackBtn?.setTextColor(color)
    }

    companion object {
        private const val TOOLBAR_BUTTON_COLOR_CHANGE_OFFSET = 60f
        private const val MAX_SCROLL_OFFSET = 1f
        private const val SCROLL_START_THRESHOLD = 0.95f
    }
}
