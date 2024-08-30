package com.teamwss.websoso.ui.novelDetail

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseDialogFragment
import com.teamwss.websoso.common.util.getAdaptedParcelable
import com.teamwss.websoso.databinding.DialogNovelAlertBinding
import com.teamwss.websoso.ui.novelDetail.model.NovelAlertModel

class NovelAlertDialogFragment : BaseDialogFragment<DialogNovelAlertBinding>(R.layout.dialog_novel_alert) {

    private lateinit var novelAlertModel: NovelAlertModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAlertModel()
        onAcceptButtonClick()
        onCancelButtonClick()
        setupView()
        updateAlertMessageVisibility()
    }

    private fun setupAlertModel() {
        arguments?.getAdaptedParcelable<NovelAlertModel>(ALERT_MODEL)?.let {
            novelAlertModel = it
        }
    }

    private fun onAcceptButtonClick() {
        binding.onAcceptClick = {
            novelAlertModel.onAcceptClick()
            dismiss()
        }
    }

    private fun onCancelButtonClick() {
        binding.onCancelClick = { dismiss() }
    }

    private fun setupView() {
        with(binding) {
            tvNovelAlertTitle.text = novelAlertModel.title
            tvNovelAlertMessage.text = novelAlertModel.message
            tvNovelAlertAccept.text = novelAlertModel.acceptButtonText
            tvNovelAlertCancel.text = novelAlertModel.cancelButtonText
        }
    }

    private fun updateAlertMessageVisibility() {
        with(binding.tvNovelAlertMessage) {
            visibility = when (binding.tvNovelAlertMessage.text.isEmpty()) {
                true -> GONE
                false -> VISIBLE
            }
        }
    }

    companion object {
        const val TAG = "NOVEL_ALERT_DIALOG_FRAGMENT"

        private const val ALERT_MODEL = "ALERT_MODEL"

        fun newInstance(
            novelAlertModel: NovelAlertModel,
        ): NovelAlertDialogFragment = NovelAlertDialogFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ALERT_MODEL, novelAlertModel)
            }
        }
    }
}