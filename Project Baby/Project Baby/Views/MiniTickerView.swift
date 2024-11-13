//
//  BottomMiniCardView.swift
//  Project Baby
//
//  Created by Jake thompson on 18/08/2024.
//

import SwiftUI
import Foundation

struct MiniTickerView: View {
    //ROTATE THIS INFO IN STRINGS
    //Time until next bottle is due
    //Wake windows today
    //Food eaten today
    //Any medicene due
    //amount of wind
    //amount of nappy changes
    
    @State var msg = "Welcome"
    @State var bottleNextTime = BottleController().nextBottleNotificationDateTime
    //@State var messagesToShow: [String] = ["Next bottle is due: \(UserDefaults.standard.string(forKey: "projectbaby.nextBottleNotificationDateTime") ?? "N/A")", "Wake windows today: N/A ", "Average time between bottles: \(UtilFunctions().convertSecondsToHours (Int(UserDefaults.standard.double(forKey: "projectbaby.averagetimebetweenbottles"))))" , "Food eaten today: N/A ", "Any medicene due: N/A", "Amount of wind: N/A", "Amount of nappy changes: N/A"]
    @State var messagesToShow: [String] = ["", "", "" , "", "", "", ""]
    //@State var imagesToShow: [String] = ["waterbottle", "powersleep", "waterbottle" , "carrot", "pill", "wind", "toilet"]
    @State var colorsToShow: [Color] = [.blue, .purple, .indigo]
    @State var colorsToShowIndex: Int = 0
    @State var messagesArrayIndex: Int = 0
    @State var now = Date()

    
    var body: some View {
        

        let timer = Timer.publish(every: 3, on: .current, in: .common).autoconnect()

        ZStack
        {
            RoundedRectangle(cornerRadius: 15)
                .frame(width: DeviceDimensions().width/1.5, height: DeviceDimensions().height/15)
                .foregroundStyle(colorsToShow[colorsToShowIndex])
            HStack
            {
                Image(systemName: "hand.point.right")
                    .foregroundStyle(.white)
                Text("\(msg)")
                    .font(.caption)
                    .foregroundStyle(.white)
                    .multilineTextAlignment(.center)
                    .frame(width: DeviceDimensions().width/2.25, height: DeviceDimensions().height/2)
                    //.foregroundStyle(.black)
                    .onReceive(timer) {_ in
                                //self.now = Date()
                        withAnimation
                        {
                            if messagesArrayIndex == 0
                            {
                                msg = "Next bottle is due: \(BottleController().nextBottleNotificationDateTime)"
                            }
                            if messagesArrayIndex == 1
                            {
                                msg = "Wake windows today: N/A"
                            }
                            if messagesArrayIndex == 2
                            {
                                msg = "Average time between bottles: \(UtilFunctions().convertSecondsToHours (Int(UserDefaults.standard.double(forKey: "projectbaby.averagetimebetweenbottles"))))"
                            }
                            if messagesArrayIndex == 3
                            {
                                msg = "Food eaten today: N/A"
                            }
                            if messagesArrayIndex == 4
                            {
                                msg = "Any medicene due: N/A"
                            }
                            if messagesArrayIndex == 5
                            {
                                msg = "Amount of wind: N/A"
                            }
                            if messagesArrayIndex == 6
                            {
                                msg = "Amount of nappy changes: N/A"
                            }
//                            else
//                            {
//                                msg = messagesToShow[messagesArrayIndex]
//                            }
                        messagesArrayIndex += 1
                        colorsToShowIndex += 1
                            if messagesArrayIndex >= messagesToShow.count {
                                messagesArrayIndex = 0
                            }
                                if colorsToShowIndex >= colorsToShow.count {
                                    colorsToShowIndex = 0
                                }
                        }
                            }
                
//                Image(systemName: "gearshape")
//                    .foregroundStyle(.black)
//    
//                Text("Settings")
//                    .font(.title2)
//                    .foregroundStyle(.black)
            }
        }
//        .onAppear()
//        {
//            print("onAppear Ticker View")
//            BottleController().calculateAverageTimeBetweenBottles()
//        }
    }
}

#Preview {
    MiniTickerView()
}
