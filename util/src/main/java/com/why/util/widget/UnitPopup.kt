package com.why.util.widget

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.widget.RadioGroup
import com.why.util.R
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.TranslationConfig

class UnitPopup(
    private val isLen: Boolean,
    private val isBottom: Boolean,
    context: Context,
    val onUnitChange: (Int) -> Unit
) :
    BasePopupWindow(context) {
    init {
        contentView = if (isLen) {
            createPopupById(R.layout.unit_len_popup)
        } else {
            createPopupById(R.layout.unit_area_popup)
        }
    }

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        val radioGroup: RadioGroup = if (isLen) {
            contentView.findViewById(R.id.lenRadioGroup)
        } else {
            contentView.findViewById(R.id.areaRadioGroup)
        }
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            onUnitChange(checkedId)
            dismiss()
        }
    }


    override fun onCreateShowAnimation(): Animation {
        return AnimationHelper.asAnimation()
            .withTranslation(
                if (isBottom) {
                    TranslationConfig.FROM_BOTTOM
                } else {
                    TranslationConfig.FROM_TOP
                }
            )
            .toShow()
    }
}