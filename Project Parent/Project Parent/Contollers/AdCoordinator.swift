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
    
    func restorePurchases() async {
            do {
                try await AppStore.sync()
            } catch {
                print(error)
            }
        }
    // [START ad_events]
      func adDidRecordImpression(_ ad: GADFullScreenPresentingAd) {
        print("\(#function) called")
      }

      func adDidRecordClick(_ ad: GADFullScreenPresentingAd) {
        print("\(#function) called")
      }

      func ad(
        _ ad: GADFullScreenPresentingAd,
        didFailToPresentFullScreenContentWithError error: Error
      ) {
        print("\(#function) called")
      }

      func adWillPresentFullScreenContent(_ ad: GADFullScreenPresentingAd) {
        print("\(#function) called")
      }

      func adWillDismissFullScreenContent(_ ad: GADFullScreenPresentingAd) {
        print("\(#function) called")
      }

      // [END ad_events]
  }
