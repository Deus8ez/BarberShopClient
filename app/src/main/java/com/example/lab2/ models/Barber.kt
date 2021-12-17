package com.example.lab2.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.serialization.Serializable

@Serializable
data class Barber(var id: Int, var name: String, val mastery: String)

//    val id: Int, val name: String, val mastery: String
//    var id: Int = 0
//    var name: String = ""
//    var mastery: String = ""
//
//    init(val _id: Int, val _name: String, val _mastery: String) : this(
//    ) {
//        id = id
//        name = name
//        mastery = parcel.readString() ?: ""
//    }

//    override fun writeToParcel(parcel: Parcel, flags: Int) {
//        parcel.writeInt(id)
//        parcel.writeString(name)
//        parcel.writeString(mastery)
//    }
//
//    override fun describeContents(): Int {
//        return 0
//    }
//
//    companion object CREATOR : Parcelable.Creator<Barber> {
//        override fun createFromParcel(parcel: Parcel): Barber {
//            return Barber(parcel)
//        }
//
//        override fun newArray(size: Int): Array<Barber?> {
//            return arrayOfNulls(size)
//        }
//    }
//}