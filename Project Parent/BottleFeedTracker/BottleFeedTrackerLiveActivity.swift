//
//  BottleFeedTrackerLiveActivity.swift
//  BottleFeedTracker
//
//  Created by Jake thompson on 19/02/2025.
//

import ActivityKit
import WidgetKit
import SwiftUI

//struct LiveActivityWidgetSampleAttributes: ActivityAttributes {
struct BottleFeedTrackerAttributes: ActivityAttributes {
    struct ContentState: Codable, Hashable {
        // Dynamic stateful properties about your activity go here!
        var bottleDuration: Int
    }
    
    // Fixed non-changing properties about your activity go here!
    var babyName: String
    var estimatedEndTimeStamp: Date
    var startTimeStamp: Date
    var dateRange: ClosedRange<Date>

    //var profileImage: UIImage?
}

struct BottleFeedTrackerLiveActivity: Widget {
    var body: some WidgetConfiguration {
        //ActivityConfiguration(for: LiveActivityWidgetSampleAttributes.self) { context in
        ActivityConfiguration(for: BottleFeedTrackerAttributes.self) { context in
            // Lock screen/banner UI goes here
            // ⚠️ Lock layout should be equal to expanded layout
            
            VStack {
                HStack
                {
                    //Image (uiImage: profileImage).resizable().scaledToFill().frame(width: 40, height: 40)
                    //Spacer()
                    Image(systemName: "waterbottle.fill")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 40, height: 40, alignment: .leading)
                    Text("\(context.attributes.babyName)'s bottle feed:")
                        .bold()
                    Text("")
                        .bold()
                    VStack
                    {
                        //Text("\(context.state.bottleDuration)")
                        Text("\(UtilFunctions().convertSecondsToMinutes(context.state.bottleDuration))")
                        //Text("\(UtilFunctions().percentageBetweenDates(startDate: context.attributes.startTimeStamp, endDate: context.attributes.estimatedEndTimeStamp, targetDate: Date.now))")
                        
                    }
                    
//                    ProgressView(
//                        timerInterval: Date.now...context.attributes.estimatedEndTimeStamp,
//                        countsDown: false,
//                        label: { EmptyView() },
//                        currentValueLabel: { EmptyView() }
//                    )
//                    .progressViewStyle(.linear)
                    
                }
//                ProgressView(value: UtilFunctions().percentageBetweenTimestamps(startTimestamp: context.attributes.startTimeStamp.timeIntervalSince1970, endTimestamp: context.attributes.estimatedEndTimeStamp.timeIntervalSince1970, targetTimestamp: Date.now.timeIntervalSince1970), total: 1)
                VStack
                {
                    ProgressView(timerInterval: context.attributes.dateRange, countsDown: false, label: { EmptyView() },                                 currentValueLabel: {EmptyView() })
                        .scaleEffect(x: 1, y: 1, anchor: .center)
                        .frame(alignment: .center)
                    Text("Progress compared to the average feed duration").padding(.horizontal)
                        .font(.caption)
                        .foregroundStyle(.white)
                }
                //ProgressView(timerInterval: Date.now...context.attributes.estimatedEndTimeStamp)
            }
            .activityBackgroundTint(Color.green.opacity(0.5))
                .activitySystemActionForegroundColor(Color.black)
                .frame(width: 350, height: 100)
//
//                    //ProgressView(value: /*@START_MENU_TOKEN@*/0.5/*@END_MENU_TOKEN@*/)
                    
//                }
            

        } dynamicIsland: { context in
            DynamicIsland { // ⚠️ On Device that don't have Dynamic Island, a brief notification is shown
                DynamicIslandExpandedRegion(.leading) {
                    // Expanded leading UI goes here
                    Spacer()
                    Image(systemName: "waterbottle.fill")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 40, height: 40, alignment: .center)
                    Spacer()
                }
                DynamicIslandExpandedRegion(.trailing) {
                    // Expanded trailing UI goes here
                    
                }
                DynamicIslandExpandedRegion(.center) {
                    // Expanded center UI goes here
                    HStack
                    {
                        Text("\(context.attributes.babyName)'s bottle feed:")
                            .bold()
                        //Text("\(UtilFunctions().percentageBetweenDates(startDate: context.attributes.startTimeStamp, endDate: context.attributes.estimatedEndTimeStamp, targetDate: Date.now))")
                        //Text("\(UtilFunctions().percentageBetweenTimestamps(startTimestamp: context.attributes.startTimeStamp.timeIntervalSince1970, endTimestamp: context.attributes.estimatedEndTimeStamp.timeIntervalSince1970, targetTimestamp: Date.now.timeIntervalSince1970))")
                        Text("\(UtilFunctions().convertSecondsToMinutes(context.state.bottleDuration))")
                            .frame(alignment: .trailing)
                    }
                }
                DynamicIslandExpandedRegion(.bottom) {
                    // Expanded bottom UI goes here
                    //ProgressView(value: UtilFunctions().percentageBetweenTimestamps(startTimestamp: context.attributes.startTimeStamp.timeIntervalSince1970, endTimestamp: context.attributes.estimatedEndTimeStamp.timeIntervalSince1970, targetTimestamp: Date.now.timeIntervalSince1970), total: 1)
                    VStack
                    {
                        ProgressView(timerInterval: context.attributes.dateRange, countsDown: false, label: { EmptyView() },                                 currentValueLabel: {EmptyView() })
                            .scaleEffect(x: 1, y: 1, anchor: .center)
                            .frame(alignment: .center)
                        Text("Progress compared to the average feed duration").padding(.horizontal)
                            .font(.caption)
                            .foregroundStyle(.gray)
                    }
                }
            } compactLeading: {
                // Compact leading UI goes here
                Image(systemName: "waterbottle.fill")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 20, height: 20, alignment: .leading)
            } compactTrailing: {
                // Compact trailing UI goes here
                Text("\(UtilFunctions().getMinutes(context.state.bottleDuration)) min")
            } minimal: {
                // Minimal UI goes here
                Text("\(UtilFunctions().getMinutes(context.state.bottleDuration))m")
            }
        }
    }
}

//extension BottleFeedTrackerAttributes {
//    fileprivate static var preview: BottleFeedTrackerAttributes {
//        BottleFeedTrackerAttributes(name: "World")
//    }
//}


//#Preview("Notification", as: .content, using: BottleFeedTrackerAttributes.preview) {
//   BottleFeedTrackerLiveActivity()
//} contentStates: {
//    BottleFeedTrackerAttributes.ContentStat
//    BottleFeedTrackerAttributes.ContentState.starEyes
//}
