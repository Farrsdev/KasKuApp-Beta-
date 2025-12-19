package com.example.kasku

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kasku.dao.GroupDao
import com.example.kasku.dao.TransactionDao
import com.example.kasku.entity.Group
import com.example.kasku.entity.Transaction

@Database(
    entities = [Group::class, Transaction::class],
    version = 1
)
abstract class AppDb: RoomDatabase() {
    abstract fun groupDao(): GroupDao
    abstract fun transactionDao() : TransactionDao

    companion object{
        @Volatile
        private var INSTANCE: AppDb? =  null

        fun getDb(context: Context) : AppDb{
            return INSTANCE ?: synchronized(this){
                val inst = Room.databaseBuilder(
                    context = context,
            AppDb::class.java,
                    "KasKuDb"
                ).fallbackToDestructiveMigration().build()

                INSTANCE = inst
                inst
            }
        }
    }
}