//
//  HomeCard.swift
//  Project Baby
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
}

var HomeCards: [HomeCard] = [
    .init(color: .mint, viewString: "PROFILE", presentedString: "Finley's Profile", imageToDisplay: "figure.child"), // Baby Profile
    .init(color: .green, viewString: "BOTTLES", presentedString: "Bottles", imageToDisplay: "waterbottle"), // Bottles
    .init(color: .indigo, viewString: "SLEEP", presentedString: "Sleep", imageToDisplay: "powersleep"), // Sleep/Naps
    .init(color: .yellow, viewString: "FOOD", presentedString: "Food", imageToDisplay: "carrot"), // Food
    .init(color: .red, viewString: "MEDS", presentedString: "Medicene", imageToDisplay: "pill"), // Meds
    .init(color: .blue, viewString: "WIND", presentedString: "Wind", imageToDisplay: "wind"), // Wind
    .init(color: .brown, viewString: "POO", presentedString: "Nappies", imageToDisplay: "toilet"), // Poo
    .init(color: .gray, viewString: "SETTINGS", presentedString: "Settings", imageToDisplay: "gear") // Settings
]

extension [HomeCard] {
    
    func zIndex(_ homeCard: HomeCard) -> CGFloat {
        if let index = firstIndex(where: {$0.id == homeCard.id}) {
            return CGFloat(count) - CGFloat(index)
        }
        return .zero
    }
}

