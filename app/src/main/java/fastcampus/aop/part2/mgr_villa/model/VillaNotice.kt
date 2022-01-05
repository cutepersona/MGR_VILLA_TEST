package fastcampus.aop.part2.mgr_villa.model

import androidx.room.*
import androidx.room.ForeignKey.CASCADE


@Entity( tableName = "VillaNotice",
    foreignKeys = arrayOf(
        ForeignKey(
            entity = VillaInfo::class,
            parentColumns = arrayOf("villaAddress"),
            childColumns = arrayOf("villaAddr"),
            onDelete = CASCADE
        )
    )
)

data class VillaNotice(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "noticeNo") var noticeNo: Long? = 0,
    @ColumnInfo(name = "noticeTitle") var noticeTitle: String,
    @ColumnInfo(name = "noticeContent") var noticeContent: String,
    @ColumnInfo(name = "noticeDatetime") var noticeDatetime: String,
    @ColumnInfo(name = "villaAddr") var villaAddr: String
)
//{ constructor() : this(null, "", "", "","") }

