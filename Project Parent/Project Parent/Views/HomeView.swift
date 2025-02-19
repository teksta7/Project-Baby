//
//  HomeView.swift
//  Project Parent
//
//  Created by Jake thompson on 15/08/2024.
//

import SwiftUI
import CoreData

struct HomeView: View {
    @State var showIndicator: Bool = false
    @State private var isRotationEnabled: Bool = true
    //@State var activeCard: CGFloat = 0.0
    @State private var cardScale = 1.0
    @State var isChartSheetPresented = false
    @State var isCardSettingsSheetPresented = false
    @Environment(\.managedObjectContext) private var viewContext
    @State var localHomeCards = HomeCards

    var body: some View
    {
        NavigationStack
        {
            VStack
            {
                Spacer(minLength: 10)
                //INSERT TOP MINI VIEW
                //TopMiniCardView(average: BottleController().averageBottleDuration, topText: "Average bottle feed time:")
                TopMiniCardView(average: UtilFunctions().convertSecondsToMinutes( Int(BottleController().averageBottleDuration)), topText: "Average bottle feed time:")
                    .onTapGesture {
                        isChartSheetPresented.toggle()
                        print(String(isChartSheetPresented))
                    }
                    .sheet(isPresented: $isChartSheetPresented, content: {
                        BottleChartsView()
                            //.environment(\.colorScheme, .dark)

                    })
                GeometryReader
                {
                    let size = $0.size
                        ScrollView(.horizontal)
                        {
                            HStack(spacing: 0)
                            {
                                ForEach(localHomeCards) { homeCard in
                                    if homeCard.toTrack == true
                                    {
                                        HomeCardView(homeCard)
                                            .padding(.horizontal, 65)
                                            .frame(width: size.width)
                                            .visualEffect { content, geometryProxy in
                                                content
                                                    .scaleEffect(scale(geometryProxy, scale: 0.1), anchor: .trailing)
                                                    .rotationEffect(rotation(geometryProxy, rotation: 5))
                                                    .offset(x: minX(geometryProxy))
                                                    .offset(x: excessMinX(geometryProxy, offset: 5))
                                                    .opacity(cardScale)
                                                //.scaleEffect(cardScale)
                                                
                                            }
                                            .zIndex(HomeCards.zIndex(homeCard))
                                    }
                                }
                                
                            }
                        
                            .navigationDestination(for: String.self) { viewString in
                                switch viewString
                                {
                                case "PROFILE":
                                    withAnimation()
                                    {
                                        ProfileView()
                                    }
                                case "BOTTLES":
                                    withAnimation()
                                    {
                                        BottlesView()
                                    }
                                case "SLEEP":
                                    withAnimation()
                                    {
                                        SleepView()
                                    }
                                case "POO":
                                    withAnimation()
                                    {
                                        PooView()
                                    }
                                case "WIND":
                                    withAnimation()
                                    {
                                        WindView()
                                    }
                                case "FOOD":
                                    withAnimation()
                                    {
                                        FoodView()
                                    }
                                case "MEDS":
                                    withAnimation()
                                    {
                                        MedsView()
                                    }
                                case "SETTINGS":
                                    withAnimation()
                                    {
                                        SettingsView()
                                    }
                                case "TEST":
                                    withAnimation()
                                    {
                                        TestView()
                                    }
                                default:
                                    //BottlesView()
                                    EmptyView()
                                }
                            }
                            .padding(.vertical, DeviceDimensions().height/30)
                            .foregroundColor(.white)
                            .accentColor(.white)
                            .buttonStyle(PlainButtonStyle())
                        }
                            
                        .scrollTargetBehavior(.paging)
                        .scrollIndicators(showIndicator ? .visible : .hidden)
                        .foregroundColor(.white)
                        .accentColor(.white)
                        .buttonStyle(PlainButtonStyle())
                }
                .frame(height: DeviceDimensions().height/1.9)
                //INSERT BOTTOM MINI VIEW (BABY AT A GLANCE VIEW)
                
                
                
                MiniTickerView()
                    .frame(width: DeviceDimensions().width/1.5, height: DeviceDimensions().height/15)
//                    .onTapGesture {
//                        isCardSettingsSheetPresented.toggle()
//                        print(String(isCardSettingsSheetPresented))
//                    }
//                    .sheet(isPresented: $isCardSettingsSheetPresented, content: {
//                        SettingsView()
//                    })
                
                
                Spacer()
            }
            .onAppear()
            {
                print("APPEAR")
                localHomeCards = HomeCards
                //UIApplication.shared.applicationIconBadgeNumber = 0
                UNUserNotificationCenter.current().setBadgeCount(0)
                //UNUserNotificationCenter.current().setBadgeCount(0)
            }
            .navigationTitle("Project Parent")
            .foregroundColor(.white)
            .accentColor(.white)
            .buttonStyle(PlainButtonStyle())
        }
        .foregroundColor(.white)
        .accentColor(.white)
        .buttonStyle(PlainButtonStyle())
        
        
    }
}


#Preview {
    HomeView()
}
