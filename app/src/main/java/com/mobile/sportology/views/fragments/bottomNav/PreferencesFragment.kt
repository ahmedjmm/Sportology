package com.mobile.sportology.views.fragments.bottomNav

import android.app.Activity
import android.app.LocaleManager
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.DropDownPreference
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mobile.sportology.BuildConfig
import com.mobile.sportology.R
import com.mobile.sportology.views.viewsUtilities.DateTimeUtils
import com.mobile.sportology.servicesAndUtilities.LanguageManager
import com.mobile.sportology.viewModels.SettingsViewModel
import com.mobile.sportology.views.activities.HomeActivity
import java.util.Locale


class PreferencesFragment: PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener, CustomPreference.MyClickListener {
    private val sync_settings_key = "sync_settings"
    private val time_format_key = "time_format"
    private val news_language_key = "news_language"
    private val time_zone_key = "time_zone"
    private val app_language_key = "app_language"
    private lateinit var localeManager: LocaleManager
    private lateinit var languageManager: LanguageManager

    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private lateinit var signInRequest: BeginSignInRequest
    private var db = Firebase.firestore

    private lateinit var accountPreference: CustomPreference
    private lateinit var syncSettingsPreference: SwitchPreference
    private lateinit var timeZonePreference: ListPreference
    private lateinit var timeFormatPreference: DropDownPreference
    private lateinit var newsLanguagePreference: ListPreference
    private lateinit var appLanguagePreference: DropDownPreference

    private lateinit var myClickListener: CustomPreference.MyClickListener
    private lateinit var settingsViewModel: SettingsViewModel

    private val accountActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                Log.d("onActivityResult", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("onActivityResult()", "Google sign in failed", e)
                settingsViewModel.setAccount(null)
            }
        }
        else settingsViewModel.setAccount(null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeDependencies()
        settingsViewModel = (requireActivity() as HomeActivity).settingsViewModel
        settingsViewModel.setAccount(user)
    }

    private fun initializeDependencies() {
        myClickListener = this
        googleSignInOptions = buildGoogleSignInOptions()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignInOptions)
        signInRequest = buildSignInRequest()
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser
    }

    private fun buildSignInRequest(): BeginSignInRequest =
        BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(BuildConfig.AUTH_WEB_CLIENT_ID)
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(true)
                    .build())
            .build()

    private fun buildGoogleSignInOptions(): GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.AUTH_WEB_CLIENT_ID)
            .requestEmail()
            .build()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
    }

    private fun initializeUI() {
        accountPreference = findPreference<CustomPreference>("google_account")!!.apply {
            this.myClickListener = this@PreferencesFragment
            this.lifecycleOwner = this@PreferencesFragment.viewLifecycleOwner
            this.settingsViewModel = this@PreferencesFragment.settingsViewModel
        }
        syncSettingsPreference = findPreference(sync_settings_key)!!
        newsLanguagePreference = findPreference(news_language_key)!!
        timeFormatPreference = findPreference(time_format_key)!!
        timeZonePreference = findPreference(time_zone_key)!!
        appLanguagePreference = findPreference(app_language_key)!!
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            news_language_key, time_zone_key -> addSettingsToCloudIfSyncIsChecked()
            time_format_key -> {
                DateTimeUtils.timeFormat = timeFormatPreference.entry.toString()
                addSettingsToCloudIfSyncIsChecked()
            }
            app_language_key -> handleAppLanguageChange(sharedPreferences)
            sync_settings_key -> handleSyncSettingsChange(sharedPreferences)
        }
    }

    private fun handleSyncSettingsChange(sharedPreferences: SharedPreferences?) {
        if(sharedPreferences?.getBoolean(sync_settings_key, false)!!) {
            user?.let {
                syncSettingsPreference.isChecked = true
                addSettingsToCloudIfSyncIsChecked()
            }?: run {
                syncSettingsPreference.isChecked = false
                Toast.makeText(context, R.string.sign_in_required, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleAppLanguageChange(sharedPreferences: SharedPreferences?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            localeManager = context?.getSystemService(LocaleManager::class.java)!!
            localeManager.applicationLocales = LocaleList(Locale.forLanguageTag(appLanguagePreference.value.toString()))
        }
        else {
            languageManager = LanguageManager(requireContext(), sharedPreferences!!)
            onLanguageSelected(sharedPreferences.getString(app_language_key, "en")!!)
        }
        addSettingsToCloudIfSyncIsChecked()
    }

    private fun addSettingsToCloudIfSyncIsChecked() {
        if (syncSettingsPreference.isChecked) {
            val settings = hashMapOf(
                time_zone_key to timeZonePreference.value,
                time_format_key to timeFormatPreference.value,
                news_language_key to newsLanguagePreference.value,
                app_language_key to appLanguagePreference.value
            )
            user?.let {
                db.collection(it.uid)
                    .document(resources.getString(R.string.settings_firestore)).set(settings)
                    .addOnSuccessListener {}
                    .addOnFailureListener { e ->
                        Log.w("addSettingsToCloud()", "Error writing document", e)
                        Toast.makeText(requireContext(), R.string.failed_setting, Toast.LENGTH_LONG).show()
                    }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    private fun onLanguageSelected(language: String) {
        languageManager.setCurrentLanguage(language)
        requireActivity().recreate()
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    user = auth.currentUser
                    settingsViewModel.setAccount(user)
                } else {
                    // sign in fails.
                    Log.w("onActivityResult()", "signInWithCredential:failure", task.exception)
                    settingsViewModel.setAccount(null)
                }
            }
    }

    override fun signIn() {
        settingsViewModel.loadAccount()
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        accountActivityResultLauncher.launch(signInIntent)
    }

    override fun signOut() {
        auth.signOut()
        user = null
        mGoogleSignInClient.signOut().addOnSuccessListener {
            settingsViewModel.setAccount(null)
            syncSettingsPreference.isChecked = false
            Toast.makeText(context, requireContext().getString(R.string.sign_out_successful), Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Log.e("signOutTask", it.toString())
        }
    }

    override fun importSettings() {
        user?.let {
            val docRef = db.collection(it.uid).document("settings_firestore")
            docRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    timeZonePreference.value = document.getString(time_zone_key)
                    newsLanguagePreference.value = document.getString(news_language_key)
                    timeFormatPreference.value = document.getString(time_format_key)
                    appLanguagePreference.value = document.getString(app_language_key)
                }
            }.addOnFailureListener { exception ->
                Log.e("importSettings()", exception.toString())
            }
        }
    }
}