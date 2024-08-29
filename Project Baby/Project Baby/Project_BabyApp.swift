//
//  Project_BabyApp.swift
//  Project Baby
//
//  Created by Jake thompson on 15/08/2024.
//

import SwiftUI

@main
struct Project_BabyApp: App {
    //Controller for setup data
    @StateObject private var coreDataController: CoreDataController = CoreDataController()
    
    var body: some Scene {
        WindowGroup {
            HomeView()
                .environmentObject(coreDataController)
                .environment(\.managedObjectContext, coreDataController.container.viewContext)
                .environment(\.colorScheme, .dark)
        }
    }
}
