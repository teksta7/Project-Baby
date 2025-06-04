package com.teksta.projectparent.services

class PlatformContracts {
}

// For managing the Android equivalent of Live Activities (e.g., foreground service with notification)
expect class BottleFeedTrackerManager(contextAsAny: Any) {
    fun startTracking(babyName: String, estimatedEndTimeStamp: Long, startTimeStamp: Long)
    fun updateTracking(currentDurationSeconds: Int)
    fun stopTracking()
}

// For handling in-app review requests
expect class InAppReviewManager(activityHolderAsAny: Any) {
    fun requestReview()
}

// For controlling the screen's idle timer
expect class ScreenIdleManager(activityAsAny: Any) {
    fun keepScreenOn()
    fun allowScreenToDim()
}

expect class BottleNotificationController(contextHolderAsAny: Any) { // <<< MODIFIED to accept contextHolder
    suspend fun requestNotificationAccessByUser()
    // You might add other functions here later like:
    // suspend fun scheduleBottleNotification(minutesUntilNotification: Int, bottleID: String)
    // fun removeExistingNotifications(notificationIdentifier: String)
}
