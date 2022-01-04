package fastcampus.aop.part2.mgr_villa.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import fastcampus.aop.part2.mgr_villa.database.VillaUsersHelper
import fastcampus.aop.part2.mgr_villa.model.VillaUsers
import fastcampus.aop.part2.mgr_villa.repository.VillaUserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class VillaUserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VillaUserRepository

    val getAll: LiveData<List<VillaUsers>>

    init {
        val villaUserDao = VillaUsersHelper.getInstance(application)?.VillaUserDao()
        repository = villaUserDao?.let { VillaUserRepository(it) }!!
        getAll = repository.allVillaUsers
    }

    fun insert(villaUsers: VillaUsers) = viewModelScope.launch {
        repository.insert(villaUsers)
    }
//
//    fun delete(villaUsers: VillaUsers) = viewModelScope.launch {
//        repository.delete(villaUsers)
//    }
//
//    fun deleteAll() = viewModelScope.launch {
//        repository.deleteAll()
//    }
//
//    fun updatePW(mail: String, pw: String) = viewModelScope.launch {
//        repository.updatePW(mail, pw)
//    }
//
//    fun getUserId(arg0: String): Job = viewModelScope.launch {
//        repository.getUserId(arg0)
//    }
//
//    fun isUserId(email: String, phoneNum: String): Job = viewModelScope.launch {
//        repository.isUserId(email, phoneNum)
//    }
//
//    fun userLogin(email: String, pw: String): Job = viewModelScope.launch {
//        repository.userLogin(email, pw)
//    }
}