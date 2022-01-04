package fastcampus.aop.part2.mgr_villa.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class VillaUsers(
    @PrimaryKey val mailAddress: String,
    @ColumnInfo(name = "roomNumber") val roomNumber: String,
    @ColumnInfo(name = "userName") val userName: String,
    @ColumnInfo(name = "passWord") val passWord: String,
    @ColumnInfo(name = "phoneNumber") val phoneNumber: String,
    @ColumnInfo(name = "userType") val userType: String
)