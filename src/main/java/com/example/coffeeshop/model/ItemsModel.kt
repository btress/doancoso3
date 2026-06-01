package com.example.coffeeshop.model

import android.os.Parcel
import android.os.Parcelable

data class ItemsModel(
    var title: String = "",
    var description: String = "",
    var picUrl: ArrayList<String> = ArrayList(),
    var price: Double = 0.0,
    var rating: Double = 0.0,
    var numberInCart: Int = 0,
    var extra: String = "",
    var categoryId: Int = 0
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: ArrayList(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeStringList(picUrl)
        parcel.writeDouble(price)
        parcel.writeDouble(rating)
        parcel.writeInt(numberInCart)
        parcel.writeString(extra)
        parcel.writeInt(categoryId)
    }

    override fun describeContents(): Int = 0

    // Hàm lấy link ảnh đầu tiên và tự động dọn dẹp khoảng trắng
    fun getFirstImageUrl(): String {
        return if (picUrl.isNotEmpty()) picUrl[0].trim() else ""
    }

    companion object CREATOR : Parcelable.Creator<ItemsModel> {
        override fun createFromParcel(parcel: Parcel): ItemsModel = ItemsModel(parcel)
        override fun newArray(size: Int): Array<ItemsModel?> = arrayOfNulls(size)
    }
}
