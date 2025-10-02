package com.example.vaultofluck.testing

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.vaultofluck.data.local.GameDatabase
import org.junit.rules.ExternalResource

class InMemoryDbRule : ExternalResource() {
    lateinit var database: GameDatabase
        private set

    override fun before() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, GameDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    override fun after() {
        database.close()
    }
}
