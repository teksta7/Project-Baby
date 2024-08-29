//
//  BottleController.swift
//  Project Baby
//
//  Created by Jake thompson on 26/08/2024.
//

import Foundation
import SwiftUI

class BottleController
{
    @Environment(\.managedObjectContext) private var viewContext
    @AppStorage("projectbaby.bottles.\(Date.now.formatted(.dateTime.dayOfYear()))") var bottlesTakenToday: Int = 0
    
    func addBottleToTodayCount()
    {
        print("Adding bottle to count")
        bottlesTakenToday += 1
        UserDefaults.standard.set(bottlesTakenToday, forKey: "projectbaby.bottles.\(Date.now.formatted(.dateTime.dayOfYear()))")
    }
    
    
}
