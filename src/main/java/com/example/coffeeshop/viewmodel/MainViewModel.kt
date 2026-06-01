package com.example.coffeeshop.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.coffeeshop.helper.FirebaseDataHelper
import com.example.coffeeshop.model.CategoryModel
import com.example.coffeeshop.model.ItemsModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private companion object {
        const val TAG = "CoffeeApp"
        const val DATABASE_URL = "https://cafeapp-c7d63-default-rtdb.firebaseio.com/"
    }

    private val firebaseDatabase = FirebaseDatabase.getInstance(DATABASE_URL)
    private val localDatabase by lazy {
        FirebaseDataHelper.loadLocalDatabase(getApplication())
    }

    private val _category = MutableLiveData<MutableList<CategoryModel>>()
    private val _popular = MutableLiveData<MutableList<ItemsModel>>()
    private val _offer = MutableLiveData<MutableList<ItemsModel>>()

    val category: LiveData<MutableList<CategoryModel>> = _category
    val popular: LiveData<MutableList<ItemsModel>> = _popular
    val offer: LiveData<MutableList<ItemsModel>> = _offer

    fun loadCategory() {
        firebaseDatabase.getReference("Category").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categories = mutableListOf<CategoryModel>()

                for (childSnapshot in snapshot.children) {
                    childSnapshot.getValue(CategoryModel::class.java)?.let { categories.add(it) }
                }

                if (categories.isEmpty()) {
                    Log.w(TAG, "Firebase Category is empty, loading local asset data")
                    categories.addAll(localDatabase.categories)
                }

                _category.value = categories
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Cannot load Category from Firebase: ${error.message}", error.toException())
                _category.value = localDatabase.categories
            }
        })
    }

    fun loadPopular() {
        firebaseDatabase.getReference("Items").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<ItemsModel>()

                for (childSnapshot in snapshot.children) {
                    parseItem(childSnapshot)?.let { items.add(it) }
                }

                if (items.isEmpty()) {
                    Log.w(TAG, "Firebase Items is empty, loading local asset data")
                    items.addAll(localDatabase.items)
                }

                _popular.value = items
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Cannot load Items from Firebase: ${error.message}", error.toException())
                _popular.value = localDatabase.items
            }
        })
    }

    fun loadOffer() {
        firebaseDatabase.getReference("Offers").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val offers = mutableListOf<ItemsModel>()

                for (childSnapshot in snapshot.children) {
                    parseItem(childSnapshot)?.let { offers.add(it) }
                }

                if (offers.isEmpty()) {
                    Log.w(TAG, "Firebase Offers is empty, loading local asset data")
                    offers.addAll(localDatabase.offers)
                }

                _offer.value = offers
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Cannot load Offers from Firebase: ${error.message}", error.toException())
                _offer.value = localDatabase.offers
            }
        })
    }

    private fun parseItem(childSnapshot: DataSnapshot): ItemsModel? {
        return try {
            val title = childSnapshot.child("title").asString()
            ItemsModel(
                title = title,
                description = childSnapshot.child("description").asString(),
                picUrl = childSnapshot.child("picUrl").asStringList().withWorkingFallback(title),
                price = childSnapshot.child("price").asDouble(),
                rating = childSnapshot.child("rating").asDouble(),
                numberInCart = childSnapshot.child("numberInCart").asInt(),
                extra = childSnapshot.child("extra").asString(),
                categoryId = childSnapshot.child("categoryId").asInt()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Cannot parse item ${childSnapshot.key}", e)
            null
        }
    }

    private fun DataSnapshot.asString(): String {
        return value?.toString()?.trim() ?: ""
    }

    private fun DataSnapshot.asDouble(): Double {
        return when (val data = value) {
            is Number -> data.toDouble()
            is String -> data.trim().toDoubleOrNull() ?: 0.0
            else -> 0.0
        }
    }

    private fun DataSnapshot.asInt(): Int {
        return when (val data = value) {
            is Number -> data.toInt()
            is String -> data.trim().toIntOrNull() ?: 0
            else -> 0
        }
    }

    private fun DataSnapshot.asStringList(): ArrayList<String> {
        val urls = ArrayList<String>()

        if (!exists()) return urls

        if (childrenCount > 0) {
            children.forEach { child ->
                child.value?.toString()?.cleanUrl()?.let { urls.add(it) }
            }
        } else {
            value?.toString()?.cleanUrl()?.let { urls.add(it) }
        }

        return urls
    }

    private fun String.cleanUrl(): String? {
        val cleaned = trim().removeSurrounding("\"").removeSurrounding("'").trim()
        return cleaned.takeIf { it.startsWith("http://") || it.startsWith("https://") }
    }

    private fun ArrayList<String>.withWorkingFallback(title: String): ArrayList<String> {
        val firstUrl = firstOrNull().orEmpty()
        if (firstUrl.isNotBlank() && !firstUrl.contains("project195-aa33b.appspot.com")) {
            return this
        }

        return arrayListOf(fallbackImageUrl(title))
    }

    private fun fallbackImageUrl(title: String): String {
        val normalizedTitle = title.lowercase()

        return when {
            "espresso" in normalizedTitle || "espersso" in normalizedTitle || "esspersso" in normalizedTitle ->
                "https://images.unsplash.com/photo-1510707577719-ae7c14805e3a?q=80&w=600"
            "latte" in normalizedTitle ->
                "https://images.unsplash.com/photo-1461023058943-07fcbe16d735?q=80&w=600"
            "americano" in normalizedTitle ->
                "https://images.unsplash.com/photo-1497935586351-b67a49e012bf?q=80&w=600"
            "affagato" in normalizedTitle || "affogato" in normalizedTitle ->
                "https://images.unsplash.com/photo-1563805042-7684c019e1cb?q=80&w=600"
            "offer" in normalizedTitle || "buy" in normalizedTitle ->
                "https://images.unsplash.com/photo-1509042239860-f550ce710b93?q=80&w=600"
            else ->
                "https://images.unsplash.com/photo-1572442388796-11668a67e53d?q=80&w=600"
        }
    }
}
