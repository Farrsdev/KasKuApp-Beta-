package com.example.kasku.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.kasku.AppDb
import com.example.kasku.entity.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    val transactionDao = AppDb.getDb(application).transactionDao()

    private val _nameInput = MutableStateFlow("")
    val nameInput = _nameInput.asStateFlow()

    private val _nameSuggestions = MutableStateFlow<List<String>>(emptyList())
    val nameSuggestion = _nameSuggestions.asStateFlow()

    private val _totalMoney = MutableStateFlow<Double?>(null)
    val totalMoney = _totalMoney.asStateFlow()

    private val _totalIncome = MutableStateFlow<Double?>(null)
    val totalIncome = _totalIncome.asStateFlow()

    private val _totalExpanse = MutableStateFlow<Double?>(null)
    val totalExpanse = _totalExpanse.asStateFlow()

    private val _groupId = MutableStateFlow(0)
    @OptIn(ExperimentalCoroutinesApi::class)
    val history: StateFlow<List<Transaction>> =
        _groupId
            .filterNotNull()
            .flatMapLatest { id ->
                transactionDao.getAllHistory(id)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    @OptIn(ExperimentalCoroutinesApi::class)
    val incomeHistory: StateFlow<List<Transaction>> = _groupId
        .filterNotNull()
        .flatMapLatest { id ->
            transactionDao.getIncomeHistory(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val expanseHistory: StateFlow<List<Transaction>> = _groupId
        .filterNotNull()
        .flatMapLatest { _groupId ->
            transactionDao.getExpanseHistory(_groupId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private var allNames = emptyList<String>()

    init {
        viewModelScope.launch {
            allNames = transactionDao.getAllName()
        }
    }

    fun addIncome(
        name:String,
        amount:Double,
        groupId:Int,
        date:String,
        description:String,
    ){
        viewModelScope.launch {
            transactionDao.insert(
                Transaction(
                    name = name,
                    amount = amount,
                    groupId = groupId,
                    date = date,
                    description = description
                )
            )
        }
    }

    fun addExpanse(
        name:String,
        amount:Double,
        groupId:Int,
        date:String,
        description:String
    ){
        viewModelScope.launch {
            transactionDao.insert(
                Transaction(
                    name = name,
                    amount = -amount,
                    groupId = groupId,
                    date = date,
                    description = description
                )
            )
        }
    }

    fun setGroupId(id:Int){
        viewModelScope.launch {
            _groupId.value = id
        }
    }

    fun getTotalMoney(groupId:Int) {
        viewModelScope.launch {
            val res =  transactionDao.getTotalMoneyByGroup(groupId)
            _totalMoney.value = res
        }
    }

    fun getTotalIncome(groupId: Int){
        viewModelScope.launch {
            val res = transactionDao.getTotalIncome(groupId)
            _totalIncome.value = res
        }
    }

    fun getTotalExpanse(groupId: Int){
        viewModelScope.launch {
            val res = transactionDao.getTotalExpanse(groupId)
            _totalExpanse.value = res
        }
    }

    fun onNameChange(newValue: String) {
        _nameInput.value = newValue

        _nameSuggestions.value =
            if (newValue.isBlank()) {
                emptyList()
            } else {
                allNames.filter {
                    it.contains(newValue, ignoreCase = true)
                }
            }
    }

    fun selectName(name: String) {
        _nameInput.value = name
        _nameSuggestions.value = emptyList()
    }
}