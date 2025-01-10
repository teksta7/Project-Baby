//
//  SleepView.swift
//  Project Parent
//
//  Created by Jake thompson on 16/08/2024.
//

import SwiftUI
import CoreData

struct ProfileView: View {
    @State var imagePadding = 1.0 //60.0 for small, 30 for medium
    @State var cardWidth: CGFloat = 1.25
    @State var outerCardHeight: CGFloat = 5
    @State var innerCardHeight: CGFloat = 15
    @State var bottomCardOpacity: CGFloat = 0.5
    @State var countableValueTextSize: CGFloat = 8.0
    @State var ageLabel = "0"
    @State var ageLabelWords = "Days old"
    @State var ageSubLabel = "Tap for weeks"
    @State var weightSubLabel = "Tap for kg"
    @State var weightLabel = "0"
    @State var weightLabelWords = "kg"
    @State var weightLabelSize = 8
    @Environment(\.managedObjectContext) private var viewContext


    @State var ageSelector = 0
    @State var weightSelector = 0

    
    var body: some View {
        ZStack
        {
            ScrollView
            {
                VStack
                {
                    profileImageView()
                        .padding(.bottom, imagePadding)
                    Spacer(minLength: 20)
                        .frame(width: DeviceDimensions().width/1.3, height: 45)
                        .multilineTextAlignment(.center)
                    //.opacity(bottleFeedTimer ? 1 : 0)
                    //.padding(.vertical, bottleFeedTimer ? 10 : 0)
                    Spacer(minLength: 60)
                    
                    Group
                    {
                        VStack
                        {
                            ZStack
                            {
                                RoundedRectangle(cornerRadius: 10)
                                    .fill(.green)
                                    .frame(width: DeviceDimensions().width/cardWidth, height: DeviceDimensions().height/outerCardHeight)
                                //.padding(.vertical, DeviceDimensions().height/10)
                                
                                Rectangle()
                                    .fill(.background)
                                    .frame(width: DeviceDimensions().width/cardWidth, height: DeviceDimensions().height/innerCardHeight)
                                    .offset(y: DeviceDimensions().height/innerCardHeight)
                                    .opacity(bottomCardOpacity)
                                VStack
                                {
                                    //change to a profile controller(to be created) that can interpret days, weeks months and years from 1 value
                                    //Text(String(BottleController().bottlesTakenToday)).bold().font(.system(size: DeviceDimensions().height/8))
                                    HStack
                                    {
                                        Text(ageLabel).bold().font(.system(size: DeviceDimensions().height/10))
                                            .offset(y: -DeviceDimensions().height/100)
                                        Text(ageLabelWords).font(.title3)
                                    }
                                    Spacer()
                                        .frame(height: 20)
                                    Text("Age:").font(.system(size: DeviceDimensions().width/25))
                                        .offset(y: -DeviceDimensions().height/200)
                                    Text(ageSubLabel).font(.caption)
                                        .offset(y: -DeviceDimensions().height/250)
                                }
                            }
                            .onTapGesture {
                                if ageSelector > 3
                                {
                                    ageSelector = 0
                                }
                                withAnimation(.spring)
                                {
                                    ageFormatSelector()
                                }
                            }
                            ZStack
                            {
                                RoundedRectangle(cornerRadius: 10)
                                    .fill(.green)
                                    .frame(width: DeviceDimensions().width/cardWidth, height: DeviceDimensions().height/outerCardHeight)
                                //.padding(.vertical, DeviceDimensions().height/10)
                                
                                Rectangle()
                                    .fill(.background)
                                    .frame(width: DeviceDimensions().width/cardWidth, height: DeviceDimensions().height/innerCardHeight)
                                    .offset(y: DeviceDimensions().height/innerCardHeight)
                                    .opacity(bottomCardOpacity)
                                VStack
                                {
                                    //change to a profile controller(to be created) that can interpret kg and lbs/oz from 2 values which are lbs & oz
                                    HStack
                                    {
                                        Text(weightLabel).bold().font(.system(size: DeviceDimensions().height/16))
                                        //Text(weightLabel).bold().font(.system(size: weightLabelSize))
                                            .offset(y: -DeviceDimensions().height/100)
                                        Text(weightLabelWords).font(.title3)
                                    }
                                    .frame(height: DeviceDimensions.screen.height/7)
                                    
                                    Text("Weight:").font(.system(size: DeviceDimensions().width/25))
                                        .offset(y: -DeviceDimensions().height/100)
                                    Text(weightSubLabel).font(.caption)
                                        .offset(y: -DeviceDimensions().height/100)
                                }
                            }
                            .onTapGesture {
                                if weightSelector > 3
                                {
                                    weightSelector = 0
                                }
                                withAnimation(.spring)
                                {
                                    weightFormatSelector()
                                }
                            }
                        }
                    }
                }
            }
        }
        .onAppear()
        {
            weightFormatSelector()
            ageFormatSelector()
        }
    }
    func ageFormatSelector()
    {
        if ageSelector > 3
        {
            ageSelector = 0
        }
        //var textToReturn: Text
        switch ageSelector
        {
        case 0:
            //ageLabel = " Days old"
            ageLabel = String(ProfileController().getAgeInDays())
            ageSubLabel = "Tap for weeks"
            ageLabelWords = " Days old"
        case 1:
            ageLabelWords = " Weeks Old"
            ageSubLabel = "Tap for months"
            ageLabel = String(ProfileController().getAgeInWeeks())

        case 2:
            ageLabelWords = " Months Old"
            ageSubLabel = "Tap for years"
            ageLabel = String(ProfileController().getAgeInMonths())

        case 3:
            ageLabelWords = " Years Old"
            ageSubLabel = "Tap for days"
            ageLabel = String(ProfileController().getAgeInYears())

        default:
            ageLabel = " Unknown"
        }
        ageSelector += 1
        //return textToReturn
    }
    func weightFormatSelector()
    {
        if weightSelector > 1
        {
            weightSelector = 0
        }
        //var textToReturn: Text
        switch weightSelector
        {
        case 0:
            //ageLabel = " Days old"
            weightLabel = String(format: "%.2f" ,(ProfileController().giveCurrentWeightInKg()))
            weightSubLabel = "Tap for lbs/oz"
            weightLabelWords = " kg"
        case 1:
            weightLabelWords = " lbs"
            weightSubLabel = "Tap for kg"
            let tempWeight = fetchLatestWeight()
        
            ProfileController().setCurrentWeight(kg: tempWeight!.kg)

            let weightInLbsOz = ProfileController().giveCurrentWeightInLbsOz()
            let weightOzFormattedLabel = String(format: "%.0f" , weightInLbsOz.1)
            if weightInLbsOz.1 > 15.8
            {
                let correctedWeightlbs = weightInLbsOz.0 + 1
                let correctedWeightOz = 0
                
                weightLabel = "\(correctedWeightlbs) lbs \(correctedWeightOz) oz"
                print("Weight: \(weightInLbsOz.0) lbs \(weightOzFormattedLabel) oz")
            }
            else if weightInLbsOz.1 < 1
            {
                let correctedWeightlbs = weightInLbsOz.0 - 1
                let correctedWeightOz = 0
                
                weightLabel = "\(correctedWeightlbs) lbs \(correctedWeightOz) oz"
                print("Weight: \(weightInLbsOz.0) lbs \(weightOzFormattedLabel) oz")
            }
            else
            {
                weightLabel = "\(weightInLbsOz.0) lbs \(weightOzFormattedLabel) oz"
                print("Weight: \(weightInLbsOz.0) lbs \(weightOzFormattedLabel) oz")
            }

        default:
            weightLabel = " Unknown"
        }
        weightSelector += 1
        //return textToReturn
    }
    func fetchLatestWeight() -> Weight? {

        let fetchRequest = NSFetchRequest<Weight>(entityName: "Weight")
        fetchRequest.sortDescriptors = [NSSortDescriptor(key: "kg", ascending: false)]
        fetchRequest.fetchLimit = 1

        do {
            let lastWeight = try viewContext.fetch(fetchRequest).first
            print("WEIGHT")
            print(lastWeight?.kg ?? 0.0)
           // setCurrentWeight(kg: lastWeight!.kg)
            return lastWeight
        } catch let error as NSError {
            print("Could not fetch. \(error), \(error.userInfo)")
            return nil
        }
    }
}



@ViewBuilder
func profileImageView() -> some View
{
    @State var profileImage = ImageFileController().loadBabyProfilePic()

    GeometryReader{ geo in
        let minY = geo.frame(in: .global).minY
        let minX = geo.frame(in: .global).minX
        let isScrolling = minY > 0
        VStack
        {
            //Image (uiImage: ImageFileController().loadBabyProfilePic()).resizable().scaledToFill()
            Image (uiImage: profileImage).resizable().scaledToFill()
            //Image(.test).resizable().scaledToFill()
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
                    //Image(uiImage: ImageFileController().loadBabyProfilePic()).resizable().scaledToFill()
                    Image (uiImage: profileImage).resizable().scaledToFill()

                    //Image(.test).resizable().scaledToFill()
                    Circle().stroke(lineWidth: 20)
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
                    Text("\(UserDefaults.standard.string(forKey: "projectparent.babyName") ?? "")").bold().font(.title)
                }
                .offset(y: isScrolling ? -minY : 0)
                
            }
            .padding(.vertical, DeviceDimensions().height/30)
            
        }
    }
    .frame(height: DeviceDimensions().height/5.5)
    
}

#Preview {
    ProfileView()
}
