//
//  GeneralUtil.swift
//  Project Baby
//
//  Created by Jake thompson on 02/09/2024.
//

import Foundation
import SwiftUI


enum TimeRange {
    case hourly
    case daily
    case ounces
}

struct TimeRangePicker: View {
    @Binding var value: TimeRange

    var body: some View {
        Picker(selection: $value.animation(.easeInOut), label: EmptyView()) {
            Text("Hourly").tag(TimeRange.hourly)
            Text("Daily").tag(TimeRange.daily)
            Text("Ounces").tag(TimeRange.ounces)
        }
        .pickerStyle(.segmented)
    }
}
