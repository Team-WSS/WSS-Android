package com.into.websoso.ui.profileEdit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Patterns
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.forEach
import androidx.core.widget.addTextChangedListener
import com.into.websoso.R.color.bg_profile_edit_chip_background_selector
import com.into.websoso.R.color.bg_profile_edit_chip_stroke_selector
import com.into.websoso.R.color.bg_profile_edit_chip_text_selector
import com.into.websoso.R.color.gray_200_AEADB3
import com.into.websoso.R.color.gray_300_52515F
import com.into.websoso.R.color.primary_100_6A5DFD
import com.into.websoso.R.drawable.bg_profile_edit_gray_50_radius_12dp
import com.into.websoso.R.drawable.bg_profile_edit_gray_70_radius_12dp
import com.into.websoso.R.drawable.bg_profile_edit_primary_50_radius_12dp
import com.into.websoso.R.drawable.bg_profile_edit_white_stroke_gray_70_radius_12dp
import com.into.websoso.R.drawable.bg_profile_edit_white_stroke_primary_100_radius_12dp
import com.into.websoso.R.drawable.bg_profile_edit_white_stroke_secondary_100_radius_12dp
import com.into.websoso.R.layout.activity_profile_edit
import com.into.websoso.R.style.body2
import com.into.websoso.core.common.ui.base.BaseActivity
import com.into.websoso.core.common.ui.custom.WebsosoChip
import com.into.websoso.core.common.ui.model.ResultFrom.ProfileEditSuccess
import com.into.websoso.core.common.util.getS3ImageUrl
import com.into.websoso.core.common.util.showWebsosoToast
import com.into.websoso.core.common.util.toFloatPxFromDp
import com.into.websoso.core.resource.R.drawable.ic_novel_detail_check
import com.into.websoso.core.resource.R.drawable.ic_novel_rating_alert
import com.into.websoso.core.resource.R.string.novel_rating_save_error
import com.into.websoso.core.resource.R.string.profile_edit_introduction_max_count
import com.into.websoso.core.resource.R.string.profile_edit_nickname_max_count
import com.into.websoso.core.resource.R.string.profile_edit_success
import com.into.websoso.databinding.ActivityProfileEditBinding
import com.into.websoso.domain.model.NicknameValidationResult.VALID_NICKNAME
import com.into.websoso.ui.profileEdit.model.Genre
import com.into.websoso.ui.profileEdit.model.Genre.Companion.toGenreFromKr
import com.into.websoso.ui.profileEdit.model.LoadProfileResult
import com.into.websoso.ui.profileEdit.model.ProfileEditResult
import com.into.websoso.ui.profileEdit.model.ProfileEditUiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileEditActivity : BaseActivity<ActivityProfileEditBinding>(activity_profile_edit) {
    private val profileEditViewModel: ProfileEditViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.onAvatarChangeClick = ::showAvatarChangeBottomSheetDialog
        binding.wllProfileEdit.setReloadButtonClickListener {
            profileEditViewModel.updateUserProfile()
        }
        profileEditViewModel.updateUserProfile()

        bindViewModel()
        setupObserver()
        setupGenreChips()
        onNicknameEditTextChange()
        onIntroductionEditTextChange()
        setupProfileIntroductionMaxLines()
    }

    private fun bindViewModel() {
        binding.viewModel = profileEditViewModel
        binding.lifecycleOwner = this
        binding.onNavigateBackClick = { finish() }
    }

    private fun setupObserver() {
        profileEditViewModel.profileEditUiState.observe(this) { uiState ->
            when (uiState.loadProfileResult) {
                LoadProfileResult.Loading -> binding.wllProfileEdit.setWebsosoLoadingVisibility(true)
                LoadProfileResult.Error -> binding.wllProfileEdit.setErrorLayoutVisibility(true)
                LoadProfileResult.Success -> {
                    binding.wllProfileEdit.setWebsosoLoadingVisibility(false)
                    profileEditViewModel.updateCheckDuplicateNicknameButtonEnabled()
                    updateGenreChips(uiState.profile.genrePreferences)
                    updateNicknameEditTextUi(uiState)
                    updateIntroductionEditTextUi(uiState.profile.introduction)
                    updateFinishButtonStatus(uiState.isFinishButtonEnabled)
                    updateDuplicateCheckButtonStatus(uiState.isCheckDuplicateNicknameEnabled)
                    handleProfileEditResult(uiState.profileEditResult)
                    updateAvatarThumbnail(uiState.profile.avatarThumbnail)
                }
            }
        }
    }

    private fun updateGenreChips(genrePreferences: List<Genre>) {
        binding.wcgProfileEditPreferGenre.forEach { view ->
            val chip = view as WebsosoChip
            chip.isSelected = genrePreferences.contains(chip.text.toString().toGenreFromKr())
        }
    }

    private fun updateDuplicateCheckButtonStatus(isEnable: Boolean) {
        binding.tvProfileEditNicknameCheckDuplicate.setTextColor(
            if (isEnable) {
                AppCompatResources
                    .getColorStateList(
                        this,
                        primary_100_6A5DFD,
                    ).defaultColor
            } else {
                AppCompatResources.getColorStateList(this, gray_200_AEADB3).defaultColor
            },
        )
        binding.tvProfileEditNicknameCheckDuplicate.setBackgroundResource(
            if (isEnable) {
                bg_profile_edit_primary_50_radius_12dp
            } else {
                bg_profile_edit_gray_70_radius_12dp
            },
        )
        binding.tvProfileEditNicknameCheckDuplicate.isEnabled = isEnable
    }

    private fun updateNicknameEditTextUi(uiState: ProfileEditUiState) =
        with(binding) {
            tvProfileEditNicknameCount.text = getColoredText(
                getString(
                    profile_edit_nickname_max_count,
                    uiState.profile.nicknameModel.nickname.length,
                ),
                listOf(
                    uiState.profile.nicknameModel.nickname.length
                        .toString(),
                ),
                AppCompatResources
                    .getColorStateList(
                        this@ProfileEditActivity,
                        gray_300_52515F,
                    ).defaultColor,
            )
            tvProfileEditNickname.isSelected = uiState.profile.nicknameModel.nickname
                .isNotEmpty()
            tvProfileEditNicknameResult.isSelected = uiState.nicknameEditResult == VALID_NICKNAME
            tvProfileEditNicknameResult.text = uiState.nicknameEditResult.profileEditMessage

            when {
                uiState.defaultState -> viewProfileEditNickname.setBackgroundResource(
                    bg_profile_edit_white_stroke_secondary_100_radius_12dp,
                )

                uiState.profile.nicknameModel.hasFocus -> viewProfileEditNickname.setBackgroundResource(
                    bg_profile_edit_white_stroke_gray_70_radius_12dp,
                )

                uiState.nicknameEditResult == VALID_NICKNAME -> viewProfileEditNickname.setBackgroundResource(
                    bg_profile_edit_white_stroke_primary_100_radius_12dp,
                )

                else -> viewProfileEditNickname.setBackgroundResource(
                    bg_profile_edit_gray_50_radius_12dp,
                )
            }

            ivProfileEditNicknameClear.isSelected = uiState.defaultState
        }

    private fun getColoredText(
        text: String,
        wordsToColor: List<String>,
        color: Int,
    ): SpannableString {
        val spannableString = SpannableString(text)
        wordsToColor.forEach { word ->
            val start = text.indexOf(word)
            if (start >= 0) {
                val end = start + word.length
                val colorSpan = ForegroundColorSpan(color)
                spannableString.setSpan(colorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        return spannableString
    }

    private fun updateIntroductionEditTextUi(introduction: String) {
        binding.tvProfileEditIntroductionCount.text = getColoredText(
            getString(profile_edit_introduction_max_count, introduction.length),
            listOf(introduction.length.toString()),
            AppCompatResources
                .getColorStateList(
                    this@ProfileEditActivity,
                    gray_300_52515F,
                ).defaultColor,
        )
    }

    private fun handleProfileEditResult(profileEditResult: ProfileEditResult) {
        when (profileEditResult) {
            ProfileEditResult.Success -> {
                setResult(ProfileEditSuccess.RESULT_OK)
                showWebsosoToast(
                    this,
                    getString(profile_edit_success),
                    ic_novel_detail_check,
                )
                finish()
            }

            ProfileEditResult.Error -> {
                showWebsosoToast(
                    this,
                    getString(novel_rating_save_error),
                    ic_novel_rating_alert,
                )
            }

            else -> return
        }
    }

    private fun updateFinishButtonStatus(isEnable: Boolean) {
        with(binding.tvProfileEditFinish) {
            isSelected = isEnable
            isEnabled = isEnable
        }
    }

    private fun setupGenreChips() {
        Genre.entries.forEach { genre ->
            WebsosoChip(binding.root.context)
                .apply {
                    setWebsosoChipText(genre.krName)
                    setWebsosoChipTextAppearance(body2)
                    setWebsosoChipTextColor(bg_profile_edit_chip_text_selector)
                    setWebsosoChipStrokeColor(bg_profile_edit_chip_stroke_selector)
                    setWebsosoChipBackgroundColor(bg_profile_edit_chip_background_selector)
                    setWebsosoChipPaddingVertical(12f.toFloatPxFromDp())
                    setWebsosoChipPaddingHorizontal(6f.toFloatPxFromDp())
                    setWebsosoChipRadius(20f.toFloatPxFromDp())
                    setOnWebsosoChipClick { profileEditViewModel.updateSelectedGenres(genre) }
                }.also { websosoChip -> binding.wcgProfileEditPreferGenre.addChip(websosoChip) }
        }
    }

    private fun onNicknameEditTextChange() {
        with(binding.etProfileEditNickname) {
            setOnFocusChangeListener { _, hasFocus ->
                profileEditViewModel.updateNicknameFocus(
                    hasFocus,
                )
            }
            addTextChangedListener { profileEditViewModel.updateNickname(it.toString()) }
        }
    }

    private fun onIntroductionEditTextChange() {
        with(binding.etProfileEditIntroduction) {
            setOnFocusChangeListener { _, hasFocus ->
                binding.clProfileEditIntroduction.isSelected = hasFocus
            }
            addTextChangedListener { profileEditViewModel.updateIntroduction(it.toString()) }
        }
    }

    private fun showAvatarChangeBottomSheetDialog() {
        val existingDialog =
            supportFragmentManager.findFragmentByTag(PROFILE_EDIT_CHARACTER_BOTTOM_SHEET_DIALOG)
        if (existingDialog == null) {
            AvatarChangeBottomSheetDialog().show(
                supportFragmentManager,
                PROFILE_EDIT_CHARACTER_BOTTOM_SHEET_DIALOG,
            )
        }
    }

    private fun updateAvatarThumbnail(avatarThumbnail: String) {
        if (avatarThumbnail.isEmpty() || Patterns.WEB_URL.matcher(avatarThumbnail).matches()) return
        val updatedAvatarThumbnail = binding.root.getS3ImageUrl(avatarThumbnail)
        profileEditViewModel.updateAvatarThumbnail(updatedAvatarThumbnail)
    }

    private fun setupProfileIntroductionMaxLines() {
        binding.etProfileEditIntroduction.filters =
            arrayOf(
                InputFilter { source, start, end, dest, dstart, dend ->

                    val newText = dest.toString().substring(0, dstart) +
                            source.toString().substring(start, end) +
                            dest.toString().substring(dend)

                    if (newText.length > MAX_LENGTH || newText.lines().size > MAX_LINES) {
                        ""
                    } else {
                        null
                    }
                },
            )
    }

    companion object {
        private const val PROFILE_EDIT_CHARACTER_BOTTOM_SHEET_DIALOG =
            "PROFILE_EDIT_CHARACTER_BOTTOM_SHEET_DIALOG"

        private const val MAX_LENGTH = 50
        private const val MAX_LINES = 2

        fun getIntent(context: Context): Intent = Intent(context, ProfileEditActivity::class.java)
    }
}
