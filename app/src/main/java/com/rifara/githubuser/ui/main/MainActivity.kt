package com.rifara.githubuser.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.rifara.githubuser.R
import com.rifara.githubuser.data.model.User
import com.rifara.githubuser.databinding.ActivityMainBinding
import com.rifara.githubuser.ui.detail.DetailUserActivity
import com.rifara.githubuser.ui.favorite.FavoriteActivity
import com.rifara.githubuser.ui.settings.SettingActivity
import com.rifara.githubuser.ui.settings.SettingPreferences
import com.rifara.githubuser.ui.settings.SettingViewModel
import com.rifara.githubuser.ui.settings.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: UserViewModel
    private lateinit var adapter: UserAdapter

    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            this, R.anim.rotate_open_fab
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            this, R.anim.rotate_close_fab
        )
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this, R.anim.from_bottom_anime
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this, R.anim.to_bottom_anime
        )
    }
    private var clicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.github_users_search)

        adapter = UserAdapter()

        // Ketika Item diklik
        adapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                Intent(this@MainActivity, DetailUserActivity::class.java).also {
                    Toast.makeText(this@MainActivity, data.login, Toast.LENGTH_SHORT).show()
                    // mengirimkan objek tersebut
                    it.putExtra(DetailUserActivity.EXTRA_USERNAME, data.login)
                    it.putExtra(DetailUserActivity.EXTRA_ID, data.id) // ...
                    it.putExtra(DetailUserActivity.EXTRA_HTML_URL, data.htmlUrl) // ...
                    it.putExtra(DetailUserActivity.EXTRA_URL, data.avatarUrl) // ...
                    startActivity(it)
                }
            }
        })

        adapter.notifyDataSetChanged()
        viewModel = ViewModelProvider(
            this, ViewModelProvider.NewInstanceFactory()
        )[UserViewModel::class.java]

        binding.apply {
            rvUser.layoutManager = LinearLayoutManager(this@MainActivity)
            rvUser.setHasFixedSize(true)
            rvUser.adapter = adapter

            // Ketika Button Search ditekan
            btnSearch.setOnClickListener {
                Toast.makeText(this@MainActivity, etQuery.text.toString(), Toast.LENGTH_SHORT)
                    .show()
                searchUser()
            }

            // Ketika Enter ditekan
            etQuery.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    Toast.makeText(this@MainActivity, etQuery.text.toString(), Toast.LENGTH_SHORT)
                        .show()
                    searchUser()
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }

        }

        viewModel.getSearchUsers().observe(this) {
            if (it != null) {
                adapter.setList(it)
                showLoading(false)
                binding.lrImageText.visibility = View.GONE
                binding.rvUser.visibility = View.VISIBLE

                if (adapter.itemCount == 0) {
                    binding.lrImageText.visibility = View.VISIBLE
                    binding.imgLogo.setImageResource(R.drawable.octocat)
                    binding.homeTitle.text = getString(R.string.user_not_found)
                    binding.homeSubTitle.text = getString(R.string.search_other_user)

                }
            }
        }

        dayNightMode()

        // Material Floating Action Button
        binding.fabMenu.setOnClickListener {
            onAddButtonClicked()
        }
        binding.fabFavorite.setOnClickListener {
            Toast.makeText(this, "Favorite Button Click", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, FavoriteActivity::class.java))
        }
        binding.fabSetting.setOnClickListener {
            Toast.makeText(this, "Setting Button Click", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, SettingActivity::class.java))
        }

    }

    private fun onAddButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        setClickable(clicked)
        clicked = !clicked
    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            binding.fabFavorite.startAnimation(fromBottom)
            binding.fabSetting.startAnimation(fromBottom)
            binding.fabMenu.startAnimation(rotateOpen)
        } else {
            binding.fabFavorite.startAnimation(toBottom)
            binding.fabSetting.startAnimation(toBottom)
            binding.fabMenu.startAnimation(rotateClose)
        }
    }

    private fun setVisibility(clicked: Boolean) {
        if (!clicked) {
            binding.fabFavorite.visibility = View.VISIBLE
            binding.fabSetting.visibility = View.VISIBLE
        } else {
            binding.fabFavorite.visibility = View.INVISIBLE
            binding.fabSetting.visibility = View.INVISIBLE
        }
    }

    private fun setClickable(clicked: Boolean) {
        if (!clicked) {
            binding.fabFavorite.isClickable = true
            binding.fabSetting.isClickable = true
        } else {
            binding.fabFavorite.isClickable = false
            binding.fabSetting.isClickable = false
        }
    }

    // Setting Day/Night Mode From MainActivity
    private fun dayNightMode() {
        val pref = SettingPreferences.getInstance(dataStore)
        val settingViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[SettingViewModel::class.java]

        settingViewModel.getThemeSettings().observe(
            this
        ) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                settingViewModel.saveThemeSetting(true)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                settingViewModel.saveThemeSetting(false)
            }
        }
    }


    private fun searchUser() {
        binding.apply {
            val query = etQuery.text.toString()
            if (query.isEmpty()) return
            binding.lrImageText.visibility = View.GONE
            binding.rvUser.visibility = View.GONE
            showLoading(true)
            viewModel.setSearchUsers(query)
        }
    }

    private fun showLoading(state: Boolean) {
        if (state) { // True
            binding.progressBar.visibility = View.VISIBLE
        } else { // False
            binding.progressBar.visibility = View.GONE
        }
    }

}