package com.rifara.githubuser.data.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rifara.githubuser.data.local.Favorite

@Dao
interface FavoriteDao {

    @Insert
    fun addFavorite(favorite: Favorite)

    @Query("SELECT * FROM favorite")
    fun getFavorite(): LiveData<List<Favorite>>

    @Query("SELECT count(*) FROM favorite WHERE favorite.id = :id")
    fun checkFavorite(id: Int): Int

    @Query("DELETE FROM favorite WHERE favorite.id = :id")
    fun removeFavorite(id: Int): Int

}