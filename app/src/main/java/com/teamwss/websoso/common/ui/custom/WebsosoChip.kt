package com.teamwss.websoso.common.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.chip.Chip
import com.teamwss.websoso.common.util.toFloatPxFromDp

class WebsosoChip(
    context: Context,
    attrs: AttributeSet? = null,
) : Chip(context, attrs) {
    init {
        setLayoutParams()
        rippleColor = null
    }

    private fun setLayoutParams() {
        layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
    }

    /*
    Sets this chip's text.
    Params:
    name - The String of this chip's text.
     */
    fun setWebsosoChipText(name: String) {
        text = name
    }

    /*
    Sets this chip's text appearance using a resource id.
    Params:
    style - The resource id of this chip's text style.
     */
    fun setWebsosoChipTextAppearance(style: Int) {
        setTextAppearance(style)
    }

    /*
    Sets this chip's text color using a resource id.
    Params:
    color - The resource id of this chip's text color.
     */
    fun setWebsosoChipTextColor(color: Int) {
        setTextColor(
            context.getColorStateList(color),
        )
    }

    /*
    Sets this chip's background color using a resource id.
    Params:
    color - The resource id of this chip's background color.
     */
    fun setWebsosoChipBackgroundColor(color: Int) {
        chipBackgroundColor = context.getColorStateList(color)
    }

    /*
    Sets this chip's stroke color using a resource id.
    It has a default value of stroke's width set 1f.
    Params:
    color - The resource id of this chip's stroke color.
     */
    fun setWebsosoChipStrokeColor(color: Int) {
        chipStrokeWidth = 1f.toFloatPxFromDp()
        chipStrokeColor = context.getColorStateList(color)
    }

    /*
    Sets this chip's radius size.
    Params:
    radius - The value of this chip's radius size.
     */
    fun setWebsosoChipRadius(radius: Float) {
        shapeAppearanceModel =
            shapeAppearanceModel.toBuilder()
                .setAllCornerSizes(radius)
                .build()
    }

    /*
    Sets this chip's vertical padding size. There was no default method to set vertical padding.
    So we set the minimum height of chip using 'chipMinHeight' by doubling padding value of params
    and adding the height of the text inside the chip.
    Params:
    padding - The value of this chip's vertical padding.
     */
    fun setWebsosoChipPaddingVertical(padding: Float) {
        chipMinHeight = (padding * 2) + textSize
    }

    /*
    Sets this chip's horizontal padding size.
    Params:
    padding - The value of this chip's vertical padding.
     */
    fun setWebsosoChipPaddingHorizontal(padding: Float) {
        chipStartPadding = padding
        chipEndPadding = padding
    }

    /*
   Sets this chip's selected.
   Params:
   isSelected - The state of this chip's selected.
    */
    fun setWebsosoChipSelected(isSelected: Boolean) {
        this.isSelected = isSelected
    }

    /*
    Sets this chip's Click Listener. Its behavior depends on whether 'isSingleSelectionMode' of
    WebsosoChipGroup is true or false. If 'app:singleSelection' is true, it enables single selection mode.
    Single Selection Mode - Can only select a chip at a time. If the currently selected chip is clicked,
    nothing will happen. otherwise, it toggles its selected state and executes the event passed as a params.
    Default - If the chip is clicked, it toggles its selected state and executes the event passed as a params.
    Params:
    event - The trigger an action declared externally.
     */
    fun setOnWebsosoChipClick(event: () -> Unit) {
        setOnClickListener {
            if ((parent as WebsosoChipGroup).isSingleSelectionMode) {
                eventOnSingleSelectionMode(event)
                return@setOnClickListener
            }

            setWebsosoChipSelected(!isSelected)
            event()
        }
    }

    /*
    Sets the visibility of the close icon on this chip.
    Params:
    isVisible - Boolean indicating whether the close icon should be visible or not.
     */
    fun setWebsosoChipCloseIconVisibility(isVisible: Boolean) {
        isCloseIconVisible = isVisible
    }

    /*
    Sets the drawable for the close icon on this chip using a resource id.
    Params:
    drawable - The resource id of the drawable to be used for the close icon.
     */
    fun setWebsosoChipCloseIconDrawable(drawable: Int) {
        closeIcon = AppCompatResources.getDrawable(context, drawable)
    }

    /*
    Sets the size of the close icon on this chip.
    Params:
    size - The value of the close icon size in pixels.
     */
    fun setWebsosoChipCloseIconSize(size: Float) {
        closeIconSize = size
    }

    /*
    Sets the end padding of the close icon on this chip.
    Params:
    padding - The value of the close icon end padding in pixels.
     */
    fun setWebsosoChipCloseIconEndPadding(padding: Float) {
        closeIconEndPadding = padding
    }

    private fun eventOnSingleSelectionMode(event: () -> Unit) {
        (parent as WebsosoChipGroup).let { websosoChipGroup ->
            when {
                websosoChipGroup.previousChip == null -> {
                    setWebsosoChipSelected(true)
                    websosoChipGroup.updateSelectedChip(this)
                }

                websosoChipGroup.previousChip != this -> {
                    setWebsosoChipSelected(true)
                    websosoChipGroup.removePreviousChipSelected()
                    websosoChipGroup.updateSelectedChip(this)
                }

                websosoChipGroup.previousChip == this -> return
            }
        }

        event()
    }
}
