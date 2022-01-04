package fastcampus.aop.part2.mgr_villa.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import fastcampus.aop.part2.mgr_villa.dao.VillaUserDao
import fastcampus.aop.part2.mgr_villa.model.VillaUsers

@Database(entities = arrayOf(VillaUsers::class), version = 3, exportSchema = false)
abstract class VillaUsersHelper : RoomDatabase() {

    abstract fun VillaUserDao(): VillaUserDao

    companion object {
        private var instance: VillaUsersHelper? = null

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE 'VillaUsers' ADD COLUMN 'passWord' TEXT NOT NULL default ''")

            }
        }

        @Synchronized
        fun getInstance(context: Context): VillaUsersHelper? {

            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    VillaUsersHelper::class.java,
                    "villaUser_database"
                )
//                    .allowMainThreadQueries()
                    .addMigrations(MIGRATION_2_3)
                    .build()
            }

            return instance
        }

    }


}