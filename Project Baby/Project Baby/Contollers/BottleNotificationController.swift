//
//  BottleNotificationController.swift
//  Project Baby
//
//  Created by Jake thompson on 09/09/2024.
//


import Foundation
import NotificationCenter

class BottleNotificationController
{
    
    //var minutesUntilNotification: Int
    
    let center = UNUserNotificationCenter.current()
    
    
    func requestNotificationAccessByUser() async
    {
        do {
            try await center.requestAuthorization(options: [.alert, .sound, .badge])
        } catch {
            // Handle the error here.
        }
            
        // Enable or disable features based on the authorization.
    }
    
    func scheduleBottleNotification(_ minutesUntilNotification: Int, _ bottleID: UUID) async
    {
        //FOR DEBUG PURPOSES
        await removeExistingNotifications("projectbaby")
        
        // Obtain the notification settings.
        let settings = await center.notificationSettings()
        
        // Verify the authorization status.
        guard (settings.authorizationStatus == .authorized) ||
                (settings.authorizationStatus == .provisional) else { return }
        
        if settings.alertSetting == .enabled {
            // Schedule an alert-only notification.
            print("Setting Notification")
            
            // 1. Create a new mutable notification content object
            let notificationContent = UNMutableNotificationContent()
            
            // 2. Set up the title and subtitle of the notification
            //notificationContent.title = "Has \(UserDefaults.standard.string(forKey: "babyName")) kicked today?"
            notificationContent.title = "Finley's next bottle is due"
            //Code for PROD below
            
            //notificationContent.title = "\(UserDefaults.standard.string(forKey: "projectbaby-babyName") ?? "")'s next bottle is due"
            
            //print (notificationContent.title)
            
            // 3. Set up the body of the notification
            notificationContent.body = "Remember when you do their next bottle, track it in the app to auto set a reminder for when the next bottle is due"
            
            // 4. Create the Date Components object
            var dateComponents = DateComponents()
            
            // 5. Set up the calendar type
            dateComponents.calendar = Calendar.current
            
            // 6. Set up the hours and minutes for the notification to be delivered
            //var hourInterval = 0
            //var minuteInterval = 0
            print(minutesUntilNotification)
            print("===============")
            print(Date.now.adding(minutes: 0))
            print(Date.now.adding(minutes: minutesUntilNotification))
            print("===============")
            let calculatedDateTimeToNotify = Date.now.adding(minutes: minutesUntilNotification)
            dateComponents = Calendar.current.dateComponents([.hour, .minute], from: calculatedDateTimeToNotify)
            
            
            //dateComponents.hour = calculatedDateTimeToNotify
            //dateComponents.minute = minuteInterval
            
            // 7. Create the trigger as a repeating event.
            let calendarTrigger = UNCalendarNotificationTrigger(
                dateMatching: dateComponents, repeats: true)
            
            // 8. Create an identifier for the notification
            //let notificationIdentifier = UUID().uuidString
            let notificationIdentifier = "projectbaby-BottleNotification-\(bottleID.uuidString)"
            
            // 9. Create a notification request object
            let notificationRequest = UNNotificationRequest(
                identifier: notificationIdentifier, // From step 8
                content: notificationContent,     // From step 1
                trigger: calendarTrigger         // From step 7
            )
            
            // 10. Get the notification center shared instance
            let notificationCenter = UNUserNotificationCenter.current()

            // 11. Add the notification request to the notification center
            do {
                try await notificationCenter.add(notificationRequest)
            } catch {
                print("⛔️ \\(error.localizedDescription)")
            }
            
           await checkPendingNotifications()
            
            
        } else {
            // Schedule a notification with a badge and sound.
        }
        
        
    }
    
    func checkPendingNotifications() async {
        let notificationCenter = UNUserNotificationCenter.current()
        let notificationRequests = await notificationCenter.pendingNotificationRequests()
            
        for notification in notificationRequests {
            print("> \(notification.identifier)")
        }
    }
    
    func removeExistingNotifications(_ notificationIdentifier: String) 
    {
        let notificationCenter = UNUserNotificationCenter.current()

        // Removing all delivered notifications
        notificationCenter.removeAllDeliveredNotifications()

        // Removing all pending notifications
        notificationCenter.removeAllPendingNotificationRequests()
        
        var notificationIdentifiers = [""]

        // Removing pending notifications wiht specific identifiers
        if notificationIdentifier == ""
        {
            notificationIdentifiers = ["projectbaby-BottleNotification"]
        }
        else
        {
            notificationIdentifiers = [notificationIdentifier]
        }
        notificationCenter.removePendingNotificationRequests(withIdentifiers: notificationIdentifiers)

        print("Removed all notifications from app")
    }
        
        
    
    
}

