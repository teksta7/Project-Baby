//
//  TimePickerView.swift
//  Project Parent
//
//  Created by Jake thompson on 06/09/2024.
//

import SwiftUI

struct TimePickerView: View {
    //@Binding var showBottleDeleteView: Bool
    var config: Config
    @Binding var minutes: Double
    @State private var isLoaded:Bool = false

    var body: some View {
        GeometryReader {
            let size = $0.size
            let horizontalPadding = size.width / 2
            
            VStack
            {
                HStack(alignment: .lastTextBaseline, spacing: 5, content: {
                    //let displayedMinutes = CGFloat(config.steps) * CGFloat(minutes)
                    //let displayedMinutes = CGFloat(minutes)
                    Text(verbatim: "\(String(format: "%.0f", minutes))")
                        .font(.largeTitle.bold())
                        .contentTransition(.numericText(value: minutes))
                        .animation(.snappy, value: minutes)
                    
                    Text("Minutes")
                        .font(.title2)
                        .fontWeight(.semibold)
                        .textScale(.secondary)
                        .foregroundStyle(.gray)
                })
   
                ScrollView(.horizontal)
                {
                    HStack(spacing: config.spacing) {
                        let totalSteps = config.steps * config.count
                        
                        ForEach(0...totalSteps, id: \.self) { index in
                            let remainder = index % config.steps
                            Divider()
                                .background(remainder == 0 ? Color.primary : .gray)
                                .frame(width: 0, height: remainder == 0 ? 20 : 10, alignment: .center)
                                .frame(maxHeight: 20, alignment: .bottom)
                                .overlay(alignment: .bottom)
                            {
                                if remainder == 0 && config.showsText
                                {
                                    Text("\((index / config.steps) * config.multiplier)")
                                        .font(.caption)
                                        .fontWeight(.semibold)
                                        .textScale(.secondary)
                                        .fixedSize()
                                        .offset(y: 20)
                                }
                            }
                        }
                    }
                    .frame(height: size.height)
                    .scrollTargetLayout()
                }
                .scrollIndicators(.hidden)
                .scrollTargetBehavior(.viewAligned)
                .scrollPosition(id: .init(get: {
                    let position: Int? = isLoaded ? (Int(minutes) * config.steps) / config.multiplier : nil
                    return position
                }, set: { newValue in
                    if let newValue { minutes = (CGFloat(newValue) / CGFloat(config.steps) * CGFloat(config.multiplier)) }
                }))
                .overlay(alignment: .center, content:
                            {
                    Rectangle()
                        .frame(width: 1, height: 40)
                        .padding(.bottom, 20)
                })
                .safeAreaPadding(.horizontal, horizontalPadding)
                .onAppear()
                {
                    if !isLoaded
                    {
                        isLoaded = true
                    }
                }
            }
        }
    }
    
    struct Config: Equatable {
        var count: Int
        var steps: Int = 10
        var spacing: CGFloat = 5
        var multiplier: Int = 10
        var showsText: Bool = true
        
    }
}

//#Preview {
//    TimePickerView()
//}
