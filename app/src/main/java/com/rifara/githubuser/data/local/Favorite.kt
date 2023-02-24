package com.rifara.githubuser.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "favorite")
data class Favorite(

    @field:SerializedName("login")
    val login: String,

    @field:SerializedName("id")
    @PrimaryKey val id: Int,

    @field:SerializedName("avatar_url")
    val avatarUrl: String,

    @field:SerializedName("html_url")
    val htmlUrl: String

) : Serializable