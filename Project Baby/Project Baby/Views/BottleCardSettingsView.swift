//
//  CardSettingsView.swift
//  Project Baby
//
//  Created by Jake thompson on 18/08/2024.
//

import SwiftUI

struct BottleCardSettingsView: View {
    @ObservedObject var bottleSettings = BottleSettings()
    @AppStorage("com.projectbaby.localTimeBetweenFeeds")var localTimeBetweenFeeds: Double = 0.0
    @State var isTimePickertSheetPresented = false
    @State private var config: TimePickerView.Config = .init(count: 36)
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
                Section(header: Text("Notification Options"))
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
            .navigationTitle("Bottle Settings")
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
    BottleCardSettingsView()
}
