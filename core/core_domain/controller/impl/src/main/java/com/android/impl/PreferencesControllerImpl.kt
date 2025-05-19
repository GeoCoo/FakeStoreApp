package com.android.impl

import  androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import javax.inject.Inject
import androidx.core.content.edit
import com.android.api.PreferencesController
import com.android.api.ResourceProvider

class PreferencesControllerImpl @Inject constructor(private val resourceProvider: ResourceProvider) :
    PreferencesController {
    /**
     * Master key used to encrypt/decrypt shared preferences.
     */
    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(resourceProvider.provideContext())
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
    }

    /**
     * Pref key scheme used to initialize [androidx.security.crypto.EncryptedSharedPreferences] instance.
     */
    private val prefKeyEncryptionScheme by lazy {
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV
    }

    /**
     * Pref value scheme used to initialize [androidx.security.crypto.EncryptedSharedPreferences] instance.
     */
    private val prefValueEncryptionScheme by lazy {
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    }

    /**
     * Initializes and returns a new [android.content.SharedPreferences] instance. Instance is using an encryption
     * to store data in device.
     *
     * @return A new [android.content.SharedPreferences] instance.
     */

    private val sharedPrefs by lazy {
        EncryptedSharedPreferences.create(
            resourceProvider.provideContext(),
            "secret_shared_prefs",
            masterKey,
            prefKeyEncryptionScheme,
            prefValueEncryptionScheme
        )
    }

    override fun contains(key: String): Boolean {
        return sharedPrefs.contains(key)
    }

    override fun clear(key: String) {
        sharedPrefs.edit {
            remove(key)
        }
    }

    override fun clear() {
        sharedPrefs.edit { clear() }
    }

    override fun setString(key: String, value: String) {
        sharedPrefs.edit { putString(key, value) }
    }

    override fun setLong(
        key: String, value: Long
    ) {
        sharedPrefs.edit { putLong(key, value) }
    }

    override fun setBool(key: String, value: Boolean) {
        sharedPrefs.edit { putBoolean(key, value) }
    }

    override fun getString(key: String, defaultValue: String): String {
        return sharedPrefs.getString(key, defaultValue) ?: defaultValue
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return sharedPrefs.getLong(key, defaultValue)
    }

    override fun getBool(key: String, defaultValue: Boolean): Boolean {
        return sharedPrefs.getBoolean(key, defaultValue)
    }
}