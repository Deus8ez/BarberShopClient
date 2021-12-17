package com.example.lab2.models

import android.os.Parcel
import android.os.Parcelable

class Service() : Parcelable {

    //    val id: Int, val name: String, val mastery: String
    var id: Int = 0
    var name: String = ""
    var price: Float = 0f

    constructor(parcel: Parcel) : this(
    ) {
        id = parcel.readInt()
        name = parcel.readString() ?: ""
        price = parcel.readFloat() ?: 0f
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeFloat(price)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Service> {
        override fun createFromParcel(parcel: Parcel): Service {
            return Service(parcel)
        }

        override fun newArray(size: Int): Array<Service?> {
            return arrayOfNulls(size)
        }
    }
}