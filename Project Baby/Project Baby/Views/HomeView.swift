//
//  HomeView.swift
//  Project Baby
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
    var body: some View
    {
        NavigationStack
        {
            VStack
            {
                //INSERT TOP MINI VIEW
                TopMiniCardView()
                    .onTapGesture {
                        isChartSheetPresented.toggle()
                        print(String(isChartSheetPresented))
                    }
                    .sheet(isPresented: $isChartSheetPresented, content: {
                        ChartsView()
                    })
                GeometryReader
                {
                    let size = $0.size
                        ScrollView(.horizontal)
                        {
                            HStack(spacing: 0)
                            {
                                ForEach(HomeCards) { homeCard in
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
                        
                            .navigationDestination(for: String.self) { viewString in
                                switch viewString
                                {
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
                                default:
                                    //BottlesView()
                                    EmptyView()
                                }
                            }
                            .padding(.vertical, DeviceDimensions().height/30)
                        }
                        .scrollTargetBehavior(.paging)
                        .scrollIndicators(showIndicator ? .visible : .hidden)
                }
                .frame(height: DeviceDimensions().height/1.75)
                //INSERT BOTTOM MINI VIEW
                BottomMiniCardView()
                    .onTapGesture {
                        isCardSettingsSheetPresented.toggle()
                        print(String(isCardSettingsSheetPresented))
                    }
                    .sheet(isPresented: $isCardSettingsSheetPresented, content: {
                        CardSettingsView()
                    })
            }
            .navigationTitle("Project Baby")
        }
    }
}


#Preview {
    HomeView()
}
