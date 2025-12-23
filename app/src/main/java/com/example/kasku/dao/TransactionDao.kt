package com.example.kasku.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.kasku.entity.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT DISTINCT name FROM `Transaction`")
    suspend fun getAllName() : List<String>

    @Query("SELECT SUM(amount) FROM `Transaction` WHERE groupId = :groupId")
    suspend fun getTotalMoneyByGroup(groupId:Int) : Double?

    @Query("SELECT SUM(amount) FROM `Transaction` WHERE groupId = :groupId AND amount > 0")
    suspend fun getTotalIncome(groupId: Int) : Double?

    @Query("SELECT SUM(ABS(amount)) FROM `Transaction` WHERE groupId = :groupId AND amount < 0")
    suspend fun getTotalExpanse(groupId: Int) : Double?

    @Query("SELECT * FROM `Transaction` WHERE groupId = :groupId ORDER BY date DESC")
    fun getAllHistory(groupId: Int) : Flow<List<Transaction>>

    @Query("SELECT * FROM `Transaction` WHERE amount > 0 AND groupId = :groupId")
    fun getIncomeHistory(groupId: Int) : Flow<List<Transaction>>

    @Query("SELECT * FROM `Transaction` WHERE amount < 0 AND groupId = :groupId")
    fun getExpanseHistory(groupId: Int) : Flow<List<Transaction>>

    @Insert
    suspend fun insert(transaction: Transaction)

    @Update
    suspend fun update(transaction: Transaction)

}