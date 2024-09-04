//
//  TopMiniCardView.swift
//  Project Baby
//
//  Created by Jake thompson on 16/08/2024.
//

import SwiftUI

struct TopMiniCardView: View {
    @State var average = BottleController().averageBottleDuration
    @State var topText = "ABC"
    @Environment(\.managedObjectContext) private var viewContext

    var body: some View {
        ZStack
        {
            RoundedRectangle(cornerRadius: 25)
                .frame(width: DeviceDimensions().width/1.5, height: DeviceDimensions().height/9)
            VStack
            {
                HStack
                {
                    Image(systemName: "info.circle")
                        .foregroundStyle(.black)
                    
                    Text(topText)
                        .font(.callout)
                        .foregroundStyle(.black)
                }
                Text(String(format: "%.0f", average))
                    .foregroundStyle(.black)
                    
                Text("Press here for charts")
                    .foregroundStyle(.black)
                    .font(.caption2)
                    .padding(0.5)
            }
        }
        .onAppear()
        {
            print("Average was \(average)")
            average = BottleController().averageBottleDuration
            print("Average is now \(average)")
        }
    }
}

#Preview {
    TopMiniCardView()
}
