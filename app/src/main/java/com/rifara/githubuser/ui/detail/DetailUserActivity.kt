package com.rifara.githubuser.ui.detail

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.rifara.githubuser.R
import com.rifara.githubuser.databinding.ActivityDetailUserBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailUserBinding
    private lateinit var viewModel: DetailUserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = intent.getStringExtra(EXTRA_USERNAME)
        val id = intent.getIntExtra(EXTRA_ID, 0)
        val avatarUrl = intent.getStringExtra(EXTRA_URL)
        val htmlUrl = intent.getStringExtra(EXTRA_HTML_URL)

        val bundle = Bundle()
        bundle.putString(EXTRA_USERNAME, username)

        supportActionBar?.title = getString(R.string.detail_user, username)

        viewModel = ViewModelProvider(this)[DetailUserViewModel::class.java] // ...
        viewModel.setUserDetail(username)
        viewModel.getUserDetail().observe(this) {
            if (it != null) {
                binding.apply {
                    tvDetailUser.text = it.name
                    tvDetailUsername.text = username
                    tvDetailRepository.text = getString(R.string.repo, it.publicRepos)
                    tvDetailFollowers.text = getString(R.string.followers, it.followers)
                    tvDetailFollowing.text = getString(R.string.following, it.following)
                    tvDetailCompany.text = it.company
                    tvDetailLocation.text = it.location
                    Glide.with(this@DetailUserActivity).load(it.avatarUrl).into(detailAvatar)
                }
            }
        }

        var isCheckedFavorite = false
        CoroutineScope(Dispatchers.IO).launch {
            val count = viewModel.checkFavorite(id)

            withContext(Dispatchers.Main) {
                if (count != null) {
                    if (count > 0) {
                        binding.toggleButtonFavorite.isChecked = true
                        isCheckedFavorite = true
                    } else {
                        binding.toggleButtonFavorite.isChecked = false
                        isCheckedFavorite = false
                    }
                }
            }

        }

        binding.toggleButtonFavorite.setOnClickListener {
            isCheckedFavorite = !isCheckedFavorite
            if (isCheckedFavorite) {
                viewModel.addFavorite(
                    username.toString(), id, avatarUrl.toString(), htmlUrl.toString()
                )
            } else {
                viewModel.removeFavorite(id)
            }
            binding.toggleButtonFavorite.isChecked = isCheckedFavorite
        }

        val sectionPagerAdapter = SectionPagerAdapter(this, bundle)
        binding.apply {
            viewPager.adapter = sectionPagerAdapter
            TabLayoutMediator(tabs, viewPager) { tab, position ->
                tab.text = resources.getString(TAB_TITLES[position])
            }.attach()
            supportActionBar?.elevation = 0f
        }

    }


    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1, R.string.tab_text_2
        )
        const val EXTRA_USERNAME = "extra_username"
        const val EXTRA_ID = "extra_id"
        const val EXTRA_URL = "extra_url"
        const val EXTRA_HTML_URL = "extra_html_url"
    }

}
