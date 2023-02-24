package com.rifara.githubuser.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.rifara.githubuser.R
import com.rifara.githubuser.data.local.Favorite
import com.rifara.githubuser.data.model.User
import com.rifara.githubuser.databinding.ActivityFavoriteBinding
import com.rifara.githubuser.ui.detail.DetailUserActivity
import com.rifara.githubuser.ui.main.UserAdapter

class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var adapter: UserAdapter
    private lateinit var viewModel: FavoriteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.favorites_list)

        adapter = UserAdapter()
        adapter.notifyDataSetChanged()

        viewModel = ViewModelProvider(this)[FavoriteViewModel::class.java]

        adapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                Intent(this@FavoriteActivity, DetailUserActivity::class.java).also {
                    it.putExtra(DetailUserActivity.EXTRA_USERNAME, data.login)
                    it.putExtra(DetailUserActivity.EXTRA_ID, data.id)
                    it.putExtra(DetailUserActivity.EXTRA_URL, data.avatarUrl)
                    it.putExtra(DetailUserActivity.EXTRA_HTML_URL, data.htmlUrl)
                    startActivity(it)
                }
            }
        })

        binding.apply {
            rvFavorite.setHasFixedSize(true)
            rvFavorite.layoutManager = LinearLayoutManager(this@FavoriteActivity)
            rvFavorite.adapter = adapter
        }

        viewModel.getFavorite()?.observe(this) {
            if (it != null) {
                val list = mapList(it)
                adapter.setList(list)
                binding.rvFavorite.visibility = View.VISIBLE
                binding.lrImageText.visibility = View.GONE

                if (adapter.itemCount == 0) {
                    binding.lrImageText.visibility = View.VISIBLE
                }
            }
        }

    }

    private fun mapList(users: List<Favorite>): ArrayList<User> {
        val listUser = ArrayList<User>()
        for (user in users) {
            val userMapped = User(
                user.login, user.id, user.avatarUrl, user.htmlUrl
            )
            listUser.add(userMapped)
        }
        return listUser
    }

}