//
//  BabyProfileOnboardingView.swift
//  Project Parent
//
//  Created by Jake thompson on 07/10/2024.
//

import SwiftUI
import PhotosUI

struct BabyProfileOnboardingView: View {
    @Environment(\.managedObjectContext) private var viewContext
    @Binding var isBabyProfileSheetPresented: Bool
    let imageName: String
    let title: String
    let description: String
    let showDoneButton: Bool
    //var nextAction: () -> Void
    @State var animation: Bool = false
    @State private var babyName = ""
    @State private var gender = "Boy"
    @State private var birthDate =  Calendar.current.date(bySettingHour: 1, minute: 0, second: 0, of: Date.now)!
    @State private var pickerItem: PhotosPickerItem?
    @State private var babyProfilePic: Image? = Image(systemName: "questionmark")
    @State private var pictureString = "Select a profile picture"
    @State private var selectedImageData: Data? = nil
    @State var showSuccessAlert = false
    @State var showWarningAlert = false
    @State var showErrorAlert = false
    
    @State private var weightValuePounds: Int = 0
    @State private var weightValueOunces: Int = 0
    //@State private var appColor = UserDefaults().getAppTheme()

    var body: some View {
        ZStack
        {
            VStack(spacing: 20) {
                Image(systemName: imageName)
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .frame(height: 150)
                    .foregroundColor(.yellow)
                    .symbolEffect(.pulse, isActive: animation)
                    .onAppear {
                        var transaction = Transaction()
                        transaction.disablesAnimations = true
                        withTransaction(transaction) {
                            animation = true
                        }
                    }
                
                Text(title)
                    .font(.title)
                    .fontWeight(.bold)
                
//                Text(description)
//                    .font(.body)
//                    .multilineTextAlignment(.center)
//                    .padding(.horizontal, 10)
//                    .foregroundColor(.gray)
                
                TextField("What is your baby's name?", text: $babyName)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, 40)
                    .foregroundColor(.gray)
                
                Text("What was their birth weight?")
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, 40)
                    .foregroundColor(.gray)
                HStack
                {
                    
                 
                Stepper("\(Int(weightValuePounds)) lbs",
                        value: $weightValuePounds,
                                    in: 0...20,
                                    step: 1)
                .padding(.horizontal, 10)
                Stepper("\(Int(weightValueOunces)) oz",
                            value: $weightValueOunces,
                                        in: 0...15,
                                        step: 1)
                .padding(.horizontal, 10)

                }
               
                
                HStack
                {
                    Text("Gender")
                        .font(.body)
                        .padding(.horizontal, 40)
                        .foregroundColor(.gray)
                    
                    Picker(selection: $gender, label: Text("Picker")) {
                        Text("Boy").tag("Boy")
                        Text("Girl").tag("Girl")
                    }
                    .tint(.white)
                    
                }
                
                // the interval represents 273 days in the future to allow for picking a date in a full pregnancy window
                
                DatePicker(selection: $birthDate)
                {
                    Text("When were they born?")
                        .foregroundColor(.gray)
                    
                }
                .multilineTextAlignment(.center)
                .padding(.horizontal, 10)
                .tint(.white)
                
                HStack
                {
                    PhotosPicker(pictureString, selection: $pickerItem, matching: .images)
                        .padding(.horizontal, 10)
                        .tint(.blue)
                    if let selectedImageData,
                       let uiImage = UIImage(data: selectedImageData) {
                        Image(uiImage: uiImage)
                            .resizable()
                            .aspectRatio(contentMode: .fit)
                            .frame(width: 50, height: 50)
                            .clipShape(Circle())
                            .overlay(Circle().stroke(Color.white, lineWidth: 4))
                            .shadow(radius: 7)
                            .padding(.horizontal, 40)
                    }
                }
                .onChange(of: pickerItem) {
                    Task {
                        babyProfilePic = try await pickerItem?.loadTransferable(type: Image.self)
                        if let data = try? await pickerItem?.loadTransferable(type: Data.self) {
                            selectedImageData = data
                        }
                    }
                }
                Button("Complete baby profile") {
                    if (setupBabyProfile() == true)
                    {
                        withAnimation {
                            showSuccessAlert = true
                            DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
                                withAnimation
                                {
                                    print("Baby profile onboarding complete")
                                    UserDefaults.standard.set(true, forKey: "projectparent.isBabyProfileOnboardingComplete")
                                    isBabyProfileSheetPresented = false
                                }
                            }
                        }
                    }
                }
                //.frame(width: 350, height: 100)
                .buttonStyle(.bordered)
                .foregroundStyle(.white)
                .padding()
                
            }
            CustomAlertView(show: $showSuccessAlert, icon: .success, text: "Success", gradientColor: .green, circleAColor: .green, details: "Baby profile created sucessfully", corner: 30)
            CustomAlertView(show: $showWarningAlert, icon: .warning, text: "Invalid ounces", gradientColor: .yellow, circleAColor: .yellow, details: "Hold down on the ounces card to set a value other than 0", corner: 30)
            CustomAlertView(show: $showErrorAlert, icon: .error, text: "Whoops", gradientColor: .red, circleAColor: .red, details: "Couldn't save the bottle, please try again", corner: 30)
        }
       
        .padding()
    }
    func setupBabyProfile() -> Bool {
        
        if selectedImageData != nil && babyName != "" 
        {
            ImageFileController().saveBabyProfilePic(imageData: selectedImageData!)
            print("Saving name...")
            UserDefaults.standard.set(babyName, forKey: "projectparent.babyName")
            print("Saving gender...")
            UserDefaults.standard.set(gender, forKey: "projectparent.babyGender")
            print("Saving due date...\(birthDate)")
            
            UserDefaults.standard.set(birthDate.timeIntervalSince1970, forKey: "projectparent.babyBirthDateTime")
            print("Saving image...")
            print("Saving weight...")
            let weightInKg = ProfileController().lbsOzToKg(pounds: Double(weightValuePounds), ounces: Double(weightValueOunces))
            if addWeightToModel(kg: weightInKg)
            {
                return true
            }
            else
            {
                return false
            }
        }
        else
        {
            if selectedImageData == nil
            {
                pictureString = "You have not selected a profile picture"
            }
            if babyName == ""
            {
                pictureString = "You have not entered a name"
            }
            if selectedImageData == nil && babyName == ""
            {
                pictureString = "You have not selected a profile picture and not entered a name"
            }
            return false
        }
    }
    func addWeightToModel(kg: Double) -> Bool
    {
        print("Adding weight to data model")
        let newWeight = Weight(context: viewContext)
        newWeight.date = Date()
        newWeight.kg = kg
        ProfileController().currentWeight = kg
        
        do {
            try viewContext.save()
        } catch {
            return false
        }
        return true
    }
}



#Preview {
    BabyProfileOnboardingView(isBabyProfileSheetPresented: .constant(true), imageName: "figure.child",
                              title: "Create a Baby Profile",
                              description: "Just a few details about your baby...",
                              showDoneButton: false)
}
