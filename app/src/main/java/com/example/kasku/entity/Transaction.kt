package com.example.kasku.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Group::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = CASCADE
        )
    ],
    indices = [Index("groupId")]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    val name:String,
    val description:String,
    val date:String,
    val groupId:Int,
    val amount: Double,
)
