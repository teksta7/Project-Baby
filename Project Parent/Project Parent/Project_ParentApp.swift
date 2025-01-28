//
//  Project_BabyApp.swift
//  Project Parent
//
//  Created by Jake thompson on 15/08/2024.
//

import SwiftUI
import GoogleMobileAds

class AppDelegate: UIResponder, UIApplicationDelegate {

  private let adCoordinator = AdCoordinator()
    
  func application(_ application: UIApplication,
      didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
      
    //GADMobileAds.sharedInstance().start(completionHandler: nil)
      GADMobileAds.sharedInstance().start()
      print("Ad setup")
      
      if UNMutableNotificationContent().badge?.intValue ?? 0 > 0
      {
          BottleNotificationController().removeExistingNotifications("projectparent")
      }

    return true
  }
        
    func applicationWillTerminate(_ application: UIApplication) {
        if UNMutableNotificationContent().badge?.intValue ?? 0 > 0
        {
            BottleNotificationController().removeExistingNotifications("projectparent")
        }
        
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
