//
//  ImageFileController.swift
//  Project Parent
//
//  Created by Jake thompson on 07/10/2024.
//


import Foundation
import SwiftUI
import UIKit

class ImageFileController
{
    func saveBabyProfilePic(imageData: Data) {
        let encoded = try! PropertyListEncoder().encode(imageData)
        UserDefaults.standard.set(encoded, forKey: "babyProfilePic")
    }
    
    func loadBabyProfilePic() -> UIImage {
        guard let data = UserDefaults.standard.data(forKey: "babyProfilePic") else { return UIImage(systemName: "figure.cardio")! }
         let decoded = try! PropertyListDecoder().decode(Data.self, from: data)
         let image = UIImage(data: decoded)
        return image!
    }
}

