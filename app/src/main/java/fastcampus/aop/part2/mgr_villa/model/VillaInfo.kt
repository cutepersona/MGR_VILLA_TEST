package fastcampus.aop.part2.mgr_villa.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity
//    (tableName = "villaInfo",
//            foreignKeys = [
//                ForeignKey(
//                        entity = VillaUsers::class,
//                        parentColumns = ["mailAddress"],
//                        childColumns = ["mailAddress"],
//                    onDelete = CASCADE
//                    )
//            ])
data class VillaInfo (

    @PrimaryKey val villaAddress: String,
    @ColumnInfo(name = "villaName") val villaName: String?,
    @ColumnInfo(name = "villaAlias") val villaAlias: String?,
    @ColumnInfo(name = "villaTenantCount") val villaTenantCount: String,
    @ColumnInfo(name = "villaParkCount") val villaParkCount: Int?,
    @ColumnInfo(name = "villaElevator") val villaElevator: Boolean,
    @ColumnInfo(name = "mailAddress") val mailAddress: String? = null,
    @ColumnInfo(name = "roomNumber") val roomNumber: String

)