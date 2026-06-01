package com.example.coffeeshop.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffeeshop.R
import com.example.coffeeshop.adapter.CategoryAdapter
import com.example.coffeeshop.adapter.OffersAdapter
import com.example.coffeeshop.adapter.PopularAdapter
import com.example.coffeeshop.databinding.ActivityMainBinding
import com.example.coffeeshop.model.ItemsModel
import com.example.coffeeshop.viewmodel.MainViewModel

class MainActivity : BaseActivity() {

    private val viewModel: MainViewModel by viewModels()
    private var selectedCategoryId = 0
    private var searchQuery = ""
    private val allPopularItems = mutableListOf<ItemsModel>()
    private var popularAdapter: PopularAdapter? = null
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.bottomNavigation.background = null

        initCategory()
        initPopular()
        initOffer()
        initSearch()
        bottomMenu()
    }

    private fun initSearch() {
        binding.editTextText.addTextChangedListener {
            searchQuery = it.toString().trim()
            popularAdapter?.updateItems(filteredPopularItems())
        }
    }

    private fun bottomMenu() {
        binding.cartBtn.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                R.id.home -> {
                    // Already at home
                    true
                }
                R.id.contact -> {
                    startActivity(Intent(this, ContactActivity::class.java))
                    true
                }
                R.id.settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun initOffer() {
        binding.progressBarOffer.visibility = View.VISIBLE
        viewModel.offer.observe(this) {
            binding.recyclerViewOffer.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.recyclerViewOffer.adapter = OffersAdapter(it)
            binding.progressBarOffer.visibility = View.GONE
        }
        viewModel.loadOffer()
    }

    private fun initPopular() {
        binding.progressBarPopular.visibility = View.VISIBLE
        viewModel.popular.observe(this) {
            allPopularItems.clear()
            allPopularItems.addAll(it)

            binding.recyclerViewPopular.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            popularAdapter = PopularAdapter(filteredPopularItems().toMutableList())
            binding.recyclerViewPopular.adapter = popularAdapter
            binding.progressBarPopular.visibility = View.GONE
        }
        viewModel.loadPopular()
    }

    private fun initCategory() {
        binding.progressBarCategory.visibility = View.VISIBLE
        viewModel.category.observe(this) {
            binding.recyclerViewCategory.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.recyclerViewCategory.adapter = CategoryAdapter(it) { category ->
                selectedCategoryId = category.id
                popularAdapter?.updateItems(filteredPopularItems())
            }
            binding.progressBarCategory.visibility = View.GONE
        }
        viewModel.loadCategory()
    }

    private fun filteredPopularItems(): List<ItemsModel> {
        return allPopularItems.filter { item ->
            val matchesCategory = selectedCategoryId == 0 || item.categoryId == selectedCategoryId
            val matchesSearch = searchQuery.isEmpty() || item.title.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }
}
