package com.teamwss.websoso.ui.common.customView

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

    private val binding: LayoutSearchBinding = LayoutSearchBinding.inflate(LayoutInflater.from(context), this, true)

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

    private fun setupWebsosoSearchTextChangedListener() {
        binding.etCommonSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateWebsosoSearchClearVisibility()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateWebsosoSearchClearVisibility() {
        val isVisible = binding.etCommonSearch.text?.isNotEmpty() == true
        binding.ivCommonSearchClear.visibility = if (isVisible) VISIBLE else GONE
    }

    private fun setupWebsosoSearchClearClickListener() {
        binding.ivCommonSearchClear.setOnClickListener {
            externalClearClickListener?.onClick(it)
            binding.etCommonSearch.text?.clear()
            clearWebsosoSearchFocus()
        }
    }

    fun clearWebsosoSearchFocus() {
        if (binding.etCommonSearch.hasFocus()) {
            binding.etCommonSearch.clearFocus()
        } else {
            externalFocusChangeListener?.onFocusChange(binding.etCommonSearch, false)
            updateSearchBackground(binding.etCommonSearch.text, false)
        }
    }

    private fun setupWebsosoSearchFocusChangeListener() {
        binding.etCommonSearch.setOnFocusChangeListener { view, isFocused ->
            val editText = view as EditText
            if (!isFocused) hideKeyboard()
            externalFocusChangeListener?.onFocusChange(editText, isFocused)
            updateSearchBackground(editText.text, isFocused)
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.etCommonSearch.windowToken, 0)
    }

    private fun updateSearchBackground(input: CharSequence?, isFocused: Boolean) {
        when (isFocused) {
            true -> this.setBackgroundResource(R.drawable.bg_novel_rating_keyword_white_radius_14dp)

            false -> this.setBackgroundResource(R.drawable.bg_novel_rating_gray_50_radius_14dp)
        }
    }

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

    fun setOnWebsosoSearchFocusChangeListener(
        onWebsosoSearchFocusChangeListener: OnFocusChangeListener,
    ) {
        externalFocusChangeListener = onWebsosoSearchFocusChangeListener
    }

    fun setOnWebsosoSearchActionListener(
        onWebsosoSearchActionListener: TextView.OnEditorActionListener,
    ) {
        externalActionListener = onWebsosoSearchActionListener
    }

    fun setOnWebsosoSearchClearClickListener(
        onWebsosoSearchClearClickListener: OnClickListener,
    ) {
        externalClearClickListener = onWebsosoSearchClearClickListener
    }

    fun setWebsosoSearchText(text: String) {
        binding.etCommonSearch.setText(text)
    }

    fun setWebsosoSearchHint(hint: String) {
        binding.etCommonSearch.hint = hint
    }

    fun getWebsosoSearchText(): String {
        return binding.etCommonSearch.text.toString()
    }

    fun getIsWebsosoSearchFocused(): Boolean {
        return binding.etCommonSearch.hasFocus()
    }
}
