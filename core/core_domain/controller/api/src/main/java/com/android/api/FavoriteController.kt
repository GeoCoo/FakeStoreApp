package com.android.api

import kotlinx.coroutines.flow.Flow

interface FavoriteController {
    fun getFavorites(userId: String?): Flow<FavoriteControllerPartialState>
    fun handleFavorites(
        userId: String?,
        id: Int,
        isFavorite: Boolean
    ): Flow<FavoriteControllerPartialState>
}


sealed class FavoriteControllerPartialState {
    data class Success(val products: List<Int>) : FavoriteControllerPartialState()
    data class Failed(val errorMessage: String) : FavoriteControllerPartialState()

}