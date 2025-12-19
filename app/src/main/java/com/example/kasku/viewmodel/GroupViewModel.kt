    package com.example.kasku.viewmodel

    import android.app.Application
    import androidx.lifecycle.AndroidViewModel
    import androidx.lifecycle.viewModelScope
    import com.example.kasku.AppDb
    import com.example.kasku.entity.Group
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.SharingStarted
    import kotlinx.coroutines.flow.asStateFlow
    import kotlinx.coroutines.flow.stateIn
    import kotlinx.coroutines.launch

    class GroupViewModel(application: Application) : AndroidViewModel(application = application ) {
        val groupDao = AppDb.getDb(application).groupDao()

        private val groupImg = listOf(
            "groupbg1",
            "groupbg2",
            "groupbg3"
        )
        val allGroup = groupDao.getAllGroup().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        val _group = MutableStateFlow<Group?>(null)
        val group = _group.asStateFlow()

        fun getGroupById(groupId:Int){
            viewModelScope.launch {
                val res = groupDao.getGroupById(groupId)
                _group.value = res
            }
        }

        fun addGroup(name:String){
            val randomImg = groupImg.random()

            val group = Group(
                name = name,
                image = randomImg
            )
            
            viewModelScope.launch {
                groupDao.insert(group)
            }
        }

        fun editGroup(group: Group) {
            viewModelScope.launch {
                groupDao.update(group)
            }
        }

        fun deleteGroup(group: Group){
            viewModelScope.launch {
                groupDao.delete(group)
            }
        }
    }