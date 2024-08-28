package com.teamwss.websoso.ui.profileEdit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.forEach
import androidx.core.widget.addTextChangedListener
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.common.ui.custom.WebsosoChip
import com.teamwss.websoso.common.util.getAdaptedParcelableExtra
import com.teamwss.websoso.databinding.ActivityProfileEditBinding
import com.teamwss.websoso.ui.profileEdit.model.Genre
import com.teamwss.websoso.ui.profileEdit.model.Genre.Companion.toGenreFromKr
import com.teamwss.websoso.ui.profileEdit.model.NicknameEditResult.NONE
import com.teamwss.websoso.ui.profileEdit.model.NicknameEditResult.VALID_NICKNAME
import com.teamwss.websoso.ui.profileEdit.model.ProfileEditResult
import com.teamwss.websoso.ui.profileEdit.model.ProfileEditUiState
import com.teamwss.websoso.ui.profileEdit.model.ProfileModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileEditActivity : BaseActivity<ActivityProfileEditBinding>(R.layout.activity_profile_edit) {
    private val profileEditViewModel: ProfileEditViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindViewModel()
        setupObserver()
        initProfileInfo()
        setupGenreChips()
        onNicknameEditTextChange()
        onIntroductionEditTextChange()
        // TODO: 프로필 이미지 설정 다이알로그 추가
    }

    private fun bindViewModel() {
        binding.viewModel = profileEditViewModel
        binding.lifecycleOwner = this
    }

    private fun setupObserver() {
        profileEditViewModel.uiState.observe(this) { uiState ->
            updateGenreChips(uiState.profile.genrePreferences)
            updateDuplicateCheckButton(uiState.isCheckDuplicateNicknameEnabled)
            updateNicknameEditTextUi(uiState)
            updateIntroductionEditTextUi(uiState.profile.introduction)
            handleProfileEditResult(uiState.profileEditResult)
            profileEditViewModel.updateCheckDuplicateNicknameBtnEnabled()
        }
    }

    private fun updateGenreChips(genrePreferences: List<Genre>) {
        binding.wcgProfileEditPreferGenre.forEach { view ->
            val chip = view as WebsosoChip
            chip.isSelected = genrePreferences.contains(chip.text.toString().toGenreFromKr())
        }
    }

    private fun updateDuplicateCheckButton(isEnable: Boolean) {
        binding.tvProfileEditNicknameCheckDuplicate.setTextColor(
            if (isEnable) AppCompatResources.getColorStateList(this, R.color.primary_100_6A5DFD).defaultColor
            else AppCompatResources.getColorStateList(this, R.color.gray_200_AEADB3).defaultColor
        )
        binding.tvProfileEditNicknameCheckDuplicate.setBackgroundResource(
            if (isEnable) R.drawable.bg_profile_edit_primary_50_radius_12dp
            else R.drawable.bg_profile_edit_gray_70_radius_12dp
        )
        binding.tvProfileEditNicknameCheckDuplicate.isEnabled = isEnable
    }

    private fun updateNicknameEditTextUi(uiState: ProfileEditUiState) = with(binding) {
        tvProfileEditNicknameCount.text = getColoredText(
            getString(R.string.profile_edit_nickname_max_count, uiState.profile.nicknameModel.nickname.length),
            listOf(uiState.profile.nicknameModel.nickname.length.toString()),
            AppCompatResources.getColorStateList(this@ProfileEditActivity, R.color.gray_300_52515F).defaultColor
        )
        tvProfileEditNickname.isSelected = uiState.profile.nicknameModel.nickname.isNotEmpty()
        tvProfileEditNicknameError.text = uiState.nicknameEditResult.message
        val defaultState = uiState.nicknameEditResult != VALID_NICKNAME && uiState.nicknameEditResult != NONE
        when {
            defaultState -> viewProfileEditNickname.setBackgroundResource(R.drawable.bg_profile_edit_white_stroke_secondary_100_radius_12dp)
            uiState.profile.nicknameModel.hasFocus -> viewProfileEditNickname.setBackgroundResource(R.drawable.bg_profile_edit_white_stroke_gray_70_radius_12dp)
            else -> viewProfileEditNickname.setBackgroundResource(R.drawable.bg_profile_edit_gray_50_radius_12dp)
        }
        ivProfileEditNicknameClear.isSelected = defaultState
    }

    private fun getColoredText(text: String, wordsToColor: List<String>, color: Int): SpannableString {
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
            getString(R.string.profile_edit_introduction_max_count, introduction.length),
            listOf(introduction.length.toString()),
            AppCompatResources.getColorStateList(this@ProfileEditActivity, R.color.gray_300_52515F).defaultColor
        )
    }

    private fun handleProfileEditResult(profileEditResult: ProfileEditResult) {
        when (profileEditResult) {
            ProfileEditResult.Success -> finish()
            ProfileEditResult.Failure -> Unit // TODO: 실패 처리
            else -> return
        }
    }

    private fun initProfileInfo() {
        val profile = intent.getAdaptedParcelableExtra<ProfileModel>(PROFILE_INFO)
        profileEditViewModel.updatePreviousProfile(profile ?: ProfileModel())
    }

    private fun setupGenreChips() {
        Genre.entries.forEach { genre ->
            WebsosoChip(binding.root.context)
                .apply {
                    setWebsosoChipText(genre.krName)
                    setWebsosoChipTextAppearance(R.style.body2)
                    setWebsosoChipTextColor(R.color.bg_profile_edit_chip_text_selector)
                    setWebsosoChipStrokeColor(R.color.bg_profile_edit_chip_stroke_selector)
                    setWebsosoChipBackgroundColor(R.color.bg_profile_edit_chip_background_selector)
                    setWebsosoChipPaddingVertical(20f)
                    setWebsosoChipPaddingHorizontal(12f)
                    setWebsosoChipRadius(40f)
                    setOnWebsosoChipClick { profileEditViewModel.updateSelectedGenres(genre) }
                }.also { websosoChip -> binding.wcgProfileEditPreferGenre.addChip(websosoChip) }
        }
    }

    private fun onNicknameEditTextChange() {
        with(binding.etProfileEditNickname) {
            setOnFocusChangeListener { _, hasFocus -> profileEditViewModel.updateNicknameFocus(hasFocus) }
            addTextChangedListener { profileEditViewModel.updateNickname(it.toString()) }
        }
    }

    private fun onIntroductionEditTextChange() {
        with(binding.etProfileEditIntroduction) {
            setOnFocusChangeListener { _, hasFocus -> binding.clProfileEditIntroduction.isSelected = hasFocus }
            addTextChangedListener { profileEditViewModel.updateIntroduction(it.toString()) }
        }
    }

    companion object {
        private const val PROFILE_INFO = "PROFILE_INFO"

        fun getIntent(context: Context, profileModel: ProfileModel): Intent {
            return Intent(context, ProfileEditActivity::class.java).apply {
                putExtra(PROFILE_INFO, profileModel)
            }
        }
    }
}
