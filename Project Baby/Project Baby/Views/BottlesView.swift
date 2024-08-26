//
//  BottlesView.swift
//  Project Baby
//
//  Created by Jake thompson on 16/08/2024.
//

import SwiftUI

struct BottlesView: View {
    
    @State var bottleFeedTimer: Bool = false
    @State var bottleFeedButtonLabel: String = "Start Bottle Feed"
    @State var bottleFeedButtonColor: Color = .green
    @AppStorage("projectbaby.bottles\(Date.now.formatted(.dateTime.dayOfYear()))") var bottlesTakenToday: Int = 0
    @State var isOuncesSheetPresented = false
    @State var isBottleListSheetPresented = false
    @State var imagePadding = 10.0 //60.0 for small, 30 for medium
    @State var notesCardColor: Color = .blue
    @State var bottleCardColor: Color = .blue
    @State var ouncesCardColor: Color = .orange
    @State var notesTextColor: Color = .white
    
    @State var cardWidth: CGFloat = 2.5
    @State var outerCardHeight: CGFloat = 5
    @State var innerCardHeight: CGFloat = 15
    @State var bottomCardOpacity: CGFloat = 0.5
    @State var countableValueTextSize: CGFloat = 8.0
    @State var cardColorChange: Bool = false


    
    @State var notesToSave: String = ""
    @State var startTimeeToSave: Date = Date.now
    @State var endTimeeToSave: Date = Date.now
    @State var bottleDuration = 0
    @State var ouncesToSave: CGFloat = 0.0
    
