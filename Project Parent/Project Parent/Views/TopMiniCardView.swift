//
//  TopMiniCardView.swift
//  Project Parent
//
//  Created by Jake thompson on 16/08/2024.
//

import SwiftUI

struct TopMiniCardView: View {
    //@State var average = BottleController().averageBottleDuration
    @State var average = "Need more data to calculate average"
    @State var topText = "ABC"
    @Environment(\.managedObjectContext) private var viewContext

    var body: some View {
        ZStack
        {
            RoundedRectangle(cornerRadius: 25)
                .frame(width: DeviceDimensions().width/1.5, height: DeviceDimensions().height/8)
                .foregroundStyle(.indigo)
            VStack
            {
                HStack
                {
                    Image(systemName: "info.circle")
                        .foregroundStyle(.white)
                    
                    Text(topText)
                        .font(.callout)
                        .foregroundStyle(.white)
                }
                .padding(0.5)

                //Text(String(format: "%.0f", average))
                Text(average)
                    .foregroundStyle(.white)
                    
                Text("Press here for charts")
                    .foregroundStyle(.white)
                    .font(.caption2)
                    .padding(0.5)
            }
        }
        .onAppear()
        {
            print("Average was \(average)")
            average = UtilFunctions().convertSecondsToMinutes( Int(BottleController().averageBottleDuration))
            print("Average is now \(average)")
        }
    }
}

#Preview {
    TopMiniCardView()
}
