//
//  AdCoordinator.swift
//  Project Parent
//
//  Created by Jake thompson on 22/11/2024.
//

import Foundation
import GoogleMobileAds
import SwiftUI
import StoreKit


class AdCoordinator: NSObject, GADFullScreenContentDelegate {
    private var appAd: GADInterstitialAd?
    @AppStorage("projectparent.toggleInAppAds") var adsEnabled: Bool = UserDefaults.standard.bool(forKey: "projectparent.toggleInAppAds")
    //@AppStorage("toggleInAppAds") var adsEnabled: Bool = UserDefaults.standard.bool(forKey: "toggleInAppAds")
    //@ObservedObject var settings = UserSettings()
    let GADApplicationIdentifierString = Bundle.main.object(forInfoDictionaryKey: "GADApplicationIdentifier") as? String
    let GADAdUnitIdString = Bundle.main.object(forInfoDictionaryKey: "GADAdUnitId") as? String



    func loadAd() async {
      do {
          //let GADAdUnitIdSafeString = GADApplicationIdentifierString
          //let GADAdUnitIdSafeString = GADApplicationIdentifierString!.replacingOccurrences(of: "~", with: "/", options: .literal, range: nil)
          //print (GADAdUnitIdSafeString ?? "")
          
          //TEST AdUnitID
          //appAd = try await GADInterstitialAd.load(withAdUnitID: "ca-app-pub-3940256099942544/4411468910", request: GADRequest())
          
          //PROD AdUnitID
          appAd = try await GADInterstitialAd.load(withAdUnitID: "ca-app-pub-2416589095814484/9850695661", request: GADRequest())
          //appAd = try await GADInterstitialAd.load(withAdUnitID: GADAdUnitIdSafeString ?? "", request: GADRequest())
          appAd?.fullScreenContentDelegate = self
      } catch {
        print("Failed to load interstitial ad with error: \(error.localizedDescription)")
      }
    }

    func showAd() {
      guard let appAd = appAd else {
        return print("Ad wasn't ready.")
      }
        appAd.present(fromRootViewController: nil)
    }

    // MARK: - GADFullScreenContentDelegate methods

    func adDidDismissFullScreenContent(_ ad: GADFullScreenPresentingAd) {
        appAd = nil
    }
    
    func disableAds()
    {
        print("Setting disable Ad flag")
        adsEnabled = false
        print("Flag set to: " + String(adsEnabled))
    }
    
    func enableAds()
    {
        print("Setting enable Ad flag")
        adsEnabled = true
        print("Flag set to: " + String(adsEnabled))
    }
    
    func adFlagChecker()
    {
        print("ad flag is set to " + String(adsEnabled))
        if UserDefaults.standard.bool(forKey: "tempToggleInAppAds") == false
        {
            print("Maintaining purchase flag from app reset " + String(adsEnabled))
            adsEnabled = false
        }
    }
    
    func restorePurchases() async {
            do {
                try await AppStore.sync()
            } catch {
                print(error)
            }
        }
  }


//class AdCoordinator: NSObject, GADFullScreenContentDelegate {
//    @AppStorage("projectparent.toggleInAppAds") var adsEnabled: Bool = UserDefaults.standard.bool(forKey: "projectparent.toggleInAppAds")
//    
//    private var interstitialAd: GADInterstitialAd?
//    
//    func loadAd() {
//        GADInterstitialAd.load(withAdUnitID: "ca-app-pub-3940256099942544/4411468910", request: GADRequest()) { ad, _ in
//            self.interstitialAd = ad
//            self.interstitialAd?.fullScreenContentDelegate = self
//        }
//        
//        func adDidDismissFullScreenContent(_ ad: GADFullScreenPresentingAd) {
//            interstitialAd = nil
//        }
//        
//        func showAd() {
//            guard let interstitialAd = interstitialAd else { return print("Ad not ready") }
//            //interstitialAd.present(fromRootViewController: UIApplication.shared.windows.first?.rootViewController!)
//            interstitialAd.present(fromRootViewController: nil)
//        }
//    }
//}

//class AdCoordinator: NSObject, GADFullScreenContentDelegate {
//    private var appAd: GADInterstitialAd?
//    private var appNativeAd: GADNativeAd?
//    @AppStorage("projectparent.toggleInAppAds") var adsEnabled: Bool = UserDefaults.standard.bool(forKey: "projectparent.toggleInAppAds")
//    //@ObservedObject var settings = UserSettings()
//    let GADApplicationIdentifierString = Bundle.main.object(forInfoDictionaryKey: "GADApplicationIdentifier") as? String
//    let GADAdUnitIdString = Bundle.main.object(forInfoDictionaryKey: "GADAdUnitId") as? String
//    
//    func loadAd() async {
//      do {
//          let GADAdUnitIdSafeString = GADApplicationIdentifierString
//          //let GADAdUnitIdSafeString = GADApplicationIdentifierString!.replacingOccurrences(of: "~", with: "/", options: .literal, range: nil)
//          //print (GADAdUnitIdSafeString ?? "")
//          //appAd = try await GADInterstitialAd.load(withAdUnitID: "ca-app-pub-2416589095814484/5400585890", request: GADRequest())
//          
//          //appNativeAd = try await GADNativeAd.load()
//          
//          appAd?.fullScreenContentDelegate = self
//      } catch {
//        print("Failed to load Native ad with error: \(error.localizedDescription)")
//      }
//    }
//    
//    func showAd()
//        {
//            
//        }
////    } func showAd() async {
////        do
////        {
////            
////        }
////        catch
////        {
////            print("Failed to load Native ad with error: \(error.localizedDescription)")
////        }
////    }
//
//
////
////    func loadAd() async {
////      do {
////          let GADAdUnitIdSafeString = GADApplicationIdentifierString
////          //let GADAdUnitIdSafeString = GADApplicationIdentifierString!.replacingOccurrences(of: "~", with: "/", options: .literal, range: nil)
////          //print (GADAdUnitIdSafeString ?? "")
////          //appAd = try await GADInterstitialAd.load(withAdUnitID: "ca-app-pub-3940256099942544/3986624511", request: GADRequest())
////          appAd = try await GADNativeAd.load()
////          //appAd = try await GADInterstitialAd.load(withAdUnitID: GADAdUnitIdSafeString ?? "", request: GADRequest())
////          appAd?.fullScreenContentDelegate = self
////      } catch {
////        print("Failed to load interstitial ad with error: \(error.localizedDescription)")
////      }
////    }
////
////    func showAd() {
////      guard let appAd = appAd else {
////        return print("Ad wasn't ready.")
////      }
////
////        appAd.present(fromRootViewController: nil)
////    }
////
////    // MARK: - GADFullScreenContentDelegate methods
////
////    func adDidDismissFullScreenContent(_ ad: GADFullScreenPresentingAd) {
////        appAd = nil
////    }
////    
////    func disableAds()
////    {
////        print("Setting disable Ad flag")
////        adsEnabled = false
////        print("Flag set to: " + String(adsEnabled))
////        //print("Flag set to: " + String(settings.isAdsEnabled))
////        //UserDefaults.standard.set(108, forKey: "203")
////        
////    }
////    func adFlagChecker()
////    {
////        print("ad flag is set to " + String(adsEnabled))
////        if UserDefaults.standard.bool(forKey: "tempToggleInAppAds") == false
////        {
////            print("Maintaining purchase flag from app reset " + String(adsEnabled))
////            adsEnabled = false
////        }
////    }
//  }