    let timer = Timer.publish(every: 1, on: .main, in: .common).autoconnect()
    var body: some View {
        ScrollView
        {
            VStack
            {
                imageView()
                    .padding(.bottom, imagePadding)
                Group
                {
                    HStack
                    {
                        ZStack
                        {
                            RoundedRectangle(cornerRadius: 10)
                                .fill(bottleCardColor)
                                .frame(width: DeviceDimensions().width/cardWidth, height: DeviceDimensions().height/outerCardHeight)
                                //.padding(.vertical, DeviceDimensions().height/10)
                            
                            Rectangle()
                                .fill(.background)
                                .frame(width: DeviceDimensions().width/cardWidth, height: DeviceDimensions().height/innerCardHeight)
                                .offset(y: DeviceDimensions().height/innerCardHeight)
                                .opacity(bottomCardOpacity)
                            VStack
                            {
                                    Text(String(bottlesTakenToday)).bold().font(.system(size: DeviceDimensions().height/8))
                                        .offset(y: -DeviceDimensions().height/45)

                                        Text("Bottles taken today:").font(.system(size: DeviceDimensions().width/25))
                                        .offset(y: -DeviceDimensions().height/250)
                            }
                        }
                        .padding(.horizontal, 5)
                            .onTapGesture {
                                withAnimation {
                                    print("triggering BottleListView")
                                    isBottleListSheetPresented.toggle()
                                }
                            }
                            .popover(isPresented: $isBottleListSheetPresented, content: {
                                //Text("IT WORKS")
//                                Stepper("Ounces to give:", value: $ouncesToSave, in: 0...10, step: 0.5)
//                                    .padding()
//
                                    BottleListView()

                                    .presentationCompactAdaptation(.sheet)
                            })
                            .sensoryFeedback(.increase, trigger: isOuncesSheetPresented)
                        
                        ZStack
                        {
                            RoundedRectangle(cornerRadius: 10)
                                .fill(ouncesCardColor)
                                .frame(width: DeviceDimensions().width/cardWidth, height: DeviceDimensions().height/outerCardHeight)
                                //.padding(.vertical, DeviceDimensions().height/10)
                            
                            Rectangle()
                                .fill(.background)
                                .frame(width: DeviceDimensions().width/cardWidth, height: DeviceDimensions().height/innerCardHeight)
                                .offset(y: DeviceDimensions().height/innerCardHeight)
                                .opacity(bottomCardOpacity)
                            VStack
                            {
                                    Text(String(format: "%.2f", Double(ouncesToSave))).bold().font(.system(size: DeviceDimensions().height/12))
                                        .offset(y: -DeviceDimensions().height/45)

                                        Text("Ounces(oz):").font(.system(size: DeviceDimensions().width/25))
                                    .offset(y: DeviceDimensions().height/50)
                                
                            }
                        }
                        .padding(.horizontal, 5)
                        
                        .onTapGesture {
                            withAnimation {
                                //Do a popover here of OZ to ML conversion
                            }
                        }
                            .onLongPressGesture {
                            print("Long pressed!")
                            isOuncesSheetPresented.toggle()
                                withAnimation
                                {
                                    
                                    ouncesCardColor = .orange
                                }
                        }
                        .popover(isPresented: $isOuncesSheetPresented, content: {
                            //Text("IT WORKS")
                            Stepper("Ounces to give:", value: $ouncesToSave, in: 0...10, step: 0.25)
                                .padding()
                                
                                .presentationCompactAdaptation(.popover)
                        })
                        .sensoryFeedback(.increase, trigger: isOuncesSheetPresented)
                        .onAppear()
                        {
                            withAnimation {
                                if ouncesToSave == 0.0
                                {
                                    //print("AAAAAA")
                                    ouncesCardColor = .red
                                }
                            }
                        }
                        .onChange(of: ouncesToSave)
                        {
                            withAnimation
                            {
                                if ouncesToSave == 0.0
                                {
                                    ouncesCardColor = .red
                                }
                                else
                                {
                                    ouncesCardColor = .green
                                }
                                if bottlesTakenToday > 0 
                                {
                                    bottleCardColor = .green
                                }
                            }
                        }
                    }
                    Spacer()
                    //.padding(.all, 0)
                    .padding(.vertical, 1)
                    HStack
                    {
                        ZStack
                        {
                            RoundedRectangle(cornerRadius: 10)
                                .fill(notesCardColor)
                                .frame(width: DeviceDimensions().width/1.2, height: DeviceDimensions().height/12)
                            TextField("Add notes for bottle...", text: $notesToSave, onEditingChanged: { (isBegin) in
                                if isBegin {
                                    //print("Begins editing")
                                    withAnimation
                                    {
                                        notesCardColor = .yellow
                                        notesTextColor = .black
                                    }
                                } else {
                                    //print("Finishes editing")
                                }
                            },
                            onCommit: {
                                print("commit")
                                withAnimation {
                                    notesCardColor = .green
                                    notesTextColor = .white
                                }

                            }
                            )
                                .frame(width: DeviceDimensions().width/2)
                                .padding()
                                .multilineTextAlignment(.center)
                                .foregroundColor(notesTextColor)

                            
                        }
                    }
                    .padding(.vertical, 10)

                   
                    Text("\(bottleDuration) seconds").bold().font(/*@START_MENU_TOKEN@*/.title/*@END_MENU_TOKEN@*/)
                        .onReceive(timer) { _ in
                            if ((bottleDuration >= 0) && (bottleFeedTimer == true)) {
                                withAnimation{
                                    bottleDuration += 1
                                }
                            }
                        }
                    ZStack
                    {
                        RoundedRectangle(cornerRadius: 10)
                            .fill(bottleFeedButtonColor)
                            .frame(width: DeviceDimensions().width/2, height: DeviceDimensions().height/15)
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
                                        notesCardColor = .blue
                                        
                                    }
                                }
                            }
                        Text(bottleFeedButtonLabel)
                            .foregroundStyle(.white)
                            .bold()
                        
                    }
                }
            }
            .ignoresSafeArea()
        }
        .onAppear()
        {
            if DeviceDimensions().height == 667.0
            {
                imagePadding = 60.0
                
            }
            else
                if DeviceDimensions().height == 852.0
            {
                imagePadding = 30.0
            }
            else
            {
                imagePadding = 10.0
            }
        }
    }

}

func resetViewForNewBottleFeed()
{
    //Reset all values in view to 0 upon completion of bottle feed
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
