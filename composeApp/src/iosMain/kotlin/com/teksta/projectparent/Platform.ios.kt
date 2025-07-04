package com.teksta.projectparent

import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

actual fun scheduleBottleFeedNotification(delaySeconds: Long, title: String, body: String) {
    // TODO: Implement notification scheduling for iOS
}