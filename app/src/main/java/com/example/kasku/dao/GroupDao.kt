package com.example.kasku.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.kasku.entity.Group
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {
    @Query("SELECT * FROM `Group`")
    fun getAllGroup() : Flow<List<Group>>

    @Query("SELECT * FROM `Group` WHERE id = :id")
    suspend fun getGroupById(id:Int) : Group?

    @Insert
    suspend fun insert(group: Group)

    @Update
    suspend fun update(group: Group)

    @Delete
    suspend fun delete(group: Group)

}