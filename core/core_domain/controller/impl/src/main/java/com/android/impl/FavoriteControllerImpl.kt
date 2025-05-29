package com.android.impl

import com.android.api.FavoriteController
import com.android.api.FavoriteControllerPartialState
import com.android.api.FavoriteControllerPartialState.Failed
import com.android.api.FavoriteControllerPartialState.Success
import com.android.api.PreferencesController
import com.android.api.ResourceProvider
import com.android.fakestore.core.core_resources.R
import com.android.model.Preferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FavoriteControllerImpl @Inject constructor(
    private val preferencesController: PreferencesController,
    private val resourceProvider: ResourceProvider,
    val gson: Gson
) : FavoriteController {


    private fun loadFavoriteProducts(userId: String?): MutableList<Int> {
        val json = preferencesController.getString(Preferences.FAVORITE_PRODUCTS.pref, "")
        if (json.isEmpty()) {
            return mutableListOf()
        } else {
            val type = object : TypeToken<MutableMap<String, MutableList<Int>>?>() {}.type
            val userFavoritesMap = gson.fromJson<MutableMap<String, MutableList<Int>>?>(json, type) ?: mutableMapOf()
            return userFavoritesMap[userId]?.toMutableList() ?: mutableListOf()
        }
    }

    override fun getFavorites(userId: String?): Flow<FavoriteControllerPartialState> = flow {
        val response = loadFavoriteProducts(userId)
        when(response.isEmpty()){
            true -> {
                emit(Failed(resourceProvider.getString(R.string.generic_error_msg)))
            }
            false -> {
                emit(Success(loadFavoriteProducts(userId).reversed()))
            }
        }
    }

    override fun handleFavorites(
        userId: String?,
        id: Int,
        isFavorite: Boolean
    ): Flow<FavoriteControllerPartialState> = flow {
        val json = preferencesController.getString(Preferences.FAVORITE_PRODUCTS.pref, "")
        val type = object : TypeToken<MutableMap<String?, MutableList<Int>>?>() {}.type
        val userFavoritesMap = if (json.isEmpty()) {
            mutableMapOf<String?, MutableList<Int>>()
        } else {
            gson.fromJson<MutableMap<String?, MutableList<Int>>?>(json, type) ?: mutableMapOf()
        }

        val products = userFavoritesMap[userId] ?: mutableListOf()

        if (!isFavorite) {
            if (!products.contains(id)) {
                products.add(id)
            }
        } else {
            products.remove(id)
        }

        userFavoritesMap[userId] = products.toMutableList()
        val updatedJson = gson.toJson(userFavoritesMap)
        preferencesController.setString(Preferences.FAVORITE_PRODUCTS.pref, updatedJson)

        emit(Success(products.reversed()))
    }
}