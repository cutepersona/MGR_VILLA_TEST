package fastcampus.aop.part2.mgr_villa.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import fastcampus.aop.part2.mgr_villa.dao.VillaInfoDao
import fastcampus.aop.part2.mgr_villa.model.VillaInfo

@Database(entities = arrayOf(VillaInfo::class), version = 2, exportSchema = false)
abstract class VillaInfoHelper : RoomDatabase() {

    abstract fun VillaInfoDao(): VillaInfoDao

    companion object {
        private var instance: VillaInfoHelper? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE 'VillaInfo' ADD COLUMN 'roomNumber' TEXT NOT NULL default ''")
            }
        }


        @Synchronized
        fun getInstance(context: Context): VillaInfoHelper? {

            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    VillaInfoHelper::class.java,
                    "villaInfo_database"
                )
//                    .allowMainThreadQueries()
                    .addMigrations(VillaInfoHelper.MIGRATION_1_2)
                    .build()
            }

            return instance
        }

    }
}
