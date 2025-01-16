//
//  CardSettingsView.swift
//  Project Parent
//
//  Created by Jake thompson on 18/08/2024.
//

import SwiftUI

struct SettingsView: View {
    @ObservedObject var bottleSettings = BottleSettings()
    @AppStorage("com.projectparent.localTimeBetweenFeeds")var localTimeBetweenFeeds: Double = 0.0
    @State var isTimePickertSheetPresented = false
    @State private var config: TimePickerView.Config = .init(count: 36)
    @AppStorage("setDefaultOunces") var localOunces: Double = UserDefaults.standard.double(forKey: "setDefaultOunces")
    @State var isOuncesSheetPresented = false
    @ObservedObject var homeCardStore: HomeCardStore = HomeCardStore()
    @AppStorage("com.projectparent.isSleepCardTracked") var isSleepCardTracked: Bool = UserDefaults.standard.bool(forKey: "com.projectparent.isSleepCardTracked")
    @AppStorage("com.projectparent.isBottlesCardTracked") var isBottlesCardTracked: Bool = UserDefaults.standard.bool(forKey: "com.projectparent.isBottlesCardTracked")
    @AppStorage("com.projectparent.isFoodCardTracked") var isFoodCardTracked: Bool = UserDefaults.standard.bool(forKey: "com.projectparent.isFoodCardTracked")
    @AppStorage("com.projectparent.isMedsCardTracked") var isMedsCardTracked: Bool = UserDefaults.standard.bool(forKey: "com.projectparent.isMedsCardTracked")
    @AppStorage("com.projectparent.isWindCardTracked") var isWindCardTracked: Bool = UserDefaults.standard.bool(forKey: "com.projectparent.isWindCardTracked")
    @AppStorage("com.projectparent.isPooCardTracked") var isPooCardTracked: Bool = UserDefaults.standard.bool(forKey: "com.projectparent.isPooCardTracked")
    
    @EnvironmentObject
    private var IAP: IAPController
    
    //@State private var minutes: Int = 0
    
