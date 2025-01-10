//
//  HomeCard.swift
//  Project Parent
//
//  Created by Jake thompson on 15/08/2024.
//

import Foundation
import SwiftUI

struct HomeCard: Identifiable
{
    var id: UUID = .init()
    var color: Color
    var viewString: String //TO BE USED TO CHECK WHICH VIEW TO DISPLAY ON THE CARD
    var presentedString: String //TO BE USED TO CHECK WHICH VIEW TO DISPLAY ON THE CARD
    var imageToDisplay: String //TO BE USED TO CHECK WHICH VIEW TO DISPLAY ON THE CARD
    var toTrack: Bool
}

class HomeCardStore: ObservableObject
{
    var profileHomeCard: HomeCard = .init(color: .mint, viewString: "PROFILE", presentedString: "Finley's Profile", imageToDisplay: "figure.child", toTrack: true)
    var bottlesHomeCard: HomeCard = .init(color: .green, viewString: "BOTTLES", presentedString: "Bottles", imageToDisplay: "waterbottle",  toTrack: UserDefaults().bool(forKey: "com.projectparent.isBottlesCardTracked"))
    var sleepHomeCard: HomeCard = .init(color: .indigo, viewString: "SLEEP", presentedString: "Sleep", imageToDisplay: "powersleep", toTrack: UserDefaults().bool(forKey: "com.projectparent.isSleepCardTracked"))
    var foodHomeCard: HomeCard = .init(color: .yellow, viewString: "FOOD", presentedString: "Food", imageToDisplay: "carrot", toTrack: UserDefaults().bool(forKey: "com.projectparent.isFoodCardTracked"))
    var medsHomeCard: HomeCard = .init(color: .red, viewString: "MEDS", presentedString: "Medicene", imageToDisplay: "pill", toTrack: UserDefaults().bool(forKey: "com.projectparent.isMedsCardTracked"))
    var windHomeCard: HomeCard = .init(color: .blue, viewString: "WIND", presentedString: "Wind", imageToDisplay: "wind", toTrack: UserDefaults().bool(forKey: "com.projectparent.isWindCardTracked"))
    var pooHomeCard: HomeCard = .init(color: .brown, viewString: "POO", presentedString: "Nappies", imageToDisplay: "toilet", toTrack: UserDefaults().bool(forKey: "com.projectparent.isPooCardTracked"))
    var settingsHomeCard: HomeCard = .init(color: .gray, viewString: "SETTINGS", presentedString: "Settings", imageToDisplay: "gear", toTrack: true)
    
    @Published var homeCards: [HomeCard] = []
}


var HomeCards: [HomeCard] = [HomeCardStore().profileHomeCard, HomeCardStore().bottlesHomeCard, HomeCardStore().sleepHomeCard, HomeCardStore().foodHomeCard, HomeCardStore().medsHomeCard, HomeCardStore().windHomeCard, HomeCardStore().pooHomeCard, HomeCardStore().settingsHomeCard]

//var HomeCards: [HomeCard] = [
//    .init(color: .mint, viewString: "PROFILE", presentedString: "Finley's Profile", imageToDisplay: "figure.child", toTrack: true), // Baby Profile
//    .init(color: .green, viewString: "BOTTLES", presentedString: "Bottles", imageToDisplay: "waterbottle",  toTrack: true), // Bottles
//    .init(color: .indigo, viewString: "SLEEP", presentedString: "Sleep", imageToDisplay: "powersleep", toTrack: false), // Sleep/Naps
//    .init(color: .yellow, viewString: "FOOD", presentedString: "Food", imageToDisplay: "carrot", toTrack: false), // Food
//    .init(color: .red, viewString: "MEDS", presentedString: "Medicene", imageToDisplay: "pill", toTrack: false), // Meds
//    .init(color: .blue, viewString: "WIND", presentedString: "Wind", imageToDisplay: "wind", toTrack: false), // Wind
//    .init(color: .brown, viewString: "POO", presentedString: "Nappies", imageToDisplay: "toilet", toTrack: false), // Poo
//    .init(color: .gray, viewString: "SETTINGS", presentedString: "Settings", imageToDisplay: "gear", toTrack: true) // Settings
//]

extension [HomeCard] {
    
    func zIndex(_ homeCard: HomeCard) -> CGFloat {
        if let index = firstIndex(where: {$0.id == homeCard.id}) {
            return CGFloat(count) - CGFloat(index)
        }
        return .zero
    }
}

