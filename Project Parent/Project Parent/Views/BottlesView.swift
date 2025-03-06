//
//  BottlesView.swift
//  Project Parent
//
//  Created by Jake thompson on 16/08/2024.
//

import SwiftUI
import StoreKit
import ActivityKit

//private struct AdControllerViewRepresentable: UIViewControllerRepresentable
//{
//    let viewController = UIViewController()
//    
//    func makeUIViewController(context: Context) -> some UIViewController {
//        return viewController
//    }
//    
//    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {
//        
//    }
//}

struct BottleFeedTrackerAttributes: ActivityAttributes {
    struct ContentState: Codable, Hashable {
        // Dynamic stateful properties about your activity go here!
        var bottleDuration: Int
    }
    
    // Fixed non-changing properties about your activity go here!
    var babyName: String
    var estimatedEndTimeStamp: Date
    var startTimeStamp: Date
    var dateRange: ClosedRange<Date>
}


struct BottlesView: View {
    private var adCoordinator = AdCoordinator()
    
    var center = UNUserNotificationCenter.current()
    
    @State var activity: Activity<BottleFeedTrackerAttributes>?

    @Environment(\.managedObjectContext) private var viewContext
    @Environment(\.requestReview) private var requestReview
    @State var bottlesBeforeReview = 10
    
    @State var bottleFeedTimer: Bool = false
    @State var bottleFeedButtonLabel: String = "Start Bottle Feed"
    @State var bottleFeedButtonColor: Color = .green
    @State var isOuncesSheetPresented = false
    @State var isBottleListSheetPresented = false
    @State var imagePadding = 1.0 //60.0 for small, 30 for medium
    @State var notesCardColor: Color = .blue
    @State var bottleCardColor: Color = .blue
    @State var ouncesCardColor: Color = .orange
    @State var notesTextColor: Color = .white
    
    @ObservedObject var bottleSettings = BottleSettings()
    
    @State var cardWidth: CGFloat = 2.5
    @State var outerCardHeight: CGFloat = 5
    @State var innerCardHeight: CGFloat = 15
    @State var bottomCardOpacity: CGFloat = 0.5
    @State var countableValueTextSize: CGFloat = 8.0
    @State var cardColorChange: Bool = false
    @State var showSuccessAlert = false
    @State var showWarningAlert = false
    @State var showErrorAlert = false
    
    
    @State var notesToSave: String = UserDefaults.standard.string(forKey: "DefaultBottleNote") ?? ""
    @State var startTimeToSave: Date = Date.now
    @State var endTimeToSave: Date = Date.now
    @State var bottleDuration = 0
    @State var ouncesToSave: CGFloat = UserDefaults.standard.double(forKey: "setDefaultOunces")
    
    @State var latestBottleID: UUID = UUID()
    
    
    @State var timer = Timer.publish(every: 1, on: .main, in: .common).autoconnect()
        
    @State private var adtimeRemaining = 60
    let adTimer = Timer.publish(every: 1, on: .main, in: .common).autoconnect()
    
