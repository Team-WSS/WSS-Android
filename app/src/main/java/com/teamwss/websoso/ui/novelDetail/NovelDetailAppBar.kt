package com.teamwss.websoso.ui.novelDetail

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.material.appbar.AppBarLayout
import com.teamwss.websoso.R
import kotlin.math.abs
import kotlin.math.min

class NovelDetailAppBar(context: Context, attrs: AttributeSet?) : AppBarLayout(context, attrs) {

    private var navigateBackBtn: ImageView? = null
    private var showMenuBtn: ImageView? = null

    init {
        setupImageViews()
        setupAppBarOnOffListener()
    }

    private fun setupImageViews() {
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                navigateBackBtn = findViewById(R.id.iv_novel_detail_navigate_back)
                showMenuBtn = findViewById(R.id.iv_novel_detail_menu)
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
        val offsetForColorChange =
            TOOLBAR_BUTTON_COLOR_CHANGE_OFFSET * resources.displayMetrics.density
        val scrollPointForColorChange = totalScrollRange - offsetForColorChange

        val currentOffsetRatio =
            calculateCurrentOffsetRatio(verticalOffset, scrollPointForColorChange)
        val currentColor = calculateColorBasedOnScrollPosition(currentOffsetRatio)

        updateImageViewsColor(currentColor)
    }

    private fun calculateCurrentOffsetRatio(
        verticalOffset: Int,
        scrollPointForColorChange: Float
    ): Float {
        val currentOffset = abs(verticalOffset) / scrollPointForColorChange
        return min(MAX_SCROLL_OFFSET, currentOffset)
    }

    private fun calculateColorBasedOnScrollPosition(offsetRatio: Float): Int {
        val colorWhenScrollAtTop = ContextCompat.getColor(this.context, R.color.gray_200_AEADB3)
        val colorWhenScrollAtBottom = ContextCompat.getColor(this.context, R.color.white)

        return if (offsetRatio == MAX_SCROLL_OFFSET) colorWhenScrollAtTop else colorWhenScrollAtBottom
    }

    private fun updateImageViewsColor(color: Int) {
        listOf(navigateBackBtn, showMenuBtn).forEach { imageView ->
            imageView?.drawable?.let { drawable ->
                DrawableCompat.setTint(DrawableCompat.wrap(drawable).mutate(), color)
            }
        }
    }

    companion object {
        private const val TOOLBAR_BUTTON_COLOR_CHANGE_OFFSET = 124
        private const val MAX_SCROLL_OFFSET = 1f
    }

}