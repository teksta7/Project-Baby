//
//  BottleFeedTrackerAttributes.swift
//  Project Parent
//
//  Created by Jake thompson on 02/02/2025.
//

import Foundation
import ActivityKit

struct BottleFeedTrackerAttributes: ActivityAttributes {
    public struct ContentState: Codable, Hashable {
        // Dynamic stateful properties about your activity go here!        
        var bottleDuration: Int //USE WITH THIS FUNCTION - UtilFunctions().convertSecondsToMinutes(bottleDuration))
        var estimatedEndTimeStamp: Date //This is the average bottle feed value that needs to be fed into this variable
    }

    // Fixed non-changing properties about your activity go here!
    var babyName: String
    var averageBottleDuration:  Double //USE WITH THIS STORAGE LOCATION -  @AppStorage("projectparent.averagebottleduration")
    //var babyProfilePic: Image?
}
