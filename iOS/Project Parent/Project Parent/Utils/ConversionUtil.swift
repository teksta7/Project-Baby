//
//  ConversionUtil.swift
//  Project Parent
//
//  Created by Jake thompson on 26/08/2024.
//

import Foundation

class ConversionUtil
{
    func getDateDiff(start: Date, end: Date) -> Int  {
        let calendar = Calendar.current
        let dateComponents = calendar.dateComponents([Calendar.Component.second], from: start, to: end)

        let seconds = dateComponents.second
        return Int(seconds!)
    }
}
