package com.teksta.projectparent.db

import android.content.Context
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.teksta.projectparent.db.AppDatabase

actual fun createDriver(context: Any): SqlDriver {
    val androidContext = context as Context
    return AndroidSqliteDriver(
        schema = AppDatabase.Schema,
        context = androidContext,
        name = "projectparent.db"
    )
}

private fun getApplicationContext(): Context {
    // This will be provided by the Android application
    // For now, we'll use a placeholder that will be resolved at runtime
    throw NotImplementedError("Application context must be provided")
} 