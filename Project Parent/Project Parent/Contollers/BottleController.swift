//
//  BottleController.swift
//  Project Parent
//
//  Created by Jake thompson on 26/08/2024.
//

import Foundation
import SwiftUI
import CoreData
import Combine

class BottleController
{
    @Environment(\.managedObjectContext) private var viewContext
    @AppStorage("projectparent.bottles.\(Date.now.formatted(.dateTime.dayOfYear()))") var bottlesTakenToday: Int = 0
    @AppStorage("projectparent.bottles.\(Calendar.current.date(byAdding: .day, value: -1, to: Date.now)!.formatted(.dateTime.dayOfYear()))")  var yesterdayCount = UserDefaults.standard.integer(forKey: "projectparent.bottles \(Calendar.current.date(byAdding: .day, value: -1, to: Date.now)!.formatted(.dateTime.dayOfYear()))")
    @AppStorage("projectparent.averagebottleduration") var averageBottleDuration: Double = 0.0
    @AppStorage("projectparent.totalbottleduration") var totalBottleDuration: Double = 0.0
    @AppStorage("projectparent.totalbottles") var totalBottles: Double = 0.0
    @AppStorage("projectparent.nextBottleNotificationDateTime") var nextBottleNotificationDateTime: String = (UserDefaults.standard.string(forKey: "projectparent.nextBottleNotificationDateTime") ?? "N/A")
    
    @FetchRequest(sortDescriptors: [NSSortDescriptor(keyPath: \Bottle.date, ascending: false)], animation: .default) private var bottles: FetchedResults<Bottle>
    
    func addBottleToTodayCount()
    {
        print("Adding bottle to count")
        bottlesTakenToday += 1
        UserDefaults.standard.set(bottlesTakenToday, forKey: "projectparent.bottles.\(Date.now.formatted(.dateTime.dayOfYear()))")
    }
    
    func removeBottleFromTodayCount()
    {
        print("Removing bottle to count")
        bottlesTakenToday -= 1
        UserDefaults.standard.set(bottlesTakenToday, forKey: "projectparent.bottles.\(Date.now.formatted(.dateTime.dayOfYear()))")
    }
    func removeBottleFromYesterdayCount()
    {
        print("Removing bottle to count")
        var bottlesTakenYesterday = UserDefaults.standard.integer(forKey: "projectparent.bottles.\(Calendar.current.date(byAdding: .day, value: -1, to: Date.now)!.formatted(.dateTime.dayOfYear()))")
        bottlesTakenYesterday -= 1
        UserDefaults.standard.set(bottlesTakenYesterday, forKey: "projectparent.bottles.\(Calendar.current.date(byAdding: .day, value: -1, to: Date.now)!.formatted(.dateTime.dayOfYear()))")
    }
    
    func calculateAverageBottleDuration()
    {
        print("Calculating")
        averageBottleDuration = totalBottleDuration / totalBottles
        print("Average Bottle time is \(averageBottleDuration)")
        UserDefaults.standard.set(averageBottleDuration, forKey: "projectparent.averagebottleduration")

    }
    
    func setTimeUntilNextBottle(timeString: String)
    {
        print("++++++++++++++++++++++")
        UserDefaults.standard.set(timeString, forKey: "projectparent.nextBottleNotificationDateTime")
        nextBottleNotificationDateTime = UserDefaults.standard.string(forKey: "projectparent.nextBottleNotificationDateTime")!
        print("++++++++++++++++++++++")
    }
    
    func calculateAverageTimeBetweenBottles()
    {
        var totalSecondsBetweenBottles: Double = 0.0
        //var currentSecondsBetweenBottles: Double = 0.0
        var bottleCount: Int = 0
        var previousBottleTime: Date? = nil
        
        // Create a fetch request for a NamedEntity
        let bottleFetchRequest: NSFetchRequest<Bottle>
        bottleFetchRequest = Bottle.fetchRequest()
        bottleFetchRequest.sortDescriptors = [NSSortDescriptor(key: "start_time", ascending: true)]

        // Perform the fetch request to get the objects
        // matching the predicate
        let coreBottles =  try! CoreDataController().container.viewContext.fetch(bottleFetchRequest)
            
        for bottle in coreBottles
            {
                let currentBottleTime: Date = bottle.start_time!
                if bottleCount > 0
                {
                    print(currentBottleTime, " - ", previousBottleTime!)
                    totalSecondsBetweenBottles += currentBottleTime.timeIntervalSince(previousBottleTime!)
                    previousBottleTime = currentBottleTime
                    
                }
                bottleCount += 1
                print("Bottle Count: \(bottleCount)")
                print("Total Seconds Between Bottles: \(totalSecondsBetweenBottles)")
                previousBottleTime = bottle.start_time!

            }
            var averageTimeBetweenBottles: Double = 0.0
            averageTimeBetweenBottles = totalSecondsBetweenBottles / Double(bottleCount)
            print("Average Time Between Bottles: \(averageTimeBetweenBottles)")
            UserDefaults.standard.set(averageTimeBetweenBottles, forKey: "projectparent.averagetimebetweenbottles")
    }
    
    func addBottleData(durationToAdd: CGFloat)
    {
        totalBottleDuration = totalBottleDuration + durationToAdd
        totalBottles = totalBottles + 1.0
        UserDefaults.standard.set(totalBottleDuration, forKey: "projectparent.totalbottleduration")
        UserDefaults.standard.set(totalBottles, forKey: "projectparent.totalbottles")
        calculateAverageBottleDuration()
    }
    
    func removeBottleData(durationToRemove: CGFloat)
    {
        totalBottleDuration = totalBottleDuration - durationToRemove
        totalBottles = totalBottles - 1.0
        UserDefaults.standard.set(totalBottleDuration, forKey: "projectparent.totalbottleduration")
        UserDefaults.standard.set(totalBottles, forKey: "projectparent.totalbottles")
        calculateAverageBottleDuration()
    }
}
