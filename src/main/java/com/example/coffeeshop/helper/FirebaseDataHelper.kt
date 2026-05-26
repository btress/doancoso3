package com.example.coffeeshop.helper

import android.content.Context
import android.util.Log
import com.example.coffeeshop.model.CategoryModel
import com.example.coffeeshop.model.ItemsModel
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class FirebaseData(
    @SerializedName("Category")
    val categories: MutableList<CategoryModel> = mutableListOf(),
    @SerializedName("Items")
    val items: MutableList<ItemsModel> = mutableListOf(),
    @SerializedName("Offers")
    val offers: MutableList<ItemsModel> = mutableListOf()
)

class FirebaseDataHelper {
    private val database = FirebaseDatabase.getInstance(DATABASE_URL)

    fun pushSampleData(context: Context) {
        val data = loadLocalDatabase(context)
        database.getReference("Category").setValue(data.categories)
        database.getReference("Items").setValue(data.items)
        database.getReference("Offers").setValue(data.offers)
    }

    companion object {
        private const val TAG = "CoffeeApp"
        private const val DATABASE_FILE = "database_firebase.json"
        private const val DATABASE_URL = "https://cafeapp-c7d63-default-rtdb.firebaseio.com/"

        fun loadLocalDatabase(context: Context): FirebaseData {
            return try {
                val json = context.assets.open(DATABASE_FILE).bufferedReader().use { it.readText() }
                Gson().fromJson(json, FirebaseData::class.java) ?: FirebaseData()
            } catch (e: Exception) {
                Log.e(TAG, "Cannot read local Firebase database file", e)
                FirebaseData()
            }
        }
    }
}
