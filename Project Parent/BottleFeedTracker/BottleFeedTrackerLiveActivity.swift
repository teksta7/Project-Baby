//
//  BottleFeedTrackerLiveActivity.swift
//  BottleFeedTracker
//
//  Created by Jake thompson on 19/02/2025.
//

import ActivityKit
import WidgetKit
import SwiftUI

struct BottleFeedTrackerAttributes: ActivityAttributes {
    public struct ContentState: Codable, Hashable {
        // Dynamic stateful properties about your activity go here!
        var value: Int
    }
    
    // Fixed non-changing properties about your activity go here!
    var name: String
}

struct BottleFeedTrackerLiveActivity: Widget {
    var body: some WidgetConfiguration {
        ActivityConfiguration(for: BottleFeedTrackerAttributes.self) { context in
            // Lock screen/banner UI goes here
            // ⚠️ Lock layout should be equal to expanded layout
        } dynamicIsland: { context in
            DynamicIsland { // ⚠️ On Device that don't have Dynamic Island, a brief notification is shown
                DynamicIslandExpandedRegion(.leading) {
                    // Expanded leading UI goes here
                }
                DynamicIslandExpandedRegion(.trailing) {
                    // Expanded trailing UI goes here
                }
                DynamicIslandExpandedRegion(.center) {
                    // Expanded center UI goes here
                }
                DynamicIslandExpandedRegion(.bottom) {
                    // Expanded bottom UI goes here
                }
            } compactLeading: {
                // Compact leading UI goes here
            } compactTrailing: {
                // Compact trailing UI goes here
            } minimal: {
                // Minimal UI goes here
            }
        }
    }
}

extension BottleFeedTrackerAttributes {
    fileprivate static var preview: BottleFeedTrackerAttributes {
        BottleFeedTrackerAttributes(name: "World")
    }
}


//#Preview("Notification", as: .content, using: BottleFeedTrackerAttributes.preview) {
//   BottleFeedTrackerLiveActivity()
//} contentStates: {
//    BottleFeedTrackerAttributes.ContentStat
//    BottleFeedTrackerAttributes.ContentState.starEyes
//}
