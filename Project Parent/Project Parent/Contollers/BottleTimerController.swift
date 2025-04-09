//
//  BottleTimerController.swift
//  Project Parent
//
//  Created by Jake thompson on 06/03/2025.
//

import Foundation
import Combine

class BottleTimerController: ObservableObject {
    /// String to show in UI
    @Published private(set) var message = "0"

    /// Is the timer running?
    @Published private(set) var isRunning = false
    
    
    /// Bottle duration
    @Published private(set) var bottleDuration = 0

    /// Time that we're counting from
    private var startTime: Date? { didSet { saveStartTime() } }

    /// The timer
    var timer: AnyCancellable?

    init() {
        startTime = fetchStartTime()

        if startTime != nil {
            start()
        }
    }
}

// MARK: - Public Interface

extension BottleTimerController {
    func start() {
        timer?.cancel() // cancel timer if any

        if startTime == nil {
            startTime = Date()
        }

        message = "0"

        timer = Timer
            .publish(every: 1, on: .main, in: .common)
            .autoconnect()
            .sink { [weak self] _ in
                guard
                    let self = self,
                    let startTime = self.startTime
                else { return }

                let now = Date()
                let elapsed = now.timeIntervalSince(startTime)
                //self.bottleDuration += 1
                
                self.bottleDuration = Int(elapsed)
                
                //let elapsedBottleDuration = bottleDuration+Int(elapsed)

//                guard elapsed < 60 else {
//                    self.stop()
//                    return
//                }

                //self.message = String(format: "%0.1f", elapsed)
                self.message = String(format: "%0.0f", elapsed)
                
//                if self.bottleDuration != elapsedBottleDuration {
//                    self.bottleDuration = elapsedBottleDuration
//                }
            }

        isRunning = true
    }

    func stop() {
        timer?.cancel()
        timer = nil
        startTime = nil
        isRunning = false
        message = "0"
        bottleDuration = 0
    }
}

// MARK: - Private implementation

private extension BottleTimerController {
    func saveStartTime() {
        if let startTime = startTime {
            UserDefaults.standard.set(startTime, forKey: "com.teksta.projectparent.bottleStartTime")
        } else {
            UserDefaults.standard.removeObject(forKey: "com.teksta.projectparent.bottleStartTime")
        }
    }

    func fetchStartTime() -> Date? {
        UserDefaults.standard.object(forKey: "com.teksta.projectparent.bottleStartTime") as? Date
    }
}
