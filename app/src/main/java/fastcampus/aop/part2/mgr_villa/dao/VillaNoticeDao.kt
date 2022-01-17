package fastcampus.aop.part2.mgr_villa.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import fastcampus.aop.part2.mgr_villa.model.*

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

    //-----------------------------------------VillaCost------------------------------------------

    // Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun standardCostInsert(standardCost: StandardCost)

    // Select
    @Query("SELECT * FROM StandardCost WHERE villaAddr= :villaAddress")
    fun getStandardCost(villaAddress: String) : StandardCost

    @Query("SELECT EXISTS (SELECT * FROM StandardCost WHERE villaAddr = :villaAddress)")
    fun isConstCost(villaAddress: String): Int


    //-----------------------------------------VillaTenant------------------------------------------

    // Insert
    @Insert
    fun villaTenantInsert(villaTenant: VillaTenant)

    // Select
    @Query("SELECT * FROM VillaTenant WHERE villaAddr =:villaAddress ORDER BY roomNumber")
    fun getAllTenantRooms(villaAddress: String) : List<VillaTenant>

    @Query("SELECT villaTenantCount FROM VillaInfo WHERE villaAddress =:villaAddr")
    fun checkTenantCount(villaAddr: String) : Int

    // Update
    @Query("UPDATE VillaTenant SET roomNumber= :newRoomNum WHERE villaAddr = :villaAddr AND roomNumber = :beforeRoomNum")
    fun villaRoomNumberUpdate(newRoomNum: String, villaAddr: String, beforeRoomNum: String)

    // 퇴거하기
    @Query("UPDATE VillaTenant SET tenantEmail = '', tenantContractDate = '', tenantLeaveDate = '' WHERE villaAddr = :villaAddr AND roomNumber = :roomNumber")
    fun leaveTenant(villaAddr: String, roomNumber: String)

    // 입주시키기
    @Query("UPDATE VillaTenant SET tenantEmail = :tenantEmail, tenantContractDate = :contractDate, tenantLeaveDate = :leaveDate WHERE villaAddr = :villaAddr AND roomNumber = :roomNumber")
    fun intoTenant(tenantEmail: String, contractDate: String, leaveDate: String, villaAddr: String, roomNumber: String)

    // Delete
    @Query("DELETE FROM VillaTenant WHERE villaAddr =:villaAddr AND roomId =:roomId")
    fun deleteTenant(villaAddr: String, roomId: Long)

    //-----------------------------------------VillaAccount------------------------------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun villaAccountInsert(villaAccount: VillaAccount)

    // Select
    @Query("SELECT * FROM VillaAccount WHERE villaAddr =:villaAddress ORDER BY accountId")
    fun getAllVillaAccounts(villaAddress: String) : List<VillaAccount>

    @Query("SELECT * FROM VillaAccount WHERE accountId =:accountId")
    fun getVillaAccount(accountId: Long) : VillaAccount

    @Query("SELECT EXISTS (SELECT * FROM VillaAccount WHERE villaAddr = :villaAddress)")
    fun isAccount(villaAddress: String): Int

    // Update
    @Query("UPDATE VillaAccount SET bankName =:bankName, accountHolder =:accountHolder, accountNumber =:accountNumber WHERE villaAddr =:villaAddress AND accountId =:accountId")
    fun updateAccount(bankName: String, accountHolder: String, accountNumber: String, villaAddress: String, accountId: Long)

    @Query("UPDATE VillaAccount SET favorite =:favorite WHERE villaAddr =:villaAddress AND accountId =:accountId")
    fun updateFavorite(favorite: String, villaAddress: String, accountId: Long)

    @Query("UPDATE VillaAccount SET favorite =:favorite WHERE villaAddr =:villaAddress")
    fun updateNoneFavorite(favorite: String, villaAddress: String)


    // Delete
    @Query("DELETE FROM VillaAccount WHERE villaAddr =:villaAddress AND accountId =:accountId")
    fun deleteAccount(villaAddress: String, accountId: Long)


}