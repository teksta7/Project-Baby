package com.teksta.projectparent.db

import com.squareup.sqldelight.db.SqlDriver
import com.teksta.projectparent.db.AppDatabase as AppDatabaseGenerated

expect fun createDriver(context: Any): SqlDriver

class AppDatabaseWrapper(context: Any) {
    private val driver = createDriver(context)
    val database = AppDatabaseGenerated(driver)
    
    val bottleFeedQueries: BottleFeedQueries
        get() = database.bottleFeedQueries
} 