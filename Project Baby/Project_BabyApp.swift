//
//  Project_BabyApp.swift
//  Project Baby
//
//  Created by Jake thompson on 15/08/2024.
//

import SwiftUI

@main
struct Project_BabyApp: App {
    let persistenceController = PersistenceController.shared

    var body: some Scene {
        WindowGroup {
            ContentView()
                .environment(\.managedObjectContext, persistenceController.container.viewContext)
        }
    }
}