    var body: some View {
        ZStack
        {
            ScrollView
            {
                VStack
                {
                    imageView()
                        .padding(.bottom, imagePadding)
                    Spacer(minLength: 20)
                        .frame(height: DeviceDimensions.screen.height/15)
                    Text(bottleFeedTimer ? "You can amend the ounces and notes before the bottle feed is done." : " ")
                        .frame(width: DeviceDimensions().width/1.3, height: bottleFeedTimer ? 45 : 0)
                        .multilineTextAlignment(.center)
                        .opacity(bottleFeedTimer ? 1 : 0)
                        //.padding(.vertical, bottleFeedTimer ? 10 : 0)
                    Spacer(minLength: 20)

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
                                    Text(String(BottleController().bottlesTakenToday)).bold().font(.system(size: DeviceDimensions().height/8))
                                        .offset(y: -DeviceDimensions().height/100)
                                    
                                    Text("Bottles taken today:").font(.system(size: DeviceDimensions().width/25))
                                        .offset(y: -DeviceDimensions().height/100)
                                    Text("Press for bottle history").font(.caption)
                                        .offset(y: -DeviceDimensions().height/100)
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
                                    Text(String(format: "%.2f", Double(ouncesToSave))).bold().font(.system(size: DeviceDimensions().height/15))
                                        .offset(y: -DeviceDimensions().height/250)
                                        .padding(.vertical, 1)
                                    
                                    Text("Ounces(oz):").font(.system(size: DeviceDimensions().width/25))
                                        .offset(y: DeviceDimensions().height/50)
                                    
                                    Text("Hold to set").font(.caption)
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
                                }
                            }
                            .onChange(of: BottleController().bottlesTakenToday)
                            {
                                if BottleController().bottlesTakenToday > 0
                                {
                                    bottleCardColor = .green
                                }
                                else
                                {
                                    bottleCardColor = .blue
                                }
                            }
                        }
                        //Spacer()
                            //.padding(.vertical, 1)
                        HStack
                        {
                            ZStack
                            {
                                RoundedRectangle(cornerRadius: 10)
                                    .fill(notesCardColor)
                                    .frame(width: DeviceDimensions().width/1.2, height: DeviceDimensions().height/12)
                                TextField(notesToSave ?? "Add notes for bottle...", text: $notesToSave, onEditingChanged: { (isBegin) in
                                    if isBegin {
                                        //print("Begins editing")
                                        withAnimation
                                        {
                                            notesCardColor = .yellow
                                            notesTextColor = .black
                                        }
                                    } else {
                                        notesTextColor = .white
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
                        
                        
                        Text("\(UtilFunctions().convertSecondsToMinutes(bottleDuration)) ").bold().font(/*@START_MENU_TOKEN@*/.title/*@END_MENU_TOKEN@*/)
                        //Text("\(bottleDuration) seconds").bold().font(/*@START_MENU_TOKEN@*/.title/*@END_MENU_TOKEN@*/)
                            
                        ZStack
                        {
                            RoundedRectangle(cornerRadius: 10)
                                .fill(bottleFeedButtonColor)
                                .frame(width: DeviceDimensions().width/2, height: DeviceDimensions().height/15)
                                .onTapGesture {
                                    withAnimation
                                    {
                                        if ouncesToSave == 0.0
                                        {
                                            showWarningAlert = true
                                        }
                                        else
                                        {
                                            Task {
                                                self.timer = Timer.publish(every: 1, on: .main, in: .common).autoconnect()
                                            }
                                           
                                            bottleFeedTimer.toggle()
                                            if bottleFeedTimer == true
                                            {
//                                                if (activity?.id.isEmpty == true) {
                                                if(bottleSettings.isBottleFeedLiveActivityOn == true)
                                                {
                                                    print("Launching Live Activity")
                                                    self.launchLiveActivity()
                                                }
                                                
                                                startTimeToSave = Date.now
                                                bottleFeedButtonLabel = "Finish Bottle Feed"
                                                bottleFeedButtonColor = .orange
                                            }
                                            else
                                            {
                                                endTimeToSave = Date.now
                                                Task {
                                                    await self.endLiveActivity()
                                                }
                                                
                                                //Code to add bottle
                                                if (addBottleToModel(addtionalNotes: notesToSave, startTime: startTimeToSave, endTime: endTimeToSave, ounces: ouncesToSave)) == true
                                                {
                                                    //Add popup alert for successful commit
                                                    
                                                    showSuccessAlert = true
                                                    //Recalculate average bottle duration
                                                    BottleController().calculateAverageBottleDuration()
                                                    BottleController().calculateAverageTimeBetweenBottles()
                                                        //These below can be moved into reset view function
                                                        bottleFeedButtonLabel = "Start Bottle Feed"
                                                        bottleFeedButtonColor = .green
                                                        notesCardColor = .blue
                                                        notesToSave = UserDefaults.standard.string(forKey: "DefaultBottleNote") ?? ""
                                                        bottleDuration = 0
                                                        bottlesBeforeReview -= 1
                                                    if bottlesBeforeReview == 0
                                                    {
                                                        requestReview()
                                                    }
                                                    }
                                                    else
                                                    {
                                                        showErrorAlert = true
                                                        bottleFeedButtonColor = .red
                                                        bottleFeedButtonLabel = "Error"
                                                    }
                                                    
                                                    
                                                    
                                                }
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
            .onChange(of: showSuccessAlert)
            {
                if showSuccessAlert == true 
                {
                    //Setting Notification if user has that enabled
                    if bottleSettings.enableBottleNotification
                    {
                        Task {
                            await BottleNotificationController().requestNotificationAccessByUser()
                            await
                            BottleNotificationController().scheduleBottleNotification(Int((UserDefaults.standard.double(forKey: "com.projectparent.localTimeBetweenFeeds"))), latestBottleID)
                        }
                    }
                }
            }
                .onAppear()
                {
                    //UIApplication.shared.applicationIconBadgeNumber = 0
                    center.setBadgeCount(0)
                    // Disable the idle timer when the view appears to prevent screen to sleep
                    UIApplication.shared.isIdleTimerDisabled = true
                    //AdCoordinator().enableAds()
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
                    
//                    if adCoordinator.adsEnabled == true
//                    {
//                        Task {
//                            await adCoordinator.loadAd()
//                        }
//                    }
                }
                .onDisappear()
                {
                    // Re-enable the idle timer when the view disappears to allow the screen to sleep
                    UIApplication.shared.isIdleTimerDisabled = false
                    Task {
                        await self.endLiveActivity()
                    }
                    
//                    if (adCoordinator.adsEnabled == true)
//                    {
//                        adCoordinator.showAd()
//                    }
                    //Code placeholder to run in background for counter
//                    DispatchQueue.global(qos: .background).async {
//                    // Your background code here
//                    // This code will run in the background
//                    DispatchQueue.main.async {
//                        // Code here will run on the main thread
//                    }
//                    }

                    
                }
                CustomAlertView(show: $showSuccessAlert, icon: .success, text: "Success", gradientColor: .green, circleAColor: .green, details: "Bottle recorded successfully", corner: 30)
                CustomAlertView(show: $showWarningAlert, icon: .warning, text: "Invalid ounces", gradientColor: .yellow, circleAColor: .yellow, details: "Hold down on the ounces card to set a value other than 0", corner: 30)
                CustomAlertView(show: $showErrorAlert, icon: .error, text: "Whoops", gradientColor: .red, circleAColor: .red, details: "Couldn't save the bottle, please try again", corner: 30)
            }
        .onReceive(timer) { _ in
            //print("Current Bottle Duration Timer: \(bottleDuration)")
            if ((bottleDuration >= 0) && (bottleFeedTimer == true)) {
                withAnimation{
                    bottleDuration += 1
//                    if (bottleDuration.isMultiple(of: 60))
//                    {
                    if(bottleSettings.isBottleFeedLiveActivityOn == true)
                    {
                        self.updateLiveActivity()
                    }
                    //}
                }
            }
        }
            
        }
        func addBottleToModel(addtionalNotes: String, startTime: Date, endTime: Date, ounces: CGFloat) -> Bool
        {
            print("Adding bottle to data model")
            let newBottle = Bottle(context: viewContext)
            newBottle.id = UUID()
            //latestBottleID = newBottle.id
            print(newBottle.id ?? "")
            newBottle.date = Date()
            print(newBottle.date ?? "")
            if addtionalNotes == ""
            {
                newBottle.addtional_notes = "None provided"
            }
            else
            {
                newBottle.addtional_notes = addtionalNotes
            }
            print(newBottle.addtional_notes ?? "")
            newBottle.duration = Double(Int(ConversionUtil().getDateDiff(start: startTime, end: endTime)))
            print(newBottle.duration)
            newBottle.start_time = startTime
            print(newBottle.start_time ?? "")
            newBottle.end_time = endTime
            print(newBottle.end_time ?? "")
            newBottle.ounces = ounces
            print(newBottle.ounces)
            
            do {
                try viewContext.save()
            } catch {
                return false
            }
            BottleController().addBottleData(durationToAdd: newBottle.duration)
            BottleController().addBottleToTodayCount()
            return true
        }
    }

private extension BottlesView {
    func launchLiveActivity() {
        if ActivityAuthorizationInfo().areActivitiesEnabled {
            print("Start live activity")
            print(Date.now)
            print(Date.now.addingTimeInterval(UserDefaults.standard.double(forKey: "projectparent.averagebottleduration")))
            activity = try? Activity.request(
                attributes: BottleFeedTrackerAttributes(babyName: UserDefaults.standard.string(forKey: "projectparent.babyName") ?? "Unknown", estimatedEndTimeStamp: Date(timeIntervalSinceNow: UserDefaults.standard.double(forKey: "projectparent.averagebottleduration")), startTimeStamp: startTimeToSave, dateRange: Date.now...Date.now.addingTimeInterval(UserDefaults.standard.double(forKey: "projectparent.averagebottleduration"))),
                content: .init(
                    state: BottleFeedTrackerAttributes.ContentState(bottleDuration: bottleDuration),
                    staleDate: nil, relevanceScore: 0
                )
            )
        }
    }
}

private extension BottlesView {
    func endLiveActivity() async {
        print("End live activity")
        await self.activity?.end(
           .init(
            state: .init(bottleDuration: 0),
               staleDate: nil
           ),
           dismissalPolicy: .after(.now + 4) // System will remove the notification after 4 seconds. You also have: .default (= 4 hours)/.immediate/.after(Date)
       )
    }
}

private extension BottlesView {
    func updateLiveActivity() {
        //guard let activity else { return }
        
        print("Update live activity")
        Task {
            await self.activity?.update(
                    ActivityContent<BottleFeedTrackerAttributes.ContentState>(
                        state: .init(bottleDuration: bottleDuration),
                        staleDate: nil,
                        relevanceScore: 0
                    ),
                    alertConfiguration: nil
                )
            }
                }
    }

    
    func resetViewForNewBottleFeed()
    {
        //Reset all values in view to 0 upon completion of bottle feed
    }

    func startTimer()
    {
        let timer = Timer.scheduledTimer(withTimeInterval: 1.0, repeats: true) { _ in
            DispatchQueue.main.async {
                    //bottleDuration += 1
                }
            }
            RunLoop.current.add(timer, forMode: .common)
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
                Image(.bottle).resizable()
                    .scaledToFill()
                    .frame(height: isScrolling ? 160 + minY/3 : 160 )
                    .opacity(0.75)
                    .clipped()
                    .offset(y: isScrolling ? -minY : 0)
                    .offset(x: isScrolling ? -minX : 0)
                    .blur(radius: isScrolling ? 0 + minY / 80 : 0)
                    .scaleEffect(isScrolling ? 1 + minY / 2000 : 1)
                    .overlay(alignment: .bottom)
                {
                    ZStack
                    {
                        Image(.bottle).resizable().scaledToFit().scaleEffect(0.9).imageScale(.small)
                        Circle().stroke(lineWidth: 10)
                        
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
        .frame(height: DeviceDimensions().height/5.5)
        
    }
