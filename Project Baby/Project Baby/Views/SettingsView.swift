//
//  CardSettingsView.swift
//  Project Baby
//
//  Created by Jake thompson on 18/08/2024.
//

import SwiftUI

struct SettingsView: View {
    @ObservedObject var bottleSettings = BottleSettings()
    @AppStorage("com.projectbaby.localTimeBetweenFeeds")var localTimeBetweenFeeds: Double = 0.0
    @State var isTimePickertSheetPresented = false
    @State private var config: TimePickerView.Config = .init(count: 36)
    @AppStorage("setDefaultOunces") var localOunces: Double = UserDefaults.standard.double(forKey: "setDefaultOunces")
    @State var isOuncesSheetPresented = false

    
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
                Section(header: Text("Top statistic to track"))
                {
                    
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

                Section(header: Text("Sleep"))
                {
                    
                }
                Section(header: Text("Food"))
                {
                    
                }
                Section(header: Text("Medicene"))
                {
                    
                }
                Section(header: Text("Wind"))
                {
                    
                }
                Section(header: Text("Nappies"))
                {
                    
                }
                
                
               
                
                
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
    }
}

#Preview {
    SettingsView()
}
