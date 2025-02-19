//
//  BottleFeedTrackerLiveActivity.swift
//  BottleFeedTracker
//
//  Created by Jake thompson on 02/02/2025.
//

import ActivityKit
import WidgetKit
import SwiftUI

struct BottleFeedTrackerLiveActivity: Widget {
    //@State var profileImage = ImageFileController().loadBabyProfilePic()
    var body: some WidgetConfiguration {
        ActivityConfiguration(for: BottleFeedTrackerAttributes.self) { context in
                // Lock screen/banner UI goes here
                VStack {
                    HStack
                    {
                        //Image (uiImage: profileImage).resizable().scaledToFill().frame(width: 40, height: 40)
                        //Spacer()
                        Text("\(context.attributes.babyName)'s bottle feed:")
                        Text("")
                            .bold()
                        VStack
                        {
                            //Text("\(context.state.bottleDuration)")
                            Text("\(UtilFunctions().convertSecondsToMinutes(context.state.bottleDuration))")
                            
                        }
                    }
                    
                    //ProgressView(value: /*@START_MENU_TOKEN@*/0.5/*@END_MENU_TOKEN@*/)
                    ProgressView(
                        timerInterval: Date.now...context.state.estimatedEndTimeStamp,
                        countsDown: false,
                        label: { EmptyView() },
                        currentValueLabel: { EmptyView() }
                    )
                    .progressViewStyle(.linear)
                }
                .activityBackgroundTint(Color.cyan)
                .activitySystemActionForegroundColor(Color.black)
                .frame(width: 300, height: 100)

            } dynamicIsland: { context in
                DynamicIsland {
                    // Expanded UI goes here.  Compose the expanded UI through
                    // various regions, like leading/trailing/center/bottom
                    DynamicIslandExpandedRegion(.leading) {
                    }
                    DynamicIslandExpandedRegion(.trailing) {
                        //Text("Trailing")
                    }
                    DynamicIslandExpandedRegion(.bottom) {
                        Text("\(context.attributes.babyName)'s bottle feed")
                        Text("\(UtilFunctions().convertSecondsToMinutes(context.state.bottleDuration))")
                        ProgressView(
                            timerInterval: Date.now...context.state.estimatedEndTimeStamp,
                            countsDown: false,
                            label: { EmptyView() },
                            currentValueLabel: { EmptyView() }
                        )
                        .progressViewStyle(.linear)
                        // more content
                    }
                } compactLeading: {
                    
                } compactTrailing: {
                    Text("\(UtilFunctions().convertSecondsToMinutes(context.state.bottleDuration))")
                } minimal: {
                    Text("\(context.state.bottleDuration)")
                }
                //.widgetURL(URL(string: "http://www.apple.com"))
                //.keylineTint(Color.red)
            }
        }
}

struct BottleFeedTrackerLiveActivity_Previews: PreviewProvider {
    
    static let attributes = BottleFeedTrackerAttributes(babyName: "Finley", averageBottleDuration: 120)
    static let contentState = BottleFeedTrackerAttributes.ContentState(bottleDuration: 1, estimatedEndTimeStamp: Date(timeIntervalSinceNow: 5))
    
    
    static var previews: some View {
        attributes
            .previewContext(contentState, viewKind: .dynamicIsland(.compact))
            .previewDisplayName("Island Compact")
        attributes
            .previewContext(contentState, viewKind: .dynamicIsland(.expanded))
            .previewDisplayName("Island Expanded")
        attributes
            .previewContext(contentState, viewKind: .dynamicIsland(.minimal))
            .previewDisplayName("Minimal")
        attributes
            .previewContext(contentState, viewKind: .content)
            .previewDisplayName("Notification")
    }
}
