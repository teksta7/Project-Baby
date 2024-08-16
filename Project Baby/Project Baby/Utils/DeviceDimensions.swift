//
//  DeviceDimensions.swift
//  Project Baby
//
//  Created by Jake thompson on 16/08/2024.
//

import Foundation
import UIKit

class DeviceDimensions {
    static var screen = DeviceDimensions()
    #if os(watchOS)
    var width: CGFloat = WKInterfaceDevice.current().screenBounds.size.width
    var height: CGFloat = WKInterfaceDevice.current().screenBounds.size.height
    #elseif os(iOS)
    var width: CGFloat = UIScreen.main.bounds.size.width
    var height: CGFloat = UIScreen.main.bounds.size.height
    #elseif os(macOS)
    // You could implement this to force a CGFloat and get the full device screen size width regardless of the window size with .frame.size.width
    var width: CGFloat? = NSScreen.main?.visibleFrame.size.width
    var height: CGFloat? = NSScreen.main?.visibleFrame.size.height
    #endif
}
