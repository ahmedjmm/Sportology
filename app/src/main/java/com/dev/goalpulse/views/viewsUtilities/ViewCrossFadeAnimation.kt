package com.dev.goalpulse.views.viewsUtilities

import android.view.View

interface ViewCrossFadeAnimation {
    var shortAnimationDuration: Int
    fun hideViewWithAnimation(view: View)

    fun showViewWithAnimation(view: View)
}