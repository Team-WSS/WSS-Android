package com.teamwss.websoso.common.ui.custom

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.LayoutSearchBinding

class WebsosoSearchEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: LayoutSearchBinding =
        LayoutSearchBinding.inflate(LayoutInflater.from(context), this, true)

    private var externalFocusChangeListener: OnFocusChangeListener? = null
    private var externalActionListener: TextView.OnEditorActionListener? = null
    private var externalClearClickListener: OnClickListener? = null

    init {
        setupWebsosoSearchTextChangedListener()
        setupWebsosoSearchClearClickListener()
        setupWebsosoSearchFocusChangeListener()
        setupWebsosoSearchActionListener()
        updateSearchBackground(null, false)
    }

    /* Sets up a listener to detect changes in the text input */
    private fun setupWebsosoSearchTextChangedListener() {
        binding.etCommonSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateWebsosoSearchClearVisibility()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    /* Updates the visibility of the clear button based on whether there is text in the input */
    private fun updateWebsosoSearchClearVisibility() {
        val isVisible = binding.etCommonSearch.text?.isNotEmpty() == true
        binding.ivCommonSearchClear.visibility = if (isVisible) VISIBLE else GONE
    }

    /* Sets up a click listener for the clear button, clearing the text input and focus */
    private fun setupWebsosoSearchClearClickListener() {
        binding.ivCommonSearchClear.setOnClickListener {
            externalClearClickListener?.onClick(it)
            binding.etCommonSearch.text?.clear()
            clearWebsosoSearchFocus()
        }
    }

    /* Clears the focus from the search input field */
    fun clearWebsosoSearchFocus() {
        if (binding.etCommonSearch.hasFocus()) {
            binding.etCommonSearch.clearFocus()
        } else {
            externalFocusChangeListener?.onFocusChange(binding.etCommonSearch, false)
            updateSearchBackground(binding.etCommonSearch.text, false)
        }
    }

    /* Sets up a focus change listener for the search input field */
    private fun setupWebsosoSearchFocusChangeListener() {
        binding.etCommonSearch.setOnFocusChangeListener { view, isFocused ->
            val editText = view as EditText
            if (!isFocused) hideKeyboard()
            externalFocusChangeListener?.onFocusChange(editText, isFocused)
            updateSearchBackground(editText.text, isFocused)
        }
    }

    /* Hides the soft keyboard */
    private fun hideKeyboard() {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.etCommonSearch.windowToken, 0)
    }

    /* Updates the background of the search input field based on its focus state */
    private fun updateSearchBackground(input: CharSequence?, isFocused: Boolean) {
        when (isFocused) {
            true -> this.setBackgroundResource(R.drawable.bg_novel_rating_keyword_white_radius_14dp)

            false -> this.setBackgroundResource(R.drawable.bg_novel_rating_gray_50_radius_14dp)
        }
    }

    /* Sets up an action listener for the search input field and search button */
    private fun setupWebsosoSearchActionListener() {
        binding.etCommonSearch.setOnEditorActionListener { view, actionId, event ->
            externalActionListener?.onEditorAction(view, actionId, event)
            view.clearFocus()
            true
        }
        binding.ivCommonSearch.setOnClickListener {
            externalActionListener?.onEditorAction(binding.etCommonSearch, 0, null)
            clearWebsosoSearchFocus()
        }
    }

    /* Sets a custom focus change listener for the search input field */
    fun setOnWebsosoSearchFocusChangeListener(
        onWebsosoSearchFocusChangeListener: OnFocusChangeListener,
    ) {
        externalFocusChangeListener = onWebsosoSearchFocusChangeListener
    }

    /* Sets a custom action listener for the search input field */
    fun setOnWebsosoSearchActionListener(
        onWebsosoSearchActionListener: TextView.OnEditorActionListener,
    ) {
        externalActionListener = onWebsosoSearchActionListener
    }

    /* Sets a custom click listener for the clear button */
    fun setOnWebsosoSearchClearClickListener(
        onWebsosoSearchClearClickListener: OnClickListener,
    ) {
        externalClearClickListener = onWebsosoSearchClearClickListener
    }

    /* Sets the color of the hint text */
    fun setWebsosoSearchHintTextColor(color: Int) {
        binding.etCommonSearch.setHintTextColor(color)
    }

    /* Sets the appearance of the text */
    fun setWebsosoSearchTextAppearance(style: Int) {
        binding.etCommonSearch.setTextAppearance(style)
    }

    /* Sets the hint in the search input field */
    fun setWebsosoSearchHint(hint: String) {
        binding.etCommonSearch.hint = hint
    }

    /* Retrieves the text from the search input field */
    fun getWebsosoSearchText(): String {
        return binding.etCommonSearch.text.toString()
    }

    /* Checks if the search input field is focused */
    fun getIsWebsosoSearchFocused(): Boolean {
        return binding.etCommonSearch.hasFocus()
    }
}
