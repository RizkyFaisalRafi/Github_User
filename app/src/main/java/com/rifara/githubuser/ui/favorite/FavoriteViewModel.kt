package com.rifara.githubuser.ui.favorite

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.rifara.githubuser.data.local.Favorite
import com.rifara.githubuser.data.room.FavoriteDao
import com.rifara.githubuser.data.room.FavoriteDatabase

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {

    private var favoriteDb: FavoriteDatabase? = FavoriteDatabase.getFavoriteDatabase(application)
    private var favoriteDao: FavoriteDao? = favoriteDb?.favoriteDao()

    fun getFavorite(): LiveData<List<Favorite>>? {
        return favoriteDao?.getFavorite()
    }
}