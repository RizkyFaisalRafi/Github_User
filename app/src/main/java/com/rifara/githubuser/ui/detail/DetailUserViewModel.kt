package com.rifara.githubuser.ui.detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rifara.githubuser.api.ApiConfig
import com.rifara.githubuser.data.local.Favorite
import com.rifara.githubuser.data.model.DetailUserResponse
import com.rifara.githubuser.data.room.FavoriteDao
import com.rifara.githubuser.data.room.FavoriteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailUserViewModel(application: Application) : AndroidViewModel(application) {

    val user = MutableLiveData<DetailUserResponse>()

    private var favoriteDb: FavoriteDatabase? = FavoriteDatabase.getFavoriteDatabase(application)
    private var favoriteDao: FavoriteDao? = favoriteDb?.favoriteDao()

    fun setUserDetail(username: String?) {
        ApiConfig.getApiService().getUserDetail(username)
            .enqueue(object : Callback<DetailUserResponse> {
                override fun onResponse(
                    call: Call<DetailUserResponse>,
                    response: Response<DetailUserResponse>,
                ) {
                    if (response.isSuccessful) {
                        user.postValue(response.body())
                    }
                }

                override fun onFailure(call: Call<DetailUserResponse>, t: Throwable) {
                    Log.d("Failure", t.message.toString())
                }
            })
    }

    fun getUserDetail(): LiveData<DetailUserResponse> {
        return user
    }

    fun checkFavorite(id: Int) = favoriteDao?.checkFavorite(id)

    fun addFavorite(username: String, id: Int, avatarUrl: String, htmlUrl: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val user = Favorite(
                username, id, avatarUrl, htmlUrl
            )
            favoriteDao?.addFavorite(user)
        }
    }

    fun removeFavorite(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            favoriteDao?.removeFavorite(id)
        }
    }

}