package fastcampus.aop.part2.mgr_villa.repository

import androidx.lifecycle.LiveData
import fastcampus.aop.part2.mgr_villa.dao.VillaUserDao
import fastcampus.aop.part2.mgr_villa.model.VillaUsers

class VillaUserRepository(private val villaUserDao: VillaUserDao) {

    val allVillaUsers: LiveData<List<VillaUsers>> = villaUserDao.getAll()

    suspend fun insert(villaUser: VillaUsers) {
        villaUserDao.insert(villaUser)
    }

    suspend fun deleteAll() {
        villaUserDao.deleteAll()
    }

    suspend fun delete(villaUser: VillaUsers) {
        villaUserDao.delete(villaUser)
    }

    suspend fun updatePW(mail: String, pw: String) {
        villaUserDao.updatePW(mail, pw)
    }

    suspend fun getUserId(arg0: String): VillaUsers {
        return villaUserDao.getUserId(arg0)
    }

    suspend fun isUserId(email: String, phoneNum: String): Int {
        return villaUserDao.isUserId(email, phoneNum)
    }

    suspend fun userLogin(email: String, pw: String): VillaUsers {
        return villaUserDao.userLogin(email, pw)
    }


}