package com.example.wallpaperapplication.repository.localrepo.utils

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseInjection {
    @Singleton
    @Provides
    fun provideDatabase(context: Application): DAO {
        val imageDatabase: ImageDatabase? = ImageDatabase.getInstance(context)
        return imageDatabase!!.Dao()
    }
}