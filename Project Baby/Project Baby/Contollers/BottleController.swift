//
//  BottleController.swift
//  Project Baby
//
//  Created by Jake thompson on 26/08/2024.
//

import Foundation
import SwiftUI
import CoreData

class BottleController
{
    @Environment(\.managedObjectContext) private var viewContext
    @AppStorage("projectbaby.bottles.\(Date.now.formatted(.dateTime.dayOfYear()))") var bottlesTakenToday: Int = 0
    @AppStorage("projectbaby.bottles.\(Calendar.current.date(byAdding: .day, value: -1, to: Date.now)!.formatted(.dateTime.dayOfYear()))")  var yesterdayCount = UserDefaults.standard.integer(forKey: "projectbaby.bottles \(Calendar.current.date(byAdding: .day, value: -1, to: Date.now)!.formatted(.dateTime.dayOfYear()))")
    @AppStorage("projectbaby.averagebottleduration") var averageBottleDuration: Double = 0.0
    @AppStorage("projectbaby.totalbottleduration") var totalBottleDuration: Double = 0.0
    @AppStorage("projectbaby.totalbottles") var totalBottles: Double = 0.0
    
    func addBottleToTodayCount()
    {
        print("Adding bottle to count")
        bottlesTakenToday += 1
        UserDefaults.standard.set(bottlesTakenToday, forKey: "projectbaby.bottles.\(Date.now.formatted(.dateTime.dayOfYear()))")
    }
    
    func removeBottleFromTodayCount()
    {
        print("Removing bottle to count")
        bottlesTakenToday -= 1
        UserDefaults.standard.set(bottlesTakenToday, forKey: "projectbaby.bottles.\(Date.now.formatted(.dateTime.dayOfYear()))")
    }
    func removeBottleFromYesterdayCount()
    {
        print("Removing bottle to count")
        var bottlesTakenYesterday = UserDefaults.standard.integer(forKey: "projectbaby.bottles.\(Calendar.current.date(byAdding: .day, value: -1, to: Date.now)!.formatted(.dateTime.dayOfYear()))")
        bottlesTakenYesterday -= 1
        UserDefaults.standard.set(bottlesTakenYesterday, forKey: "projectbaby.bottles.\(Calendar.current.date(byAdding: .day, value: -1, to: Date.now)!.formatted(.dateTime.dayOfYear()))")
    }
    
    func calculateAverageBottleDuration()
    {
        print("Calculating")
        averageBottleDuration = totalBottleDuration / totalBottles
        print("Average Bottle time is \(averageBottleDuration)")
        UserDefaults.standard.set(averageBottleDuration, forKey: "projectbaby.averagebottleduration")

    }
    
    func addBottleData(durationToAdd: CGFloat)
    {
        totalBottleDuration = totalBottleDuration + durationToAdd
        totalBottles = totalBottles + 1.0
        UserDefaults.standard.set(totalBottleDuration, forKey: "projectbaby.totalbottleduration")
        UserDefaults.standard.set(totalBottles, forKey: "projectbaby.totalbottles")
        calculateAverageBottleDuration()
    }
    
    func removeBottleData(durationToRemove: CGFloat)
    {
        totalBottleDuration = totalBottleDuration - durationToRemove
        totalBottles = totalBottles - 1.0
        UserDefaults.standard.set(totalBottleDuration, forKey: "projectbaby.totalbottleduration")
        UserDefaults.standard.set(totalBottles, forKey: "projectbaby.totalbottles")
        calculateAverageBottleDuration()
    }
    
}
