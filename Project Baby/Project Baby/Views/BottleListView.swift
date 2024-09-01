//
//  BottleListView.swift
//  Project Baby
//
//  Created by Jake thompson on 26/08/2024.
//

import SwiftUI

struct BottleListView: View {
    @FetchRequest(sortDescriptors: [NSSortDescriptor(keyPath: \Bottle.date, ascending: false)], animation: .default) private var bottles: FetchedResults<Bottle>
    @Environment(\.managedObjectContext) var viewContext
    @State private var destID = 0
    @State private var count = 0

    
    @State var todayCount = BottleController().bottlesTakenToday
    @State var yesterdayCount = BottleController().yesterdayCount
    private var olderDate = Calendar.current.date(byAdding: .day, value: -2, to: Date())

    
    var body: some View {
        NavigationView {
            VStack
            {
                List {
                    Section(header: Text("Today").font(/*@START_MENU_TOKEN@*/.title/*@END_MENU_TOKEN@*/)) {
                        DisclosureGroup("Bottles today: \(todayCount)")
                        {
                            ForEach(bottles) { bottle in
                                if (Calendar.current.isDateInToday(bottle.date!))
                                {
                                    NavigationLink(destination: SingleBottleView(bottle: bottle)
                                        .onDisappear() { self.destID = self.destID + 1 })
                                    {
                                        HStack(alignment: .center) {
                                            VStack(alignment: .leading) {
                                                Text("Given at: \(bottle.start_time?.formatted(date: .omitted, time: .shortened) ?? " ")")
                                                    .font(.title3)
                                                Text(formatThisDate(bottle.date ?? Date()))
                                                    .font(.subheadline)
                                                    .foregroundColor(.gray)
                                            }
                                            Spacer()
                                        }
                                    }
                                }
                            }
                            .onDelete(perform: delete)
                        }
                    }
                    .headerProminence(.increased)
                    Section(header: Text("Yesterday").font(/*@START_MENU_TOKEN@*/.title/*@END_MENU_TOKEN@*/)) {
                        DisclosureGroup("Bottles yesterday: \(yesterdayCount)")
                        {
                            ForEach(bottles) { bottle in
                                if (Calendar.current.isDateInYesterday(bottle.date!))
                                {
                                    NavigationLink(destination: SingleBottleView(bottle: bottle)
                                        .onDisappear() { self.destID = self.destID + 1 })
                                    {
                                        HStack(alignment: .center) {
                                            VStack(alignment: .leading) {
                                                Text("Given at: \(bottle.start_time?.formatted(date: .omitted, time: .shortened) ?? " ")")
                                                    .font(.title3)
                                                Text(formatThisDate(bottle.date ?? Date()))
                                                    .font(.subheadline)
                                                    .foregroundColor(.gray)
                                            }
                                            Spacer()
                                        }
                                    }
                                }
                            }
                            .onDelete(perform: delete)
                        }
                    }
                    .headerProminence(.increased)
                    Section(header: Text("Older").font(/*@START_MENU_TOKEN@*/.title/*@END_MENU_TOKEN@*/)) {
                        DisclosureGroup("Older bottles")
                        {
                            ForEach(bottles) { bottle in
                                if ((bottle.date?.formatted(.dateTime.dayOfYear()))! <= (olderDate?.formatted(.dateTime.dayOfYear()))!)                                {
                                    NavigationLink(destination: SingleBottleView(bottle: bottle)
                                        .onDisappear() { self.destID = self.destID + 1 })
                                    {
                                        HStack(alignment: .center) {
                                            VStack(alignment: .leading) {
                                                Text("Given on:  \(bottle.start_time?.formatted(date: .omitted, time: .shortened) ?? " ")")
                                                    .font(.title3)
                                                Text(formatThisDate(bottle.date ?? Date()))
                                                    .font(.subheadline)
                                                    .foregroundColor(.gray)
                                            }
                                            Spacer()
                                        }
                                    }
                                }
                            }
                            .onDelete(perform: delete)
                        }
                    }
                    .headerProminence(.increased)
                }
                .navigationTitle("Bottle History")
            }
            .preferredColorScheme(.dark)
            
        }
        .onChange(of: BottleController().bottlesTakenToday) { _ in
            print("Changed")
            todayCount = BottleController().bottlesTakenToday
        }
    }
    private func formatThisDate(_ date: Date) -> String {
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        formatter.timeStyle = .none
        return formatter.string(from: date)
    }
    
    private func delete(at offsets: IndexSet) {

        for index in offsets {
            let bottle = bottles[index]
                if (bottle.date?.formatted(.dateTime.dayOfYear()) == Date.now.formatted(.dateTime.dayOfYear()))
            {
                print("Removing 1 from todayCount")
                    BottleController().removeBottleData(durationToRemove: bottle.duration)
                    BottleController().removeBottleFromTodayCount()
            }
            else
            if (bottle.date?.formatted(.dateTime.dayOfYear()) == Calendar.current.date(byAdding: .day, value: -1, to: Date())?.formatted(.dateTime.dayOfYear()))
            {
                print("Removing 1 from yesterdayCount")
                BottleController().removeBottleData(durationToRemove: bottle.duration)
                BottleController().removeBottleFromYesterdayCount()

            }
            else
            {
                print("Removing 1 from older bottles and updating average")
                BottleController().removeBottleData(durationToRemove: bottle.duration)
            }
            self.viewContext.delete(bottle)
            do {
                try viewContext.save()
                print("Deleted Bottle")
            } catch {
                print("Error occured when saving bottle... \(error.localizedDescription)")
            }
        }
//        localTodayCount = CounterController().todayCount
//        localYesterdayCount = CounterController().yesterdayCount
    }
}

#Preview {
    BottleListView()
}
