package fastcampus.aop.part2.mgr_villa.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(primaryKeys= [ "villaAddr", "roomNumber" ],
    tableName = "VillaTenant",
    foreignKeys = arrayOf(
        ForeignKey(
            entity = VillaInfo::class,
            parentColumns = arrayOf("villaAddress"),
            childColumns = arrayOf("villaAddr"),
            onDelete = ForeignKey.CASCADE
        )
    )
)
data class VillaTenant (
    @ColumnInfo(name = "villaAddr") var villaAddr: String,
    @ColumnInfo(name = "roomNumber") var roomNumber: String,
    @ColumnInfo(name = "tenantEmail") var tenantEmail: String?,
    @ColumnInfo(name = "tenantContractDate") var tenantContractDate: String?,
    @ColumnInfo(name = "tenantLeaveDate") var tenantLeaveDate: String?,
    @ColumnInfo(name = "tenantStatus") var tenantStatus: String?
)