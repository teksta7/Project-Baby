//
//  BottleSettings.swift
//  Project Parent
//
//  Created by Jake thompson on 05/09/2024.
//

import Foundation
import Combine

final class BottleSettings: ObservableObject {

    let objectWillChange = PassthroughSubject<Void, Never>()

    @UserDefault("EnableBottleNotification", defaultValue: false)
    var enableBottleNotification: Bool {
        willSet {
            objectWillChange.send()
        }
    }
    @UserDefault("isBottleFeedLiveActivityOn", defaultValue: false)
    var isBottleFeedLiveActivityOn: Bool {
        willSet {
            objectWillChange.send()
        }
    }
   
    @UserDefault("toggleInAppAds", defaultValue: true)
    var toggleInAppAds: Bool {
        willSet {
            objectWillChange.send()
        }
    }

    @UserDefault("DefaultBottleNote", defaultValue: "None")
    var setDefaultBottleNote: String {
        willSet {
            objectWillChange.send()
        }
    }
    

    @UserDefault("setBottleNotificationCountDownMinutes", defaultValue: 0)
    var setBottleNotificationCountDownMinutes: Int {
        willSet {
            objectWillChange.send()
        }
    }
    
    @UserDefault("setDefaultOunces", defaultValue: 0.00)
    var setDefaultOunces: CGFloat {
        willSet {
            objectWillChange.send()
        }
    }
    
}
