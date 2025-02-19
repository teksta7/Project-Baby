//
//  Project_BabyApp.swift
//  Project Parent
//
//  Created by Jake thompson on 15/08/2024.
//

import SwiftUI
import GoogleMobileAds

class AppDelegate: UIResponder, UIApplicationDelegate, UNUserNotificationCenterDelegate {

  private let adCoordinator = AdCoordinator()
    
//  func application(_ application: UIApplication,
//      didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
//      
//    //GADMobileAds.sharedInstance().start(completionHandler: nil)
//      GADMobileAds.sharedInstance().start()
//      print("Ad setup")
//      
//      if UNMutableNotificationContent().badge?.intValue ?? 0 > 0
//      {
//          BottleNotificationController().removeExistingNotifications("projectparent")
//      }
//
//    return true
//  }
        
    func applicationWillTerminate(_ application: UIApplication) async {
        if UNMutableNotificationContent().badge?.intValue ?? 0 > 0
        {
            BottleNotificationController().removeExistingNotifications("projectparent")
        }
        await ActivityController().cancelAllRunningActivities()
        
    }
    
    func applicationDidEnterBackground(_ application: UIApplication) {
        if UNMutableNotificationContent().badge?.intValue ?? 0 > 0
        {
            BottleNotificationController().removeExistingNotifications("projectparent")
        }
    }
    
    func applicationWillResignActive(_ application: UIApplication) {
        if UNMutableNotificationContent().badge?.intValue ?? 0 > 0
        {
            BottleNotificationController().removeExistingNotifications("projectparent")
        }
    }
    
    func applicationDidBecomeActive(_ application: UIApplication) {
        if UNMutableNotificationContent().badge?.intValue ?? 0 > 0
        {
            BottleNotificationController().removeExistingNotifications("projectparent")
        }
    }
    func applicationDidFinishLaunching(_ application: UIApplication) {
        if UNMutableNotificationContent().badge?.intValue ?? 0 > 0
        {
            BottleNotificationController().removeExistingNotifications("projectparent")
        }
    }
    
    
    //For Live Activity
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        UNUserNotificationCenter.current().delegate = self
        UNUserNotificationCenter.current()
            .requestAuthorization(
                options: [.alert, .sound, .badge]) { granted, _ in
                    guard granted else { return }
                    UNUserNotificationCenter.current().getNotificationSettings { settings in
                        guard settings.authorizationStatus == .authorized else { return }
                        DispatchQueue.main.async {
                            application.registerForRemoteNotifications()
                        }
                    }
                }
        //GADMobileAds.sharedInstance().start(completionHandler: nil)
          //GADMobileAds.sharedInstance().start()
          //print("Ad setup")
        
          if UNMutableNotificationContent().badge?.intValue ?? 0 > 0
          {
              BottleNotificationController().removeExistingNotifications("projectparent")
          }
        return true
    }
    
    func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable : Any], fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
        guard let aps = userInfo["aps"] as? [String: AnyObject] else {
            completionHandler(.failed)
            return
        }
        print("got something, aka the \(aps)")
    }
    
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        let tokenParts = deviceToken.map { data in String(format: "%02.2hhx", data) }
        let token = tokenParts.joined()
        print("Device Token: \(token)")
    }
    
    func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
        print("Device Token not found.")
    }
}

@main
struct Project_ParentApp: App {
    //Controller for setup data
    @StateObject private var coreDataController: CoreDataController = CoreDataController()
    
    // To handle app delegate callbacks in an app that uses the SwiftUI lifecycle,
    // you must create an application delegate and attach it to your `App` struct
    // using `UIApplicationDelegateAdaptor`.
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    
    @StateObject
    private var IAP = IAPController()
    
    
    var body: some Scene {
        WindowGroup {
            //HomeView()
            InitialOnboardingView()
                .environmentObject(coreDataController)
                .environment(\.managedObjectContext, coreDataController.container.viewContext)
                .environmentObject(IAP)
                .preferredColorScheme(.dark)
                .task {
                    await IAP.updatePurchasedProducts()
                    do {
                        try await IAP.loadProducts()
                    } catch {
                        print(error)
                    }
                }
        }
    }
}
