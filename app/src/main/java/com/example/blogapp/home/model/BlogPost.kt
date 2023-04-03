package com.example.blogapp.home.model

import android.os.Parcel
import android.os.Parcelable
import java.util.*

data class BlogPost(
    val title: String = "",
    val imageUrl: String = "",
    val creatorId: String = "",
    val creatorName: String = "",
    val createdAt: Date = Date(),
    val description: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        Date(parcel.readLong()),
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(imageUrl)
        parcel.writeString(creatorId)
        parcel.writeString(creatorName)
        parcel.writeLong(createdAt.time)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BlogPost> {
        override fun createFromParcel(parcel: Parcel): BlogPost {
            return BlogPost(parcel)
        }

        override fun newArray(size: Int): Array<BlogPost?> {
            return arrayOfNulls(size)
        }
    }
}
