package fastcampus.aop.part2.mgr_villa.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "VillaTenantCost",
    foreignKeys = arrayOf(
        ForeignKey(
            entity = VillaInfo::class,
            parentColumns = arrayOf("villaAddress"),
            childColumns = arrayOf("villaAddr"),
            onDelete = ForeignKey.CASCADE
        )
    )
)

data class VillaTenantCost (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "costId") var costId: Long?,
    @ColumnInfo(name = "roomNumber") var roomNumber: String,
    @ColumnInfo(name = "costYear") var costYear: String,
    @ColumnInfo(name = "costMonth") var costMonth: String,
    @ColumnInfo(name = "useTon") var useTon: Float,
    @ColumnInfo(name = "costTon") var costTon: Int,
    @ColumnInfo(name = "totalUseTon") var totalUseTon: Int,
    @ColumnInfo(name = "costClean") var costClean: Int,
    @ColumnInfo(name = "costUsun") var costUsun: Int,
    @ColumnInfo(name = "costMgr") var costMgr: Int,
    @ColumnInfo(name = "costStatus") var costStatus: String?,
    @ColumnInfo(name = "villaAddr") var villaAddr: String
)