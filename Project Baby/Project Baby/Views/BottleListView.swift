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
    
    var body: some View {
        NavigationView {
            VStack
            {
                //                Text("List of bottles to be displayed here")
                //                List {
                //                    Text("Example A")
                //                    Text("Example B")
                //                    Text("Example C")
                //                }
                List {
                    Section(header: Text("Today").font(/*@START_MENU_TOKEN@*/.title/*@END_MENU_TOKEN@*/)) {
                        DisclosureGroup("Bottles today: \(todayCount)")
                        {
                            ForEach(bottles) { bottle in
//                                if (Calendar.current.isDateInToday(bottle.date!))
//                                {
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
//
                                        }
                                    }
                               // }
                            }
                            //.onDelete(perform: delete)
                        }
                    }
                    .headerProminence(.increased)
                }
                .navigationTitle("Bottle History")
            }
            .preferredColorScheme(.dark)
            
        }
    }
    private func formatThisDate(_ date: Date) -> String {
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        formatter.timeStyle = .none
        return formatter.string(from: date)
    }
    
//    private func delete(at offsets: IndexSet) {
//        var yesterday = Calendar.current.date(byAdding: .day, value: -1, to: Date())
//        for index in offsets {
//            let kick = kicks[index]
//            if (kick.kickAgeValue == Date.now.formatted(.dateTime.dayOfYear()))
//            {
//                print("Removing 1 from todayCount")
//                CounterController().removeKickFromTodayTally()
//            }
//            if (kick.kickAgeValue == Calendar.current.date(byAdding: .day, value: -1, to: Date())?.formatted(.dateTime.dayOfYear()))
//            {
//                print("Removing 1 from yesterdayCount")
//                CounterController().removeKickFromYesterdayTally()
//            }
//            self.viewContext.delete(kick)
//            do {
//                try viewContext.save()
//                print("Deleted Kick")
//            } catch {
//                print("Error occured when saving kick... \(error.localizedDescription)")
//            }
//        }
//        localTodayCount = CounterController().todayCount
//        localYesterdayCount = CounterController().yesterdayCount
//    }
}

#Preview {
    BottleListView()
}
