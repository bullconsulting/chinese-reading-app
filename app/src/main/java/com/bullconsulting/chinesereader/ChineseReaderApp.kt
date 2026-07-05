package com.bullconsulting.chinesereader

import android.app.Application
import android.util.Log
import com.bullconsulting.chinesereader.data.local.DictionarySeeder
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * The app's entry point. @HiltAndroidApp turns on dependency injection.
 * On launch it seeds the bundled dictionary (once) on a background thread.
 */
@HiltAndroidApp
class ChineseReaderApp : Application() {

    override fun onCreate() {
        super.onCreate()
        val seeder = EntryPointAccessors
            .fromApplication(this, SeederEntryPoint::class.java)
            .dictionarySeeder()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                seeder.seedIfNeeded()
            } catch (e: Exception) {
                Log.e("ChineseReaderApp", "Dictionary seeding failed", e)
            }
        }
    }

    /** Lets us pull the seeder out of Hilt from inside the Application class. */
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface SeederEntryPoint {
        fun dictionarySeeder(): DictionarySeeder
    }
}
