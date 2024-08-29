//
//  TopMiniCardView.swift
//  Project Baby
//
//  Created by Jake thompson on 16/08/2024.
//

import SwiftUI

struct TopMiniCardView: View {
    @State var average = BottleController().averageBottleDuration
    @Environment(\.managedObjectContext) private var viewContext

    var body: some View {
        ZStack
        {
            RoundedRectangle(cornerRadius: 25)
                .frame(width: DeviceDimensions().width/1.5, height: DeviceDimensions().height/10)
            VStack
            {
                HStack
                {
                    Image(systemName: "info.circle")
                        .foregroundStyle(.black)
                    
                    Text("Average bottle feed time:")
                        .font(.callout)
                        .foregroundStyle(.black)
                }
                Text(String(format: "%.0f", average))
                    .foregroundStyle(.black)
            }
        }
        .onAppear()
        {
            print("Average was \(average)")
            average = BottleController().averageBottleDuration
            print("Average is now \(average)")
        }
//        .onChange(of: BottleController().averageBottleDuration)
//        {
//            
//
//        }
    }
}

#Preview {
    TopMiniCardView()
}
