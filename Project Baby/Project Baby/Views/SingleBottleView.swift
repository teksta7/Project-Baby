//
//  SingleBottleView.swift
//  Project Baby
//
//  Created by Jake thompson on 29/08/2024.
//

import SwiftUI

struct SingleBottleView: View {
    var bottle: Bottle
    
    var body: some View {
        ZStack
        {

            RoundedRectangle(cornerRadius: 15.0)
                .frame(width: DeviceDimensions().width/1.25, height: DeviceDimensions().height/1.5)
                .foregroundStyle(.green)
            Rectangle()
                .fill(.background)
                .frame(width: DeviceDimensions().width/1.25, height: DeviceDimensions().height/3)
                .offset(y: DeviceDimensions().height/3)
                .opacity(0.5)
            VStack
            {
                Image(systemName: "waterbottle")
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .frame(width: DeviceDimensions().width/1.5, height:  DeviceDimensions().height/2.5)
                    //.offset(y: DeviceDimensions().height/10)
                    .padding(.all, 30)
                
                Text("Bottle Details").font(.title).bold()
                   // .padding(.vertical, 2)
                HStack
                {
                    Text("Ounces given: ")
                    Text(String(bottle.ounces))
                }
                HStack
                {
                    Text("Bottle Duration: ")
                    Text(String(format: "%.0f", bottle.duration) + " seconds")
                }
                HStack
                {
                    Text("Notes: ")
                    Text(String(bottle.addtional_notes ?? ""))
                }
            }
        }
        .navigationTitle("Bottle at: \(bottle.start_time?.formatted(date: .omitted, time: .shortened) ?? " ")")
    }
}

//#Preview {
//    SingleBottleView()
//}
