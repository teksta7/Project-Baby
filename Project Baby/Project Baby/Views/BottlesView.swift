//
//  BottlesView.swift
//  Project Baby
//
//  Created by Jake thompson on 16/08/2024.
//

import SwiftUI

struct BottlesView: View {
    
    @State var timeRemaining = 0
    @State var bottleFeedTimer: Bool = false
    @State var bottleFeedButtonLabel: String = "Start Bottle Feed"
    @State var bottleFeedButtonColor: Color = .green
    let timer = Timer.publish(every: 1, on: .main, in: .common).autoconnect()
    var body: some View {
        ScrollView
        {
            VStack
            {
                imageView()
                Group
                {
                    HStack
                    {
                        ZStack
                        {
                            RoundedRectangle(cornerRadius: 10)
                                .fill(.blue)
                                .frame(width: DeviceDimensions().width/2.5, height: DeviceDimensions().height/5)
                                .padding(.vertical, DeviceDimensions().height/10)
                            
                            Rectangle()
                                .fill(.background)
                                .frame(width: DeviceDimensions().width/2.5, height: DeviceDimensions().height/15)
                                .offset(y: DeviceDimensions().height/15)
                                .opacity(0.5)
                            VStack
                            {
                                Text("0").bold().font(.system(size: DeviceDimensions().height/8))
                                    .offset(y: -DeviceDimensions().height/50)
                                Text("Bottles taken today:").font(.system(size: DeviceDimensions().width/25))
                                    .offset(y: -DeviceDimensions().height/250)
                            }
                        }
                        .padding(.vertical, DeviceDimensions().height/25)
                    }
                    HStack
                    {
                        //this needs to be a dropdown
                        Text("Ounces:")
                        Text("1oz")
                    }
                    List {
                        Text("A List Item")
                        Text("A Second List Item")
                        Text("A Third List Item")
                    }
                    Text("\(timeRemaining)")
                        .onReceive(timer) { _ in
                            if ((timeRemaining >= 0) && (bottleFeedTimer == true)) {
                                timeRemaining += 1
                            }
                        }
                    ZStack
                    {
                        RoundedRectangle(cornerRadius: 10)
                            .fill(bottleFeedButtonColor)
                            .frame(width: DeviceDimensions().width/2, height: DeviceDimensions().height/10)
                            .onTapGesture {
                                withAnimation
                                {
                                    bottleFeedTimer.toggle()
                                    if bottleFeedTimer == true
                                    {
                                        bottleFeedButtonLabel = "Stop Bottle Feed"
                                        bottleFeedButtonColor = .orange
                                    }
                                    else
                                    {
                                        bottleFeedButtonLabel = "Start Bottle Feed"
                                        bottleFeedButtonColor = .green
                                        
                                    }
                                }
                            }
                        Text(bottleFeedButtonLabel)
                            .foregroundStyle(.white)
                            .bold()
                        
                    }
                }
                //.padding(.vertical, DeviceDimensions().height/399)

            }
            .ignoresSafeArea()
            //.navigationTitle("Bottles")
        }
    }
}

#Preview {
    BottlesView()
}

@ViewBuilder
func imageView() -> some View
{
    GeometryReader{ geo in
        let minY = geo.frame(in: .global).minY
        let minX = geo.frame(in: .global).minX
        let isScrolling = minY > 0
        VStack
        {
            Image(.test).resizable().scaledToFill()
                .frame(height: isScrolling ? 160 + minY/3 : 160 )
                .clipped()
                .offset(y: isScrolling ? -minY : 0)
                .offset(x: isScrolling ? -minX : 0)
                .blur(radius: isScrolling ? 0 + minY / 80 : 0)
                .scaleEffect(isScrolling ? 1 + minY / 2000 : 1)
                .overlay(alignment: .bottom)
                {
                  ZStack
                    {
                        Image(.test).resizable().scaledToFill()
                        Circle().stroke(lineWidth: 60)
                    }
                    .frame(width: 110, height: 160)
                    .clipShape(/*@START_MENU_TOKEN@*/Circle()/*@END_MENU_TOKEN@*/)
                    .offset(y: 50)
                    .offset(y: isScrolling ? -minY : 0)
                }
            Group
            {
                VStack
                {
                    Text("Bottles").bold().font(.title)
                }
                .offset(y: isScrolling ? -minY : 0)

            }
            .padding(.vertical, DeviceDimensions().height/30)

        }
    }
    .frame(height: DeviceDimensions().height/5)
    
}
