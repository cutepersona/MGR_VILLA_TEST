package fastcampus.aop.part2.mgr_villa.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import fastcampus.aop.part2.mgr_villa.model.VillaInfo
import fastcampus.aop.part2.mgr_villa.model.VillaNotice
import fastcampus.aop.part2.mgr_villa.model.VillaUsers

@Dao
interface VillaNoticeDao {

    //-----------------------------------------VillaNotice------------------------------------------

    // Insert
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun villaNoticeInsert(notice: VillaNotice)

    // Select
    @Query("SELECT * FROM VillaNotice WHERE villaAddr = :villaAddress")
    fun getAllNotice(villaAddress: String): List<VillaNotice>

    @Query("SELECT * FROM VillaNotice")
    fun getNoticeAll(): LiveData<List<VillaNotice>>

    @Query("SELECT * FROM VillaNotice WHERE noticeNo= :noticeNo")
    fun getNotice(noticeNo: Long): VillaNotice

    // Update
    @Query("UPDATE VillaNotice SET noticeTitle = :noticeTitle, noticeContent = :noticeContent WHERE noticeNo = :noticeNo")
    fun updateNotice(noticeTitle: String, noticeContent: String, noticeNo: Long)

    // Delete
    @Query("DELETE FROM VillaNotice WHERE noticeNo = :noticeNo")
    fun deleteNotice(noticeNo: Long)


    //-----------------------------------------VillaUsers------------------------------------------

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
    fun getUserAll(): LiveData<List<VillaUsers>>

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



    //-----------------------------------------VillaInfo------------------------------------------

    // Insert
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(villaInfo: VillaInfo)

    // Delete
    @Query ("DELETE FROM villaInfo WHERE villaAddress = :villaAddress")
    fun delete(villaAddress: String)

    @Query("DELETE FROM VillaInfo")
    fun deleteVillaInfoAll()

    //Select
    @Query("SELECT * FROM VillaInfo")
    fun getVillaInfoAll(): LiveData<List<VillaInfo>>

    @Query("SELECT EXISTS (SELECT * FROM villaInfo WHERE mailAddress = :mailAddress)")
    fun isVilla(mailAddress: String): Int

    @Query("SELECT * FROM VillaInfo WHERE mailAddress = :email")
    fun getVillaInfo(email:String) : VillaInfo

}