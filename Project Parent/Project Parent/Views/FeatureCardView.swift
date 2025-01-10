//
//  FeatureCardView.swift
//  Project Parent
//
//  Created by Jake thompson on 22/08/2024.
//

import SwiftUI

struct FeatureCardView: View {
    //@Binding var countableValue: String
    @State var countableValue: String = "0"
    @State var countableValueTextSize: CGFloat = 8.0
    @State var bottomLabelToSet: String = ""
    @State var cardColor: Color = .blue
    @State var cardColorChange: Bool = false
    @State var sheetView: String = ""
    @State var cardWidth: CGFloat = 2.5
    @State var outerCardHeight: CGFloat = 5
    @State var innerCardHeight: CGFloat = 15
    @State var bottomCardOpacity: CGFloat = 0.5

    var body: some View {
        ZStack
        {
            RoundedRectangle(cornerRadius: 10)
                .fill(cardColor)
                .frame(width: DeviceDimensions().width/cardWidth, height: DeviceDimensions().height/outerCardHeight)
                //.padding(.vertical, DeviceDimensions().height/10)
            
            Rectangle()
                .fill(.background)
                .frame(width: DeviceDimensions().width/cardWidth, height: DeviceDimensions().height/innerCardHeight)
                .offset(y: DeviceDimensions().height/innerCardHeight)
                .opacity(bottomCardOpacity)
            VStack
            {
                if countableValue != " "
                {
                    Text(String(countableValue)).bold().font(.system(size: DeviceDimensions().height/countableValueTextSize))
                        .offset(y: -DeviceDimensions().height/45)
                }
                if bottomLabelToSet != " "
                {
                    Text(bottomLabelToSet).font(.system(size: DeviceDimensions().width/25))
                        .offset(y: -DeviceDimensions().height/250)
                }
            }
        }
        .padding(.horizontal, 5)
        
        .onTapGesture {
            withAnimation {
                cardColorChange.toggle()
                if cardColorChange == true
                {
                    cardColor = .green
                }
                else
                {
                    cardColor = .blue
                }
            }
        }
        //.padding(.vertical, DeviceDimensions().height/25)
    }
}

#Preview {
    FeatureCardView()
}
