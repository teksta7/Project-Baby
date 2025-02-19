//
//  ProfileController.swift
//  Project Parent
//
//  Created by Jake thompson on 17/10/2024.
//

import Foundation
import SwiftUI
import StoreKit
import CoreData

class ProfileController
{
    @Environment(\.managedObjectContext) private var viewContext
    @AppStorage("projectparent.babyBirthDateTime") var birthDay = 0.0
    @AppStorage("projectparent.currentWeight") var currentWeight: Double = 0.0
    var localProfileString = (UserDefaults.standard.string(forKey: "projectparent.babyName") ?? "Unknown")+"'s Profile"
    @AppStorage("projectparent.babyName") var babyName = (UserDefaults.standard.string(forKey: "projectparent.babyName")) ?? "Unknown"

    
    func lbsOzToKg(pounds: Double, ounces: Double) -> Double
    {
        let totalPounds = pounds + (ounces / 16)
        let kg = totalPounds * 0.45359237
        return kg
        
        // Usage
        //let kg = lbsOzToKg(pounds: 150, ounces: 4)
        //print("Weight in kg: \(kg)")
    }
    
    func kgToLbsOz(kg: Double) -> (Int, Double)
    {
        print ("kg: \(kg)")
        let totalPounds = kg * 2.20462
        let pounds = Int(totalPounds)
        let ounces = (totalPounds - Double(pounds)) * 16
        print ("pounds: \(pounds)")
        print ("ounces: \(ounces)")
        return (pounds, ounces)
        
        // Usage
        //let (pounds, ounces) = kgToLbsOz(kg: 75)
        //print("Weight: \(pounds) lbs \(ounces) oz")
    }
    
    func giveCurrentWeightInKg() -> Double
    {
//        if currentWeight == 0.0
//        {
//            currentWeight = fetchLatestWeight()?.kg ?? 0.0
//        }
        return currentWeight
    }
    
    func giveCurrentWeightInLbsOz() -> (Int, Double)
    {
//        if currentWeight == 0.0
//        {
//            currentWeight = fetchLatestWeight()?.kg ?? 0.0
//        }
        return kgToLbsOz(kg: currentWeight)
        //// Use the function
        //let weightInLbsOz = giveCurrentWeightInLbsOz()
        //print("Current weight: \(weightInLbsOz.0) lbs \(weightInLbsOz.1) oz")
    }
    
    func setCurrentWeight(kg: Double) -> Bool
    {
//        if addWeightToModel(kg: kg) == true
//        {
//        if currentWeight == 0.0
//        {
//            currentWeight = fetchLatestWeight()?.kg ?? 0.0
//        }
            currentWeight = kg
            return true
       // }
       // else { return false }
    }
    
    func getAgeInDays() -> Int
    {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd"
        //let dateOfBirth = dateFormatter.date(from: "1990-01-01")!
        let currentDate = Date()
        let components = Calendar.current.dateComponents([.day], from: dateFromTimeInterval(birthDay), to: currentDate)
        return components.day!

    }
    
    func getAgeInWeeks() -> Int
    {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd"
        //let dateOfBirth = dateFormatter.date(from: "1990-01-01")!
        let currentDate = Date()
        let components = Calendar.current.dateComponents([.weekOfYear], from: dateFromTimeInterval(birthDay), to: currentDate)
        return components.weekOfYear!

    }
    
    func getAgeInMonths() -> Int
    {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd"
        //let dateOfBirth = dateFormatter.date(from: "1990-01-01")!
        let currentDate = Date()
        let components = Calendar.current.dateComponents([.month], from: dateFromTimeInterval(birthDay), to: currentDate)
        return components.month!
    }
    
    func getAgeInYears() -> Int
    {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd"
        //let dateOfBirth = dateFormatter.date(from: "1990-01-01")!
        let currentDate = Date()
        let components = Calendar.current.dateComponents([.year], from: dateFromTimeInterval(birthDay), to: currentDate)
        return components.year!
    }

    func dateFromTimeInterval(_ timeInterval: TimeInterval) -> Date {
        return Date(timeIntervalSince1970: timeInterval)
    }
}
