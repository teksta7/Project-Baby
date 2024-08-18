//
//  CoreDataController.swift
//  Project Baby
//
//  Created by Jake thompson on 18/08/2024.
//

import Foundation
import CoreData

class CoreDataController: NSObject, ObservableObject {
    
    let container = NSPersistentContainer(name: "Project_Baby")
    
    /// Dynamic properties that the UI will react to
    @Published var bottles: [Bottle] = [Bottle]()
    
    override init() {
            super.init()
            container.loadPersistentStores { _, _ in }
        }
}
