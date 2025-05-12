package com.android.core.core_domain.controller

import android.content.SharedPreferences
import  androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.android.core_resources.provider.ResourceProvider
import javax.inject.Inject
import androidx.core.content.edit

interface PreferencesController {
    /**
     * Defines if [SharedPreferences] contains a value for given [key]. This function will only
     * identify if a key exists in storage and will not check if corresponding value is valid.
     *
     * @param key The name of the preference to check.
     *
     * @return `true` if preferences contain given key. `false` otherwise.
     */
    fun contains(key: String): Boolean

    /**
     * Removes given preference key from shared preferences. Notice that this operation is
     * irreversible and may lead to data loss.
     */
    fun clear(key: String)

    /**
     * Removes all keys from shared preferences. Notice that this operation is
     * irreversible and may lead to data loss.
     */
    fun clear()

    /**
     * Assigns given [value] to device storage - shared preferences given [key]. You can
     * retrieve this value by calling [getString].
     *
     * Shared preferences are encrypted. Do not create your own instance to add or retrieve data.
     * Instead, call operations of this controller.
     *
     * @param key   Key used to add given [value].
     * @param value Value to add after given [key].
     */
    fun setString(key: String, value: String)

    /**
     * Assigns given [value] to device storage - shared preferences given [key]. You can
     * retrieve this value by calling [getString].
     *
     * Shared preferences are encrypted. Do not create your own instance to add or retrieve data.
     * Instead, call operations of this controller.
     *
     * @param key   Key used to add given [value].
     * @param value Value to add after given [key].
     */
    fun setLong(
        key: String, value: Long
    )

    /**
     * Assigns given [value] to device storage - shared preferences given [key]. You can
     * retrieve this value by calling [getString].
     *
     * Shared preferences are encrypted. Do not create your own instance to add or retrieve data.
     * Instead, call operations of this controller.
     *
     * @param key   Key used to add given [value].
     * @param value Value to add after given [key].
     */
    fun setBool(key: String, value: Boolean)

    /**
     * Retrieves a string value from device shared preferences that corresponds to given [key]. If
     * key does not exist or value of given key is null, [defaultValue] is returned.
     *
     * Shared preferences are encrypted. Do not create your own instance to add or retrieve data.
     * Instead, call operations of this controller.
     *
     * @param key          Key to get corresponding value.
     * @param defaultValue Default value to return if given [key] does not exist in prefs or if
     * key value is invalid.
     */
    fun getString(key: String, defaultValue: String): String

    /**
     * Retrieves a long value from device shared preferences that corresponds to given [key]. If
     * key does not exist or value of given key is null, [defaultValue] is returned.
     *
     * Shared preferences are encrypted. Do not create your own instance to add or retrieve data.
     * Instead, call operations of this controller.
     *
     * @param key          Key to get corresponding value.
     * @param defaultValue Default value to return if given [key] does not exist in prefs or if
     * key value is invalid.
     */
    fun getLong(key: String, defaultValue: Long): Long

    /**
     * Retrieves a boolean value from device shared preferences that corresponds to given [key]. If
     * key does not exist or value of given key is null, [defaultValue] is returned.
     *
     * Shared preferences are encrypted. Do not create your own instance to add or retrieve data.
     * Instead, call operations of this controller.
     *
     * @param key          Key to get corresponding value.
     * @param defaultValue Default value to return if given [key] does not exist in prefs or if
     * key value is invalid.
     */
    fun getBool(key: String, defaultValue: Boolean): Boolean
}

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
     * Pref key scheme used to initialize [EncryptedSharedPreferences] instance.
     */
    private val prefKeyEncryptionScheme by lazy {
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV
    }

    /**
     * Pref value scheme used to initialize [EncryptedSharedPreferences] instance.
     */
    private val prefValueEncryptionScheme by lazy {
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    }

    /**
     * Initializes and returns a new [SharedPreferences] instance. Instance is using an encryption
     * to store data in device.
     *
     * @return A new [SharedPreferences] instance.
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
        sharedPrefs.edit { remove(key) }
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