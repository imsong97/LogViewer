package com.ch0pp4.logviewer.model

import com.logviewer.data.model.BuildInfo as BuildInfoModel

data class BuildInfo(
    val branch: String,
    val step: String,
    val swVersion: String,
) {
    companion object {
        val EMPTY = BuildInfo(branch = "", step = "", swVersion = "")
    }

    val hasAnyValues get() =
        branch.isNotBlank() && step.isNotBlank() && swVersion.isNotBlank()
}

fun BuildInfoModel.toPresentation() = BuildInfo(
    branch = branch,
    step = step,
    swVersion = swVersion,
)
