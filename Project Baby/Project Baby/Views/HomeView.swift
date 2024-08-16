//
//  ContentView.swift
//  Project Baby
//
//  Created by Jake thompson on 15/08/2024.
//

import SwiftUI
import CoreData

struct HomeView: View {
    @State var showIndicator: Bool = false
    @State private var isRotationEnabled: Bool = true
    var body: some View
    {
        NavigationStack
        {
            VStack
            {
                RoundedRectangle(cornerRadius: 25)
                    .frame(width: DeviceDimensions().width/1.5, height: DeviceDimensions().height/10)
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
                                        }
                                        .zIndex(HomeCards.zIndex(homeCard))
                                }
                            }
                            .padding(.vertical, DeviceDimensions().height/30)
                            .onTapGesture {
                                print("Hello")
                            }
                        }
                        .scrollTargetBehavior(.paging)
                        .scrollIndicators(showIndicator ? .visible : .hidden)
                }
                .frame(height: DeviceDimensions().height/1.75)
                RoundedRectangle(cornerRadius: 25)
                    .frame(width: DeviceDimensions().width/1.5, height: DeviceDimensions().height/10)
            }
            .navigationTitle("Project Baby")
        }
    }
}


#Preview {
    HomeView()
}
