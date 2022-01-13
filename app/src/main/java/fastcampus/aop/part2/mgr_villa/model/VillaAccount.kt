package fastcampus.aop.part2.mgr_villa.model

import androidx.room.*
import androidx.room.ForeignKey.CASCADE


@Entity( tableName = "VillaAccount",
    foreignKeys = arrayOf(
        ForeignKey(
            entity = VillaInfo::class,
            parentColumns = arrayOf("villaAddress"),
            childColumns = arrayOf("villaAddr"),
            onDelete = CASCADE
        )
    )
)

data class VillaAccount(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "accountId") var accountId: Long? = 0,
    @ColumnInfo(name = "bankName") var bankName: String,
    @ColumnInfo(name = "accountHolder") var accountHolder: String,
    @ColumnInfo(name = "accountNumber") var accountNumber: String,
    @ColumnInfo(name = "favorite") var favorite: String,
    @ColumnInfo(name = "villaAddr") var villaAddr: String
)

