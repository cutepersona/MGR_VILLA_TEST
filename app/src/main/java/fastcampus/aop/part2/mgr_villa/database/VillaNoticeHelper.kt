package fastcampus.aop.part2.mgr_villa.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import fastcampus.aop.part2.mgr_villa.dao.VillaNoticeDao
import fastcampus.aop.part2.mgr_villa.model.VillaInfo
import fastcampus.aop.part2.mgr_villa.model.VillaNotice
import fastcampus.aop.part2.mgr_villa.model.VillaUsers

@Database(entities = arrayOf(VillaUsers::class, VillaInfo::class, VillaNotice::class), version = 1, exportSchema = false)
abstract class VillaNoticeHelper: RoomDatabase() {

    abstract fun VillaNoticeDao(): VillaNoticeDao

    companion object {
        private var instance: VillaNoticeHelper? = null

        @Synchronized
        fun getInstance(context: Context): VillaNoticeHelper? {

            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    VillaNoticeHelper::class.java,
                    "villa_database"
                )
//                    .allowMainThreadQueries()
                    .build()
            }

            return instance
        }

    }

}