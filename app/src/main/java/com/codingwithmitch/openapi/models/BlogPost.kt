package com.codingwithmitch.openapi.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Local data class for modeling: https://open-api.xyz/ blog objects
see example: https://gist.github.com/mitchtabian/93f287bd1370e7a1ad3c9588b0b22e3d
 * Docs: https://open-api.xyz/api/
 */

@Entity(tableName = "blog_post")
data class BlogPost(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "pk")
    var id: Int,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "slug")
    var slug: String,

    @ColumnInfo(name = "body")
    var body: String,

    @ColumnInfo(name = "image")
    var image: String,

    @ColumnInfo(name = "date_updated")
    var dateUpdated: Long,

    @ColumnInfo(name = "username")
    var userName: String


) {

    override fun toString(): String {
        return "BlogPost(id=$id, " +
                "title='$title', " +
                "slug='$slug', " +
                "image='$image', " +
                "date_updated=$dateUpdated, " +
                "username='$userName')"
    }
}