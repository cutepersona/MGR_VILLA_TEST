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
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "costId") var costId: String = "",
    @ColumnInfo(name = "roomNumber") var roomNumber: String= "",
    @ColumnInfo(name = "totalCost") var totalCost: Int = 0,
    @ColumnInfo(name = "costYear") var costYear: String = "",
    @ColumnInfo(name = "costMonth") var costMonth: String = "",
    @ColumnInfo(name = "useTon") var useTon: Float = 0.0f,
    @ColumnInfo(name = "costTon") var costTon: Int = 0,
    @ColumnInfo(name = "totalUseTon") var totalUseTon: Int = 0,
    @ColumnInfo(name = "costClean") var costClean: Int = 0,
    @ColumnInfo(name = "costUsun") var costUsun: Int = 0,
    @ColumnInfo(name = "costMgr") var costMgr: Int = 0,
    @ColumnInfo(name = "costStatus") var costStatus: String? = "",
    @ColumnInfo(name = "villaAddr") var villaAddr: String = ""
)