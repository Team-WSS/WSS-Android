package com.into.websoso.core.common.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import com.into.websoso.core.common.util.DynamicLimitPhotoPicker.Input

/**
 * Websoso 프로젝트에서 사용하는 커스텀 이미지 선택기입니다.
 *
 * 이 클래스는 이미지 여러 장을 선택할 수 있으며,
 * 선택 가능한 최대 개수(maxSelectable)를 런타임에서 동적으로 지정할 수 있습니다.
 *
 * Android 13(API 33) 이상에서는 시스템의 PhotoPicker([MediaStore.ACTION_PICK_IMAGES])를 사용하며,
 * `MediaStore.EXTRA_PICK_IMAGES_MAX`를 통해 선택 가능 개수를 제한할 수 있습니다.
 *
 * Android 13 미만에서는 [Intent.ACTION_OPEN_DOCUMENT]를 사용하여
 * 여러 장 선택이 가능하지만 선택 개수 제한은 직접 처리해주셔야 합니다.
 *
 * 사용 예시:
 * ```
 * val pickerLauncher = registerForActivityResult(DynamicLimitPhotoPicker()) { uris ->
 *     // 선택된 이미지 URI 목록을 처리합니다
 * }
 *
 * pickerLauncher.launch(DynamicLimitPhotoPicker.Input(maxSelectable = 3))
 * ```
 *
 * @return 선택된 이미지들의 [Uri] 목록을 반환합니다.
 * @see Input 선택 가능한 최대 개수를 설정할 수 있는 입력 파라미터입니다.
 */
class DynamicLimitPhotoPicker : ActivityResultContract<DynamicLimitPhotoPicker.Input, List<Uri>>() {
    data class Input(
        val maxSelectable: Int,
    )

    override fun createIntent(
        context: Context,
        input: Input,
    ): Intent {
        val mimeType = "image/*"
        val intent: Intent

        if (Build.VERSION.SDK_INT >= 33) {
            intent = Intent(MediaStore.ACTION_PICK_IMAGES).apply {
                type = mimeType
                putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, input.maxSelectable)
            }
        } else {
            intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = mimeType
                addCategory(Intent.CATEGORY_OPENABLE)
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
        }

        return intent
    }

    override fun parseResult(
        resultCode: Int,
        intent: Intent?,
    ): List<Uri> {
        if (resultCode != Activity.RESULT_OK || intent == null) return emptyList()

        val uris = mutableListOf<Uri>()

        intent.clipData?.let { clip ->
            for (i in 0 until clip.itemCount) {
                clip.getItemAt(i).uri?.let { uris.add(it) }
            }
        }

        intent.data?.let { uris.add(it) }

        return uris
    }
}
