//
//  BottleChartsView.swift
//  Project Baby
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
                Text("Amount of feeds per day").font(.title2).frame( width: DeviceDimensions().width/1.25, alignment: .leading)
                    .padding()
                ZStack
                {
                    Chart {
                        ForEach(bottles, id: \.date) { bottle in
                                LineMark(
                                    x: .value("Date", bottle.date ?? Date.now, unit: .day),
                                    y: .value("Count", UserDefaults.standard.integer(forKey:"projectbaby.bottles.\( bottle.date?.formatted(.dateTime.dayOfYear()) ?? Date.now.formatted(.dateTime.dayOfYear()))"))
                                )
                                .symbol {
                                    Circle()
                                        .fill(Color.black.opacity(0.6))
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
                        }
                    }
                    .chartLegend(Visibility.hidden)
                    .chartYScale(range: .plotDimension(endPadding: 2))
                    .chartScrollableAxes(.horizontal)
                    .chartYVisibleDomain(length: 1)
                    //.chartXSelection(value: $rawSelectedDate)
                    Spacer(minLength: 10)
                }
            }
//        case .ounces:
//            VStack
//            {
//                Text("Amount of times specific ounce measurement has been given").font(.title2).frame( width: DeviceDimensions().width/1.25, alignment: .leading)
//                    .padding()
//                ZStack
//                {
//                    Chart {
//                        ForEach(bottles, id: \.id) { bottle in
//                            BarMark(x: .value("Amount of ounces", bottle.ounces ?? 0.0), y: .value("Count", UserDefaults.standard.integer(forKey: "projectbaby.bottles.\(bottle.date?.formatted(.dateTime.dayOfYear()) ?? Date.now.formatted(.dateTime.dayOfYear()))")))
//                        }
//                    }
//                    .aspectRatio(1, contentMode: .fit)
//                    .padding()
//                    .chartLegend(.visible)
//                    .chartXAxis {
//                        AxisMarks(position: .bottom, values: .automatic) { value in
//                            AxisGridLine(centered: true, stroke: StrokeStyle(dash: [1, 2]))
//                            AxisTick(centered: true, stroke: StrokeStyle(dash: [1, 2]))
//                            AxisValueLabel() {
//                                if let doubleValue = value.as(Double.self) {
//                                    Text("\(String(format: "%.0f", doubleValue)) oz")
//                                        .font(.system(size: 10))
//                                }
//                            }
//                        }
//                    }
//                    //.chartScrollableAxes(.horizontal)
//                }
//            }
        case .hourly:
            VStack
            {
                Text("Breakdown of feed duration(seconds) per hour").font(.title2).frame( width: DeviceDimensions().width/1.25, alignment: .leading)
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
                    .chartXVisibleDomain(length: 15000)
                    .chartYScale(range: .plotDimension(endPadding: 10))
                    .chartScrollableAxes(.horizontal)

                }
            }
        case .daily:
            VStack
            {
                Text("Breakdown of feed duration(seconds) per day").font(.title2).frame( width: DeviceDimensions().width/1.25, alignment: .leading)
                    .padding()
                ZStack
                {
                    Chart {
                        ForEach(bottles, id: \.id) { bottle in
                            BarMark(x: .value("Date", bottle.date ?? Date.now, unit: .day), y: .value("Duration", bottle.duration))
                                .foregroundStyle(by: .value("Duration", bottle.duration ?? 0.0))
                        }
                    }
                    
                    .chartLegend(.visible)
                    .aspectRatio(1, contentMode: .fit)
                    .padding()
                    .chartXAxis {
                        AxisMarks(values: .stride(by: .day)) { _ in
                            AxisTick()
                            AxisGridLine()
                            AxisValueLabel(format: .dateTime.day().month().year(), centered: true)
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