    var body: some View {
        //Text("This will be the card settings view")
        NavigationView
        {
            Form
            {
                Section(header: Text("Information")) {
                    LabeledContent("App Version", value: UtilFunctions().getAppVersion())
                }
                Section(header: Text("Purchase Options"))
                {
                    if IAP.hasUnlockedNoAds == true {
                        Text("You have purchased no ads, enjoy!")
                        }
                    else
                    {
                        ForEach(IAP.products) { product in
                            Button ("Remove Ads")
                            {
                                Task
                                {
                                    do
                                    {
                                        try await
                                        //IAP.purchase(product)
                                        IAP.purchase(IAP.products[0])
                                        AdCoordinator().disableAds()
                                    }
                                    catch {
                                        print (error)
                                    }
                                }
                            }
                            .foregroundStyle(.blue)
                            
                        }
                        Button("Restore Purchases") {
                            Task {
                                await AdCoordinator().restorePurchases()
                            }
                        }
                        .foregroundStyle(.blue)
                        .frame(height: 15, alignment: .center)
//                        ForEach(IAP.products) { product in
//                            Button ("Remove Ads") {
//                                Task
//                                {
//                                    do
//                                    {
//                                        try await IAP.purchase(product)
//                                        AdCoordinator().disableAds()
//                                    }
//                                    catch {
//                                        print(error)
//                                    }
//                                }
//                            }
//                            Button("Restore Purchases") {
//                                Task {
//                                    await AdCoordinator().restorePurchases()
//                                }
//                            }
//                            .frame(height: 15, alignment: .center)
//                        }
                    }
                }
                Section(header: Text("Baby stat cards"))
                {
                    Text("More coming soon...")
                    //Text("Top statistic to track(TBC)")
                    //ENABLE IN A LATER VERSION
                    Toggle(isOn: $isBottlesCardTracked)
                    {
                        Text("Bottles")
                        Text("Enable the app to track the number of bottles you have given to your baby")
                    }
//                    //Toggle(isOn: $homeCardStore.sleepHomeCard.toTrack)
//                    //ENABLE FOR MILESTONE 3
//                    Toggle(isOn: $isSleepCardTracked)
//                    {
//                        Text("Sleep (COMING SOON)")
//                        Text("Enable the app to track the amount sleep your baby has had")
//                    }
//                    //ENABLE FOR MILESTONE 6
//                    Toggle(isOn: $isFoodCardTracked)
//                    {
//                        Text("Food (COMING SOON)")
//                        Text("Enable the app to track the amount of food you give to your baby")
//                    }
//                    //ENABLE FOR MILESTONE 5
//                    Toggle(isOn: $isMedsCardTracked)
//                    {
//                        Text("Medicene (COMING SOON)")
//                        Text("Enable the app to track any medicene administered to your baby")
//                    }
//                    //ENABLE FOR MILESTONE 4
//                    Toggle(isOn: $isWindCardTracked)
//                    {
//                        Text("Wind (COMING SOON)")
//                        Text("Enable the app to track the amount of wind your baby has had")
//                    }
//                    //ENABLE FOR MILESTONE 2
//                    Toggle(isOn: $isPooCardTracked)
//                    {
//                        Text("Nappies (COMING SOON)")
//                        Text("Enable the app to track any nappy changes your baby has had")
//                    }

                }
                Section(header: Text("Bottles"))
                {
                    Toggle(isOn: $bottleSettings.enableBottleNotification)
                    {
                        Text("Next bottle feed")
                        Text("This will notify you when the next bottle feed is due using the duration you have given below")
                    }
                    HStack
                    {
                        Button("Select a duration:", action: {
                            withAnimation(.spring(response: 0.6, dampingFraction: 0.7))
                            {
                                isTimePickertSheetPresented.toggle()
                                //deleteAllKickData()
                            }
                            
                            
                        })
                        .foregroundStyle(.cyan)
                        .bold()
                        Text("\(String(format: "%.0f",localTimeBetweenFeeds)) Minutes")
                        //                    Stepper("Time between bottle feeds: \(localTimeBetweenFeeds)", value: $localTimeBetweenFeeds.animation(), in: 0...10, step: 1)
                        
                        
                    }
                    HStack
                    {
                        Button("Set default ounces:", action: {
                            isOuncesSheetPresented.toggle()
                        })
                        .foregroundStyle(.cyan)
                        .bold()
                        Text("\(String(format: "%.2f",localOunces)) oz")
                    }
                    .popover(isPresented: $isOuncesSheetPresented, content: {
                        //Text("IT WORKS")
                        Stepper("Ounces to give:", value: $localOunces, in: 0...10, step: 0.25)
                            .padding()
                        
                            .presentationCompactAdaptation(.popover)
                    })
                    HStack
                    {
                        Text("Set default note:")
                            .foregroundStyle(.cyan)
                            .bold()
                        TextField("Enter default note", text: $bottleSettings.setDefaultBottleNote)
                    }
                }

                //RE-ENABLE THIS SECTION IN PARTS
//                Section(header: Text("Sleep"))
//                {
//                    
//                }
//                Section(header: Text("Food"))
//                {
//                    
//                }
//                Section(header: Text("Medicene"))
//                {
//                    
//                }
//                Section(header: Text("Wind"))
//                {
//                    
//                }
//                Section(header: Text("Nappies"))
//                {
//                    
//                }
                
                
               
                
                
                //.foregroundStyle(settingsTextColour)
                //                HStack
                //                {
                //                    Text("Export data file type")
                //                    Menu(settings.setDefaultShareFormat) {
                //                        Button("JPEG")
                //                        {
                //                            settings.setTheme = "JPEG"
                //                        }
                //                        Button("PNG")
                //                        {
                //                            settings.setTheme = "PNG"
                //
                //                        }
                //                        Button("PDF")
                //                        {
                //                            settings.setTheme = "PDF"
                //                        }
                //                    }
                //                }
               
                
                                    
            }
            .navigationTitle("Settings")
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
        .popover(isPresented: $isTimePickertSheetPresented, content: {
            TimePickerView(config: config, minutes: $localTimeBetweenFeeds)
                .presentationCompactAdaptation(.sheet)
                .frame(height: 60)
        })
        //.onAppear()
        //{
        .task {
            Task {
                do {
                    try await IAP.loadProducts()
                } catch {
                    print(error)
                }
            }
        }
    }
}

#Preview {
    SettingsView()
        .environmentObject(IAPController())
}
