package com.mobile.sportology.views.fragments.bottomNav

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.auth.FirebaseUser
import com.mobile.sportology.R
import com.mobile.sportology.ResponseState
import com.mobile.sportology.viewModels.SettingsViewModel
import com.mobile.sportology.views.viewsUtilities.ViewCrossFadeAnimation
import com.mobile.sportology.views.viewsUtilities.imageBinding

class CustomPreference(
    context: Context?,
    attrs: AttributeSet?,
) : Preference(context!!, attrs), ViewCrossFadeAnimation {
    lateinit var myClickListener: MyClickListener
    lateinit var lifecycleOwner: LifecycleOwner
    lateinit var settingsViewModel: SettingsViewModel

    override var shortAnimationDuration: Int = 0

    private lateinit var profileImage: ImageView
    private lateinit var email: TextView
    private lateinit var username: TextView
    private lateinit var signOut: View
    private lateinit var signIn: View
    private lateinit var importSettings: View
    private lateinit var progressIndicator: CircularProgressIndicator

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        initializeViews(holder)
        signIn.setOnClickListener {
            showViewWithAnimation(progressIndicator)
            myClickListener.signIn()
        }

        signOut.setOnClickListener {
            myClickListener.signOut()
        }

        importSettings.setOnClickListener {
            myClickListener.importSettings()
        }

        settingsViewModel.accountLiveData.observe(lifecycleOwner) { responseState ->
            when(responseState) {
                is ResponseState.Success -> handleSuccessResponse(responseState.data)
                is ResponseState.Loading -> handleLoadingResponse()
                else -> {}
            }
        }
    }

    override fun hideViewWithAnimation(view: View){
        view.animate().alpha(0f).setDuration(shortAnimationDuration.toLong()).setListener(object: AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                view.visibility = View.GONE
            }
        })
    }

    override fun showViewWithAnimation(view: View) {
        view.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate().alpha(1f).setDuration(shortAnimationDuration.toLong()).setListener(null)
        }
    }

    private fun handleLoadingResponse() {
        showViewWithAnimation(progressIndicator)
        hideViewWithAnimation(profileImage)
        hideViewWithAnimation(username)
        hideViewWithAnimation(email)
        hideViewWithAnimation(signOut)
        hideViewWithAnimation(signIn)
    }

    private fun handleSuccessResponse(user: FirebaseUser?) {
        hideViewWithAnimation(signIn)
        hideViewWithAnimation(progressIndicator)
        user?.let {
            profileImage.apply {
                imageBinding(it.photoUrl.toString())
                alpha = 0f
                visibility = View.VISIBLE
                animate().alpha(1f).setDuration(shortAnimationDuration.toLong()).setListener(null)
            }
            username.apply {
                text = user.displayName
                showViewWithAnimation(this)
            }
            email.apply {
                text = user.email
                showViewWithAnimation(this)
            }
            showViewWithAnimation(signOut)
            showViewWithAnimation(importSettings)
        }?: run {
            hideViewWithAnimation(profileImage)
            hideViewWithAnimation(username)
            hideViewWithAnimation(email)
            hideViewWithAnimation(signOut)
            hideViewWithAnimation(importSettings)
            showViewWithAnimation(signIn)
            hideViewWithAnimation(progressIndicator)
        }
    }

    private fun initializeViews(holder: PreferenceViewHolder) {
        shortAnimationDuration = context.resources.getInteger(android.R.integer.config_shortAnimTime)
        profileImage = holder.findViewById(R.id.profile_image) as ImageView
        email = holder.findViewById(R.id.email) as TextView
        username = holder.findViewById(R.id.username) as TextView
        progressIndicator = holder.findViewById(R.id.progress_indicator) as CircularProgressIndicator
        signIn = holder.findViewById(R.id.sign_in_button)
        signOut = holder.findViewById(R.id.sign_out_button)
        importSettings = holder.findViewById(R.id.import_settings)
    }

    interface MyClickListener {
        fun signIn()

        fun signOut()

        fun importSettings()
    }
}