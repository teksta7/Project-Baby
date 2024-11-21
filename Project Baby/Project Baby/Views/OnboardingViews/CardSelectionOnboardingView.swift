//
//  CardSelectionOnboardingView.swift
//  Project Baby
//
//  Created by Jake thompson on 07/10/2024.
//

import SwiftUI

struct CardSelectionOnboardingView: View {
    @ObservedObject var bottleSettings = BottleSettings()
    @AppStorage("com.projectbaby.localTimeBetweenFeeds")var localTimeBetweenFeeds: Double = 0.0
    @State var isTimePickertSheetPresented = false
    @State private var config: TimePickerView.Config = .init(count: 36)
    @AppStorage("setDefaultOunces") var localOunces: Double = UserDefaults.standard.double(forKey: "setDefaultOunces")
    @State var isOuncesSheetPresented = false
    @ObservedObject var homeCardStore: HomeCardStore = HomeCardStore()
    @AppStorage("com.projectbaby.isSleepCardTracked") var isSleepCardTracked: Bool = UserDefaults.standard.bool(forKey: "com.projectbaby.isSleepCardTracked")
    @AppStorage("com.projectbaby.isBottlesCardTracked") var isBottlesCardTracked: Bool = UserDefaults.standard.bool(forKey: "com.projectbaby.isBottlesCardTracked")
    @AppStorage("com.projectbaby.isFoodCardTracked") var isFoodCardTracked: Bool = UserDefaults.standard.bool(forKey: "com.projectbaby.isFoodCardTracked")
    @AppStorage("com.projectbaby.isMedsCardTracked") var isMedsCardTracked: Bool = UserDefaults.standard.bool(forKey: "com.projectbaby.isMedsCardTracked")
    @AppStorage("com.projectbaby.isWindCardTracked") var isWindCardTracked: Bool = UserDefaults.standard.bool(forKey: "com.projectbaby.isWindCardTracked")
    @AppStorage("com.projectbaby.isPooCardTracked") var isPooCardTracked: Bool = UserDefaults.standard.bool(forKey: "com.projectbaby.isPooCardTracked")
    
    //@State private var minutes: Int = 0
    
    var body: some View {
        Text("Card Selection").bold()
            .font(.largeTitle)
            .frame(width: DeviceDimensions().width, alignment: .leading)
            .offset(x: 15)
            .padding()
        Text("Here you can select which baby stat cards to enable, each of which enable you to track different stats about your baby.")
            .frame(width: DeviceDimensions().width/1.1, alignment: .center)
            .padding()
        Text("You can change this at any time in the settings.")
            .frame(width: DeviceDimensions().width/1.1, alignment: .center)

        NavigationView
        {
            Form
            {

                Section(header: Text("Baby stat cards"))
                {
                    Toggle(isOn: $isBottlesCardTracked)
                    {
                        Text("Bottles")
                        Text("Enable the app to track the number of bottles you have given to your baby")
                    }
                    //Toggle(isOn: $homeCardStore.sleepHomeCard.toTrack)
//                    Toggle(isOn: $isSleepCardTracked)
//                    {
//                        Text("Sleep (COMING SOON)")
//                        Text("Enable the app to track the amount sleep your baby has had")
//                    }
//                    Toggle(isOn: $isFoodCardTracked)
//                    {
//                        Text("Food (COMING SOON)")
//                        Text("Enable the app to track the amount of food you give to your baby")
//                    }
//                    Toggle(isOn: $isMedsCardTracked)
//                    {
//                        Text("Medicene (COMING SOON)")
//                        Text("Enable the app to track any medicene administered to your baby")
//                    }
//                    Toggle(isOn: $isWindCardTracked)
//                    {
//                        Text("Wind (COMING SOON)")
//                        Text("Enable the app to track the amount of wind your baby has had")
//                    }
//                    Toggle(isOn: $isPooCardTracked)
//                    {
//                        Text("Nappies (COMING SOON)")
//                        Text("Enable the app to track any nappy changes your baby has had")
//                    }

                }
            }
            //.navigationTitle("Card Selection")
        }
        .onChange(of: isBottlesCardTracked)
        {
            HomeCards[1].toTrack = isBottlesCardTracked
        }
        .onChange(of: isSleepCardTracked)
        {
            HomeCards[2].toTrack = isSleepCardTracked
        }
        .onChange(of: isFoodCardTracked)
        {
            HomeCards[3].toTrack = isFoodCardTracked
        }
        .onChange(of: isMedsCardTracked)
        {
            HomeCards[4].toTrack = isMedsCardTracked
        }
        .onChange(of: isWindCardTracked)
        {
            HomeCards[5].toTrack = isWindCardTracked
        }
        .onChange(of: isPooCardTracked)
        {
            HomeCards[6].toTrack = isPooCardTracked
        }
        .onChange(of: bottleSettings.enableBottleNotification)
        {
            if bottleSettings.enableBottleNotification == true
            {
                Task {
                    await BottleNotificationController().requestNotificationAccessByUser()
                }
            }
        }
    }
}

#Preview {
    CardSelectionOnboardingView()
}
