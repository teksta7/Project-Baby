package com.teksta.projectparent

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun startBottleFeedForegroundService(babyName: String, elapsed: Int, total: Int, average: Int)
expect fun updateBottleFeedForegroundService(elapsed: Int, total: Int, average: Int)