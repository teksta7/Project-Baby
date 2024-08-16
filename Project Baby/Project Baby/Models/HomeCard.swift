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
    //var viewString: String //TO BE USED TO CHECK WHICH VIEW TO DISPLAY ON THE CARD
}

var HomeCards: [HomeCard] = [
    .init(color: .green), // Bottles
    .init(color: .indigo), // Sleep/Naps
    .init(color: .yellow), // Food
    .init(color: .red), // Meds
    .init(color: .blue), // Wind
    .init(color: .brown) // Bowl
]

extension [HomeCard] {
    
    func zIndex(_ homeCard: HomeCard) -> CGFloat {
        if let index = firstIndex(where: {$0.id == homeCard.id}) {
            return CGFloat(count) - CGFloat(index)
        }
        return .zero
    }
}

