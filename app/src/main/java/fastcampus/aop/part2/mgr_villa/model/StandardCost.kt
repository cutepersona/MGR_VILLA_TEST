package fastcampus.aop.part2.mgr_villa.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "StandardCost",
    foreignKeys = arrayOf(
        ForeignKey(
            entity = VillaInfo::class,
            parentColumns = arrayOf("villaAddress"),
            childColumns = arrayOf("villaAddr"),
            onDelete = ForeignKey.CASCADE
        )
    )
)

data class StandardCost (
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "villaAddr") var villaAddr: String,
    @ColumnInfo(name = "tonCost") var tonCost: Int,
    @ColumnInfo(name = "cleanCost") var cleanCost: Int,
    @ColumnInfo(name = "usunCost") var usunCost: Int,
    @ColumnInfo(name = "mgrCost") var mgrCost: Int
)