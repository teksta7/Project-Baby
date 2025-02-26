//
//  GeneralUtil.swift
//  Project Parent
//
//  Created by Jake thompson on 02/09/2024.
//

import Foundation
import SwiftUI
import Combine


enum TimeRange {
    case hourly
    case daily
    case total
}

struct TimeRangePicker: View {
    @Binding var value: TimeRange

    var body: some View {
        Picker(selection: $value.animation(.easeInOut), label: EmptyView()) {
            Text("Hourly Duration").tag(TimeRange.hourly)
            Text("Daily Duration").tag(TimeRange.daily)
            Text("Total Bottles").tag(TimeRange.total)
        }
        .pickerStyle(.segmented)
    }
}

class UtilFunctions
{
    func getAppVersion() -> String {
        return "\(Bundle.main.infoDictionary!["CFBundleShortVersionString"] ?? "")"
    }
    
    func convertSecondsToMinutes(_ seconds: Int) -> String {
        let minutes = seconds / 60
        let secondsLeft = seconds % 60
        return "\(minutes) minutes \(secondsLeft.formatted()) seconds"
    }
    
    func convertSecondsToHours(_ seconds: Int) -> String {
        let hours = seconds / 3600
        let minutesLeft = seconds % 3600 / 60
        return "\(hours) hours \(minutesLeft.formatted()) minutes"
    }
    
    func getMinutes(_ seconds: Int) -> Int {
        let minutes = seconds / 60
        //let secondsLeft = seconds % 60
        return minutes
    }
    
    
    func percentageBetweenTimestamps(startTimestamp: TimeInterval, endTimestamp: TimeInterval, targetTimestamp: TimeInterval) -> Double {
        //start date = dateTime stamp bottle started
        //end date = estimated dateTime stamp for when bottle will end (will use the estimated average)
        //target date = current dateTime stamp at the point this function is called
        let totalTimeInterval = endTimestamp - startTimestamp
        let targetTimeInterval = targetTimestamp - startTimestamp
        let percentage = (targetTimeInterval / totalTimeInterval) * 100
        return percentage
    }
    
    func percentageBetweenDates(startDate: Date, endDate: Date, targetDate: Date) -> Double {
        //start date = dateTime stamp bottle started
        //end date = estimated dateTime stamp for when bottle will end (will use the estimated average)
        //target date = current dateTime stamp at the point this function is called
        let totalTimeInterval = endDate.timeIntervalSince(startDate)
        let targetTimeInterval = targetDate.timeIntervalSince(startDate)
        let percentage = (targetTimeInterval / totalTimeInterval) * 100
        return percentage
    }
    
}

extension Date {
    func adding(minutes: Int) -> Date {
        return Calendar.current.date(byAdding: .minute, value: minutes, to: self)!
    }
}

class Stopwatch: ObservableObject {
    /// String to show in UI
    @Published private(set) var message = "Not running"

    /// Is the timer running?
    @Published private(set) var isRunning = false

    /// Time that we're counting from
    private var startTime: Date? { didSet { saveStartTime() } }

    /// The timer
    private var timer: AnyCancellable?

    init() {
        startTime = fetchStartTime()

        if startTime != nil {
            start()
        }
    }
}

class ReloadViewHelper: ObservableObject {
    func reloadView() {
        objectWillChange.send()
    }
}

// MARK: - Public Interface

extension Stopwatch {
    func start() {
        timer?.cancel() // cancel timer if any

        if startTime == nil {
            startTime = Date()
        }

        message = ""

        timer = Timer
            .publish(every: 0.1, on: .main, in: .common)
            .autoconnect()
            .sink { [weak self] _ in
                guard
                    let self = self,
                    let startTime = self.startTime
                else { return }

                let now = Date()
                let elapsed = now.timeIntervalSince(startTime)

                guard elapsed < 60 else {
                    self.stop()
                    return
                }

                self.message = String(format: "%0.1f", elapsed)
            }

        isRunning = true
    }

    func stop() {
        timer?.cancel()
        timer = nil
        startTime = nil
        isRunning = false
        message = "Not running"
    }
}

// MARK: - Private implementation

private extension Stopwatch {
    func saveStartTime() {
        if let startTime = startTime {
            UserDefaults.standard.set(startTime, forKey: "startTime")
        } else {
            UserDefaults.standard.removeObject(forKey: "startTime")
        }
    }

    func fetchStartTime() -> Date? {
        UserDefaults.standard.object(forKey: "startTime") as? Date
    }
}
