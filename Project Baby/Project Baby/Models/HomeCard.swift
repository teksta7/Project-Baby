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
}

var HomeCards: [HomeCard] = [
    .init(color: .green, viewString: "BOTTLES"), // Bottles
    .init(color: .indigo, viewString: "SLEEP"), // Sleep/Naps
    .init(color: .yellow, viewString: "FOOD"), // Food
    .init(color: .red, viewString: "MEDS"), // Meds
    .init(color: .blue, viewString: "WIND"), // Wind
    .init(color: .brown, viewString: "POO") // Poo
]

extension [HomeCard] {
    
    func zIndex(_ homeCard: HomeCard) -> CGFloat {
        if let index = firstIndex(where: {$0.id == homeCard.id}) {
            return CGFloat(count) - CGFloat(index)
        }
        return .zero
    }
}

