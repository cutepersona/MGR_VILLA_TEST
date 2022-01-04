package fastcampus.aop.part2.mgr_villa.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import fastcampus.aop.part2.mgr_villa.model.VillaUsers

@Dao
interface VillaUserDao {


    // Insert
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(villaUsers: VillaUsers)

    // Delete
    @Query("DELETE FROM VillaUsers")
    fun deleteAll()

    @Delete
    fun delete(villaUsers: VillaUsers)

    // Update
    @Query("UPDATE VillaUsers SET passWord = :pw WHERE mailAddress = :mail")
    fun updatePW(mail: String, pw: String)


    // Select
    @Query("SELECT * FROM VillaUsers")
    fun getAll(): LiveData<List<VillaUsers>>

    // 아이디 찾기
    @Query("SELECT * FROM VillaUsers WHERE phoneNumber = :arg0")
    fun getUserId(arg0: String): VillaUsers

    // 이름 찾기
    @Query("SELECT * FROM VillaUsers WHERE mailAddress = :email")
    fun getUser(email: String): VillaUsers

    // 아이디 체크
    @Query("SELECT EXISTS (SELECT * FROM VillaUsers WHERE mailAddress = :email AND phoneNumber = :phoneNum)")
    fun isUserId(email: String, phoneNum: String): Int

    // 로그인 체크
    @Query("SELECT * FROM VillaUsers WHERE mailAddress = :email AND passWord = :pw")
    fun userLogin(email: String, pw: String): VillaUsers

}