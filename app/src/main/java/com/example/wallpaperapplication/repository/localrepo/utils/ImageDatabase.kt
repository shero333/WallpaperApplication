package com.example.wallpaperapplication.repository.localrepo.utils

import android.content.Context
import android.os.AsyncTask
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.wallpaperapplication.models.RoomImageModel

@Database(entities = [RoomImageModel::class], version = 1, exportSchema = false)
abstract class ImageDatabase : RoomDatabase() {
    // below line is to create
    // abstract variable for dao.
    abstract fun Dao(): DAO

    // we are creating an async task class to perform task in background.
    private class PopulateDbAsyncTask internal constructor(instance: ImageDatabase?) :
        AsyncTask<Void?, Void?, Void?>() {
        init {
            val dao = instance!!.Dao()
        }


        @Deprecated("Deprecated in Java", ReplaceWith("null"))
        override fun doInBackground(vararg p0: Void?): Void? {
            return null
        }
    }

    companion object {
        // below line is to create instance
        // for our database class.
        private var instance: ImageDatabase? = null

        // on below line we are getting instance for our database.
        @Synchronized
        fun getInstance(context: Context): ImageDatabase? {

            // below line is to create a callback for our room database.
            val roomCallback: Callback = object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // this method is called when database is created
                    // and below line is to populate our data.
                    PopulateDbAsyncTask(instance).execute()
                }
            }
            if (instance == null) {
                // if the instance is null we
                // are creating a new instance
                instance = databaseBuilder(context.applicationContext, ImageDatabase::class.java, "image_database")
                    .allowMainThreadQueries()
                    .build()
            }

            return instance
        }
    }
}