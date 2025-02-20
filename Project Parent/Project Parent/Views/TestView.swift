//
//  TestView.swift
//  Project Parent
//
//  Created by Jake thompson on 19/02/2025.
//

import SwiftUI
import ActivityKit
import BottleFeedTrackerExtension

struct TestView: View {
        @State private var activity: Activity<LiveActivityWidgetSampleAttributes>?

        var body: some View {
            VStack {
                Button("Start") {
                    self.launchLiveActivity()
                    
                }
                Button("End") {
                    Task {
                        await self.endLiveActivity()
                    }
                }
            }
//            .onChange(of: scenePhase) { newPhase in
//            }
        }
}

private extension TestView {
    func endLiveActivity() async {
        print("End live activity")
        await self.activity?.end(
           .init(
               state: .init(value: 0),
               staleDate: nil
           ),
           dismissalPolicy: .after(.now + 4) // System will remove the notification after 4 seconds. You also have: .default (= 4 hours)/.immediate/.after(Date)
       )
    }
}

private extension TestView {
    func launchLiveActivity() {
        if ActivityAuthorizationInfo().areActivitiesEnabled {
            print("Start live activity")
            activity = try? Activity.request(
                attributes: LiveActivityWidgetSampleAttributes(name: UUID().uuidString),
                content: .init(
                    state: LiveActivityWidgetSampleAttributes.ContentState(value: 0),
                    staleDate: nil
                )
            )
        }
        Task {
            for await pushToken in self.activity!.pushTokenUpdates {
                let pushTokenString = pushToken.reduce ("") { $0 + String(format: "%02x", $1) }
                print("New push token: \(pushTokenString)")
                //try await self.sendPushToken (hero: hero, pushTokenString: pushTokenString)
            }
        }
        //print (self.activity?.id ?? "No activity")
        //print (self.activity?.pushToken ?? "No push token")
    }
}

//private extension TestView {
//    func didEnterBackground() {
//        print("Entering Background")
//        // Simulate server push notifications
////        Server().send(
////            notifications: 6,
////            everySeconds: 4,
////            completion: { i in
//                // when receiving a push notification
//        var i = 1
//                Task {
//                    if i == 6 {
//                        await self.activity?.end(
//                            .init(
//                                state: .init(value: i),
//                                staleDate: nil
//                            ),
//                            dismissalPolicy: .after(.now + 4) // System will remove the notification after 4 seconds. You also have: .default (= 4 hours)/.immediate/.after(Date)
//                        )
//                    } else {
//                        // When you update you could provide an alert to wake up the screen OR expand the notification if screen already on with alertConfiguration
//                        await self.activity?.update(
//                            .init(
//                                state: .init(value: i),
//                                staleDate: nil
//                            ),
//                            alertConfiguration: nil
//                        
//                        )
//                    }
//                    i += 1
//                }
//            }
//    }


struct LiveActivityWidgetSampleAttributes: ActivityAttributes {
    struct ContentState: Codable, Hashable {
        // Dynamic stateful properties about your activity go here!
        var value: Int
    }
    
    // Fixed non-changing properties about your activity go here!
    var name: String
}

#Preview {
    TestView()
}
