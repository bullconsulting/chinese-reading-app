package com.bullconsulting.chinesereader.data.local

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Loads the bundled CC-CEDICT file into the database on first launch.
 * Seeds two things: the main `dictionary` table (exact lookups) and the
 * `dictionary_fts` index (English search). Each reseeds only if incomplete,
 * so an interrupted run — or a newly-added index — self-heals on next launch.
 */
@Singleton
class DictionarySeeder @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: DictionaryDao,
    private val ftsDao: DictionaryFtsDao,
) {
    suspend fun seedIfNeeded() {
        seedDictionary()
        seedFts()
    }

    private suspend fun seedDictionary() {
        if (dao.count() >= MIN_EXPECTED_ENTRIES) return
        Log.i(TAG, "Seeding dictionary ...")
        dao.clear()
        var inserted = 0
        forEachBatch { batch ->
            dao.insertAll(batch)
            inserted += batch.size
        }
        Log.i(TAG, "Dictionary seeded: $inserted entries")
    }

    private suspend fun seedFts() {
        if (ftsDao.count() >= MIN_EXPECTED_ENTRIES) return
        Log.i(TAG, "Seeding dictionary FTS index ...")
        ftsDao.clear()
        var inserted = 0
        forEachBatch { batch ->
            ftsDao.insertAll(
                batch.map { DictionaryFtsEntity(it.simplified, it.pinyin, it.definition) },
            )
            inserted += batch.size
        }
        Log.i(TAG, "Dictionary FTS seeded: $inserted entries")
    }

    /** Reads the bundled file and yields parsed entries in batches. */
    private suspend fun forEachBatch(action: suspend (List<DictionaryEntity>) -> Unit) {
        context.assets.open(ASSET).bufferedReader().use { reader ->
            val batch = ArrayList<DictionaryEntity>(BATCH_SIZE)
            var line = reader.readLine()
            while (line != null) {
                CedictParser.parseLine(line)?.let(batch::add)
                if (batch.size >= BATCH_SIZE) {
                    action(ArrayList(batch))
                    batch.clear()
                }
                line = reader.readLine()
            }
            if (batch.isNotEmpty()) action(batch)
        }
    }

    companion object {
        private const val ASSET = "cedict.txt"
        private const val BATCH_SIZE = 2000
        // CC-CEDICT has ~124k entries; well below this means an incomplete seed.
        private const val MIN_EXPECTED_ENTRIES = 100_000
        private const val TAG = "DictionarySeeder"
    }
}
