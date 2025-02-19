//
//  ActivityController.swift
//  Project Parent
//
//  Created by Jake thompson on 13/02/2025.
//
import ActivityKit
import Combine
import Foundation

final class ActivityController: ObservableObject {
    @MainActor @Published private(set) var activityID: String?
    @MainActor @Published private(set) var activityToken: String?
    
    static let shared = ActivityController()
    
    func start() async {
        await cancelAllRunningActivities()
        await startNewLiveActivity()
    }
    
    func startNewLiveActivity() async {

        print("Starting Live Activity")
        print(ActivityAuthorizationInfo().areActivitiesEnabled)

        if ActivityAuthorizationInfo().areActivitiesEnabled {
            let attributes = BottleFeedTrackerAttributes(babyName: UserDefaults.standard.string(forKey: "projectparent.babyName") ?? "Unknown", averageBottleDuration: UserDefaults.standard.double(forKey: "projectparent.averageBottleDuration"))
            
            let initialContentState = ActivityContent(state: BottleFeedTrackerAttributes.ContentState(bottleDuration: 0 ,estimatedEndTimeStamp: Date(timeIntervalSinceNow: UserDefaults.standard.double(forKey: "projectparent.averageBottleDuration"))), staleDate: nil, relevanceScore: 0)
            
            let activity = try? Activity.request(
                attributes: attributes,
                content: initialContentState,
                pushType: .token)
            
            guard let activity = activity else { return }
            await MainActor.run { activityID = activity.id }
            
            Task {
                for await pushToken in activity.pushTokenUpdates {
                    let pushTokenString = pushToken.reduce("") {
                          $0 + String(format: "%02x", $1)
                    }


                    print("New push token: \(pushTokenString)")
                            
                    await MainActor.run {activityToken =  pushTokenString} //(hero: hero, pushTokenString: pushTokenString)
                }
            }
            
//            Task
//            {
//                for await data in activity.pushTokenUpdates {
//                    let token = data.map {String(format: "%02x", $0)}.joined()
//                    print("ACTIVITY TOKEN:\n\(token)")
//                    await MainActor.run { activityToken = token }
//                    // HERE SEND THE TOKEN TO THE SERVER
//                }
//            }
        }
        //await activityLogging()

    }
    
    func updateActivity() async {
        guard let activityID = await activityID,
              let runningActivity = Activity<BottleFeedTrackerAttributes>.activities.first(where: { $0.id == activityID }) else {
            return
        }
        let newContentState = BottleFeedTrackerAttributes.ContentState(bottleDuration: 1, estimatedEndTimeStamp: Date(timeIntervalSinceNow: UserDefaults.standard.double(forKey: "projectparent.averageBottleDuration")))
        await runningActivity.update(using: newContentState)
    }
    
    func endActivity() async {
        guard let activityID = await activityID,
              let runningActivity = Activity<BottleFeedTrackerAttributes>.activities.first(where: { $0.id == activityID }) else {
            return
        }

        let initialContentState = BottleFeedTrackerAttributes.ContentState(bottleDuration: 0 ,estimatedEndTimeStamp: Date(timeIntervalSinceNow: UserDefaults.standard.double(forKey: "projectparent.averageBottleDuration")))

        await runningActivity.end(
            ActivityContent(state: initialContentState, staleDate: Date.distantFuture),
            dismissalPolicy: .immediate
        )
        
        await MainActor.run {
            self.activityID = nil
            self.activityToken = nil
        }
    }
    
    func cancelAllRunningActivities() async {
        print("Stop all running live activities")
        for activity in Activity<BottleFeedTrackerAttributes>.activities {
            //await activity.end(dismissalPolicy: .immediate)

            let initialContentState = BottleFeedTrackerAttributes.ContentState(bottleDuration: 0 ,estimatedEndTimeStamp: Date(timeIntervalSinceNow: UserDefaults.standard.double(forKey: "projectparent.averageBottleDuration")))
            
            await activity.end(
                ActivityContent(state: initialContentState, staleDate: Date()),
                dismissalPolicy: .immediate
            )
        }
        await MainActor.run {
            activityID = nil
            activityToken = nil
        }
        await activityLogging()
    }
    
    func activityLogging() async
    {
        await print(activityID ?? "No activity ID")
        await print(activityToken ?? "No activity token")
    }
    
}
