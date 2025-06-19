//
//  BottleChartsView.swift
//  Project Parent
//
//  Created by Jake thompson on 26/08/2024.
//

import SwiftUI
import Charts

struct BottleChartsView: View {
    @FetchRequest(sortDescriptors: [NSSortDescriptor(keyPath: \Bottle.date, ascending: false)], animation: .default) private var bottles: FetchedResults<Bottle>
    
    @State private var timeRange: TimeRange = .daily
    let symbolSize: CGFloat = 100
    let lineWidth: CGFloat = 3

    
    var body: some View {
       //Bar chart
        Text("Bottle Charts").font(.title).bold()
            .frame(alignment: .leading)
            .padding()
        TimeRangePicker(value: $timeRange)
            .padding(.bottom)

        switch timeRange {
        case .total:
            VStack
            {
                Text("Amount of feeds per day").font(.title2).frame( width: DeviceDimensions().width/1.25, alignment: .center)
                    .padding()
                ZStack
                {
                    Chart {
                        ForEach(bottles, id: \.date) { bottle in
                                LineMark(
                                    x: .value("Date", bottle.date ?? Date.now, unit: .day),
                                    y: .value("Count", UserDefaults.standard.integer(forKey:"projectparent.bottles.\( bottle.date?.formatted(.dateTime.dayOfYear()) ?? Date.now.formatted(.dateTime.dayOfYear()))"))
                                )
                                .symbol {
                                    Circle()
                                        .fill(Color.white.opacity(0.6))
                                        .frame(width: 8)
                                }
                                .interpolationMethod(.catmullRom)
                                .lineStyle(StrokeStyle(lineWidth: lineWidth))
                                .symbolSize(symbolSize)
                        }
                    }
                    .padding()

                    .chartXAxis {
                        AxisMarks(values: .stride(by: .day)) { _ in
                            AxisTick()
                            AxisGridLine()
                            AxisValueLabel(format: .dateTime.day().month().year(), centered: true)
                            //AxisValueLabel(format: .dateTime.day().month(), centered: true)
                        }
                    }
                    .chartLegend(Visibility.hidden)
                    //.chartYScale(range: .plotDimension(endPadding: 2))
                    .chartScrollableAxes(.horizontal)
                    .chartYVisibleDomain(length: 1)
                    
                    .chartXVisibleDomain(length: 250000)
                    .chartYScale(range: .plotDimension(endPadding: 10))
                    //.chartXSelection(value: $rawSelectedDate)
                    //Spacer(minLength: 10)
                }
            }
        case .hourly:
            VStack
            {
                Text("Breakdown of feed duration per hour").font(.title2).frame( width: DeviceDimensions().width/1.25, alignment: .center)
                    .padding()
                ZStack
                {
                    Chart {
                        ForEach(bottles, id: \.id) { bottle in
                            BarMark(x: .value("Hour", bottle.start_time ?? Date.now, unit: .hour), y: .value("Count", bottle.duration))
                        }
                    }
                    .aspectRatio(1, contentMode: .fit)
                    .padding()
                    .chartXAxis {
                        AxisMarks(values: .stride(by: .hour)) { _ in
                            AxisTick()
                            AxisGridLine()
                            AxisValueLabel(format: .dateTime.day().month().hour(), centered: true)
                        }
                    }
                    .chartYAxis {
                            AxisMarks() { value in
                                if let seconds = value.as(Int.self) {
                                    let minutes = seconds / 60
                                    AxisValueLabel {
                                        Text("\(minutes) min")
                                    }
                                }
                            }
                        }
                    .chartXVisibleDomain(length: 15000)
                    .chartYScale(range: .plotDimension(endPadding: 10))
                    .chartScrollableAxes(.horizontal)

                }
            }
        case .daily:
            VStack
            {
                Text("Total duration of feeds per day").font(.title2).frame( width: DeviceDimensions().width/1.25, alignment: .center)
                Text("Each feed represented by a different shade on the chart").font(.subheadline).frame( width: DeviceDimensions().width/1.5, alignment: .center)
                    //.padding()
                ZStack
                {
                    Chart {
                        ForEach(bottles, id: \.id) { bottle in
                            BarMark(x: .value("Date", bottle.date ?? Date.now, unit: .day), y: .value("Duration", bottle.duration))
                                .foregroundStyle(by: .value("Duration", bottle.duration))
                        }
                    }
                    
                    .chartLegend(.hidden)
//                    .chartLegend(position: .bottom) {
//                        ScrollView(.horizontal)
//                        {
//                            HStack
//                            {
//                                ForEach(bottles) { bottle in
//                                    let minutes = bottle.duration.rounded(.toNearestOrEven) / 60
//                                        //Text("\(minutes) min")
//                                    HStack
//                                    {
//                                        BasicChartSymbolShape.circle
//                                        //Circle()
//                                        //.foregroundColor(colorFor(symbol: symbol))
//                                        //.foregroundStyle(by: .value("Duration", bottle.duration))
//                                            .frame(width: 8, height: 8)
//                                        //Text(String(format: "%.2f", "\(minutes) min"))
//                                        Text("\(minutes) min")
//                                            .foregroundColor(.gray)
//                                            .font(.caption)
//                                    }
//                                }
//                            }
//                        }
//                        //.frame(height: 100)
//                    }
                    .aspectRatio(1, contentMode: .fit)
                    .padding()
                    .chartXAxis {
                        AxisMarks(values: .stride(by: .day)) { _ in
                            AxisTick()
                            AxisGridLine()
                            AxisValueLabel(format: .dateTime.day().month().year(), centered: true)
                        }
                    }
                    .chartYAxis {
                            AxisMarks() { value in
                                if let seconds = value.as(Int.self) {
                                    let minutes = seconds / 60
                                    AxisValueLabel {
                                        Text("\(minutes) min")
                                    }
                                }
                            }
                        }
                    .chartXVisibleDomain(length: 250000)
                    .chartYScale(range: .plotDimension(endPadding: 10))
                    .chartScrollableAxes(.horizontal)
                    
                }
            }
        }
        
    }
}

#Preview {
    BottleChartsView()
}
