package fastcampus.aop.part2.mgr_villa.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import fastcampus.aop.part2.mgr_villa.model.VillaInfo

@Dao
interface VillaInfoDao {

    // Insert
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(villaInfo: VillaInfo)

    // Delete
    @Query ("DELETE FROM villaInfo WHERE villaAddress = :villaAddress")
    fun delete(villaAddress: String)

    @Query("DELETE FROM VillaInfo")
    fun deleteAll()

    //Select
    @Query("SELECT * FROM VillaInfo")
    fun getAll(): LiveData<List<VillaInfo>>

    @Query("SELECT EXISTS (SELECT * FROM villaInfo WHERE mailAddress = :mailAddress)")
    fun isVilla(mailAddress: String): Int

    @Query("SELECT * FROM VillaInfo WHERE mailAddress = :email")
    fun getVillaInfo(email:String) :VillaInfo

}