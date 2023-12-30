package com.mobile.sportology.views.fragments.bottomNav

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mobile.sportology.BuildConfig
import com.mobile.sportology.R
import com.mobile.sportology.Shared
import com.mobile.sportology.databinding.FragmentSettingsBinding
import com.mobile.sportology.servicesAndUtilities.DateTimeUtils
import com.mobile.sportology.views.viewsUtilities.imageBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private var selectedLanguageIndex = 0
    private val RC_SIGN_IN = 10
    private var selectedTimeZoneIndex = 0
    private var isSyncOption = false
    private lateinit var _binding: FragmentSettingsBinding
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(BuildConfig.AUTH_WEB_CLIENT_ID).requestEmail().build()
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private lateinit var signInRequest: BeginSignInRequest
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(BuildConfig.AUTH_WEB_CLIENT_ID)
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(true)
                    .build())
            .build()
        val resources = resources
        selectedTimeZoneIndex = sharedPreferences.getInt(resources.getString(R.string.select_time_zone_index), 0)
        selectedLanguageIndex = sharedPreferences.getInt(resources.getString(R.string.selected_news_language_index), 0)
        isSyncOption = sharedPreferences.getBoolean(resources.getString(R.string.is_sync_options), false)
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val account = GoogleSignIn.getLastSignedInAccount(requireActivity())
        setupUI(account)
        setupClickListeners()
    }


    private fun setupUI(account: GoogleSignInAccount?) {
        if (account != null) {
            _binding.apply {
                username.text = account.displayName
                username.visibility = View.VISIBLE
                email.text = account.email
                email.visibility = View.VISIBLE
                profileImage.imageBinding(account.photoUrl.toString())
                profileImage.visibility = View.VISIBLE
                signInButton.visibility = View.GONE
                signOutButton.visibility = View.VISIBLE
            }
        } else {
            _binding.apply {
                username.visibility = View.GONE
                email.visibility = View.GONE
                profileImage.visibility = View.GONE
                signInButton.visibility = View.VISIBLE
                signOutButton.visibility = View.GONE
            }
        }
        _binding.selectedTimeZone.text = sharedPreferences.getString(
            resources.getString(R.string.selected_time_zone), resources.getStringArray(R.array.time_zones)[selectedTimeZoneIndex])
        _binding.selectedTimeFormat.text = sharedPreferences.getString(
            resources.getString(R.string.selected_time_format), resources.getString(R.string.time_format_12))
        _binding.selectedNewsLanguage.text = sharedPreferences.getString(
            resources.getString(R.string.selected_news_language), resources.getStringArray(R.array.language_key)[selectedLanguageIndex])
    }

    private fun setupClickListeners() {
        _binding.signInButton.setOnClickListener {
            if (Shared.isConnected) signIn()
            else Toast.makeText(requireContext(), R.string.unable_to_connect, Toast.LENGTH_SHORT).show()
        }
        _binding.signOutButton.setOnClickListener { signOut() }

        _binding.timeZones.setOnClickListener { changeTimeZoneSetting(isSyncOption = isSyncOption) }
        _binding.timeFormat.setOnClickListener {
            changeTimeFormatSetting(syncOption = isSyncOption, view = _binding.timeFormat)
        }
        _binding.newsLanguage.setOnClickListener {
            changeNewsLanguage(isSyncOption)
        }

        _binding.syncSwitcher.isChecked = isSyncOption
        _binding.syncSwitcher.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                user?.let {
                    isSyncOption = true
                    val setting = hashMapOf(
                        resources.getString(R.string.selected_time_zone) to resources.getStringArray(R.array.time_zones)[selectedTimeZoneIndex],
                        resources.getString(R.string.selected_time_format) to _binding.selectedTimeFormat.text.toString(),
                        resources.getString(R.string.selected_news_language_index) to selectedLanguageIndex.toString()
                    )
                    changeFirestorePersistenceSetting(true)
                    addSettingsToCloud(setting)
                }?: run {
                    isSyncOption = false
                    _binding.syncSwitcher.isChecked = false
                    Toast.makeText(context, R.string.sign_in_required, Toast.LENGTH_SHORT).show()
                }
            } else {
                isSyncOption = false
                changeFirestorePersistenceSetting(false)
            }
            sharedPreferences.edit().putBoolean(resources.getString(R.string.is_sync_options), isSyncOption).commit()
        }
    }

    private fun changeNewsLanguage(isSyncOption: Boolean = false) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.news_language))
            .setSingleChoiceItems(resources.getStringArray(R.array.language_key), selectedLanguageIndex) { _, which ->
                selectedLanguageIndex = which
            }
            .setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
                sharedPreferences.edit().apply {
                    putString(
                        resources.getString(R.string.selected_news_language), resources.getStringArray(R.array.language_key)[selectedLanguageIndex])
                    putInt(
                        resources.getString(R.string.selected_news_language_index), selectedLanguageIndex)
                }.commit()
                _binding.selectedNewsLanguage.text = resources.getStringArray(R.array.language_key)[selectedLanguageIndex]
                if(isSyncOption) {
                    val setting = hashMapOf(
                        resources.getString(R.string.selected_time_zone) to resources.getStringArray(R.array.time_zones)[selectedTimeZoneIndex],
                        resources.getString(R.string.selected_time_format) to _binding.selectedTimeFormat.text.toString(),
                        resources.getString(R.string.selected_news_language) to selectedLanguageIndex.toString()
                    )
                    addSettingsToCloud(setting)
                }
            }.setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun changeFirestorePersistenceSetting(isSyncOption: Boolean = true) {
        db.terminate()
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(isSyncOption)
            .build()
        db = Firebase.firestore
        db.firestoreSettings = settings
    }

    private fun changeTimeFormatSetting(syncOption: Boolean = false, view: View) {
        showMenu(syncOption, view, R.menu.time_format)
    }

    private fun changeTimeZoneSetting(isSyncOption: Boolean = false) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.select_time_zone))
            .setSingleChoiceItems(resources.getStringArray(R.array.time_zones), selectedTimeZoneIndex) { _, which ->
                selectedTimeZoneIndex = which
            }
            .setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
                sharedPreferences.edit().apply {
                    putString(
                        resources.getString(R.string.selected_time_zone), resources.getStringArray(R.array.time_zones)[selectedTimeZoneIndex])
                    putInt(
                        resources.getString(R.string.select_time_zone_index), selectedTimeZoneIndex)
                }.commit()
                _binding.selectedTimeZone.text = resources.getStringArray(R.array.time_zones)[selectedTimeZoneIndex]
                if(isSyncOption) {
                    val setting = hashMapOf(
                        resources.getString(R.string.selected_time_zone) to resources.getStringArray(R.array.time_zones)[selectedTimeZoneIndex],
                        resources.getString(R.string.selected_time_format) to _binding.selectedTimeFormat.text.toString(),
                        resources.getString(R.string.selected_news_language) to selectedLanguageIndex.toString()
                    )
                    addSettingsToCloud(setting)
                }
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun addSettingsToCloud(settings: HashMap<String, String>) {
        user?.let {
            db.collection(it.uid)
                .document(resources.getString(R.string.settings)).set(settings)
                .addOnSuccessListener {
                    Log.d("addSettingsToCloud()", "DocumentSnapshot successfully written!")
                }
                .addOnFailureListener { e -> Log.w("addSettingsToCloud()", "Error writing document", e) }
        }
    }

    private fun showMenu(isSyncOption: Boolean = false, v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(requireContext(), v)
        popup.menuInflater.inflate(menuRes, popup.menu)
        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when(menuItem.itemId) {
                R.id.time_format_12 -> {
                    DateTimeUtils.timeFormat = resources.getString(R.string.time_format_12)
                    sharedPreferences.edit().putString(
                        resources.getString(R.string.selected_time_format),
                        resources.getString(R.string.time_format_12)).commit()
                    _binding.selectedTimeFormat.text = resources.getString(R.string.time_format_12)
                }
                R.id.time_format_24 -> {
                    DateTimeUtils.timeFormat = resources.getString(R.string.time_format_24)
                    sharedPreferences.edit().putString(
                        resources.getString(R.string.selected_time_format),
                        resources.getString(R.string.time_format_24)).commit()
                    _binding.selectedTimeFormat.text = resources.getString(R.string.time_format_24)
                }
            }
            if(isSyncOption) {
                val setting = hashMapOf(
                    resources.getString(R.string.selected_time_zone) to resources.getStringArray(R.array.time_zones)[selectedTimeZoneIndex],
                    resources.getString(R.string.selected_time_format) to _binding.selectedTimeFormat.text.toString(),
                    resources.getString(R.string.selected_news_language) to selectedLanguageIndex.toString()
                )
                addSettingsToCloud(setting)
            }
            return@setOnMenuItemClickListener true
        }
        popup.show()
    }

    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("onActivityResult", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("onActivityResult()", "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    user = auth.currentUser
                    updateUI(user)
                } else {
                    // sign in fails.
                    Log.w("onActivityResult()", "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        user?.let {
            _binding.username.text = user.displayName
            _binding.username.visibility = View.VISIBLE
            _binding.email.text = user.email
            _binding.email.visibility = View.VISIBLE
            _binding.profileImage.imageBinding(user.photoUrl.toString())
            _binding.profileImage.visibility = View.VISIBLE
            _binding.signInButton.visibility = View.GONE
            _binding.signOutButton.visibility = View.VISIBLE
            Log.i("SignInResult", user.photoUrl.toString())
        }?: run {
            _binding.username.visibility = View.GONE
            _binding.email.visibility = View.GONE
            _binding.profileImage.visibility = View.GONE
            _binding.signInButton.visibility = View.VISIBLE
            _binding.signOutButton.visibility = View.GONE
            Log.e("SignInResult", "failed to sign in")
        }
    }

    private fun signOut() {
        auth.signOut()
        user = null
        mGoogleSignInClient.signOut().addOnSuccessListener {
            _binding.username.visibility = View.GONE
            _binding.email.visibility = View.GONE
            _binding.profileImage.visibility = View.GONE
            _binding.signInButton.visibility = View.VISIBLE
            _binding.signOutButton.visibility = View.GONE
            isSyncOption = false
            _binding.syncSwitcher.isChecked = isSyncOption
            Toast.makeText(context, requireContext().getString(R.string.sign_out_successful), Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Log.e("signOutTask", it.toString())
        }
    }
}