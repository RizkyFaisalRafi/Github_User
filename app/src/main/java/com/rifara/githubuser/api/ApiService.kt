package com.rifara.githubuser.api

import com.rifara.githubuser.BuildConfig
import com.rifara.githubuser.data.model.DetailUserResponse
import com.rifara.githubuser.data.model.User
import com.rifara.githubuser.data.model.UserResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

const val mySuperSecretKey = BuildConfig.KEY

interface ApiService {

    @GET("search/users")
    @Headers("Authorization: token $mySuperSecretKey")
    fun getSearchUser(
        @Query("q") query: String
    ): Call<UserResponse>

    @GET("users/{username}")
    @Headers("Authorization: token $mySuperSecretKey")
    fun getUserDetail(
        @Path("username") username: String?
    ): Call<DetailUserResponse>

    @GET("users/{username}/followers")
    @Headers("Authorization: token $mySuperSecretKey")
    fun getFollowers(
        @Path("username") username: String
    ): Call<ArrayList<User>>

    @GET("users/{username}/following")
    @Headers("Authorization: token $mySuperSecretKey")
    fun getFollowing(
        @Path("username") username: String
    ): Call<ArrayList<User>>

}