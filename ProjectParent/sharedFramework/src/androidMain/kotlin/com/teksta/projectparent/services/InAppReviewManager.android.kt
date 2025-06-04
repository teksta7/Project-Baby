package com.teksta.projectparent.services

import android.app.Activity
import android.util.Log
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.ReviewManager

actual class InAppReviewManager actual constructor(activityHolderAsAny: Any) { // <<< MODIFIED CONSTRUCTOR
    private val activity: Activity = activityHolderAsAny as Activity // Cast to Android Activity
    private val reviewManager: ReviewManager = ReviewManagerFactory.create(activity)

    actual fun requestReview() {
        val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                val flow = reviewManager.launchReviewFlow(activity, reviewInfo)
                flow.addOnCompleteListener { _ ->
                    Log.d("InAppReviewManager", "Review flow finished.")
                }
            } else {
                Log.e("InAppReviewManager", "Error requesting review flow", task.exception)
            }
        }
    }
}