//
//  InitialOnboardingScreen.swift
//  Project Parent
//
//  Created by Jake thompson on 03/10/2024.
//

import SwiftUI
import Foundation

struct InitialOnboardingView: View {
    @AppStorage("projectparent.showWelcomeOnboarding") var showWelcomeOnboarding: Bool = true
    @AppStorage("projectparent.isBabyProfileOnboardingComplete") var isBabyProfileOnboardingComplete: Bool = false
    @AppStorage("projectparent.isCardSelectionOnboardingComplete") var isCardSelectionOnboardingComplete: Bool = false
    @State var babyCardColor: Color = .yellow
    @State var cardsCardColor: Color = .yellow
    @State var finishColor: Color = .red
    @State var isBabyProfileSheetPresented: Bool = false
    @State var isCardSelectionSheetPresented: Bool = false
    @State var showSuccessAlert = false
    @State var showWarningAlert = false
    @State var showErrorAlert = false
    
    @EnvironmentObject
    private var IAP: IAPController

    var body: some View {
        if showWelcomeOnboarding == true {
            ZStack
            {
                Image(ImageResource.appLogo)
                    .resizable()
                    .offset(y: -200)
                    .frame(width: 200 + DeviceDimensions().width/5, height: 200 + DeviceDimensions().height/15)
                    //.frame(width: DeviceDimensions().width/1.75, height: DeviceDimensions().height/4)
                    //.frame(width: 200, height: 200)
                Text("Welcome to Project Parent").bold().font(.largeTitle)
                    .offset(y: -65)
                    .frame(width: 250)
                    .multilineTextAlignment(.center)
                Text("Lets get started by completing the sections below to setup the app...").font(.title2)
                    .offset(y: -15)
                    .frame(width: 250)
                    .multilineTextAlignment(.center)
                //Text("Complete the sections below to setup the app").font(.title3)
                    .offset(y: 60)
                    
                    
                HStack
                {
                    ZStack{
                        RoundedRectangle(cornerRadius: 10)
                            .fill(babyCardColor)
                            .frame(width: 100, height: 100)
                            .padding()
                            .offset(y: 175)
                            .opacity(0.5)
                        Text("Baby").foregroundColor(.white)
                            .offset(y: 175)
                    }
                    .onTapGesture {
                        withAnimation {
                            print("triggering Baby Profile Setup View")
                            isBabyProfileSheetPresented.toggle()
                        }
                    }
                    .popover(isPresented: $isBabyProfileSheetPresented, content: {
                        BabyProfileOnboardingView(isBabyProfileSheetPresented: $isBabyProfileSheetPresented, imageName: "figure.child",
                                                  title: "Create a Baby Profile",
                                                  description: "Just a few details about your baby...",
                                                  showDoneButton: false)
                            .presentationCompactAdaptation(.sheet)
                    })
                    .sensoryFeedback(.increase, trigger: isBabyProfileSheetPresented)
                    ZStack{
                        RoundedRectangle(cornerRadius: 10)
                            .fill(cardsCardColor)
                            .frame(width: 100, height: 100)
                            .padding()
                            .offset(y: 175)
                            .opacity(0.5)
                        Text("Cards").foregroundColor(.white)
                            .offset(y: 175)
                    }
                    .onTapGesture {
                        withAnimation {
                            print("triggering Card Selection Setup View")
                            isCardSelectionSheetPresented.toggle()
                        }
                    }
                    .popover(isPresented: $isCardSelectionSheetPresented, content: {
                        CardSelectionOnboardingView( isCardSettingsSheetPresented: $isCardSelectionSheetPresented)
                            .presentationCompactAdaptation(.sheet)
                    })
                    .sensoryFeedback(.increase, trigger: isCardSelectionSheetPresented)
                }
                .onChange(of: isBabyProfileSheetPresented)
                {
                    withAnimation
                    {
                        if isBabyProfileOnboardingComplete == true
                        {
                            babyCardColor = .green
                            if isCardSelectionOnboardingComplete == true && isBabyProfileOnboardingComplete == true
                            {
                                finishColor = .green
                            }
                            else
                            {
                                finishColor = .yellow
                            }
                        }
                        else
                        {
                            babyCardColor = .red
                        }
                    }
                }
                .onChange(of: isCardSelectionSheetPresented)
                {
                    withAnimation
                    {
                        isCardSelectionOnboardingComplete = true
                        if isCardSelectionOnboardingComplete == true
                        {
                            cardsCardColor = .green
                            if isCardSelectionOnboardingComplete == true && isBabyProfileOnboardingComplete == true
                            {
                                finishColor = .green
                            }
                            else
                            {
                                finishColor = .yellow
                            }
                        }
                        else
                        {
                            cardsCardColor = .red
                        }
                    }
                }
                ZStack
                {
                    RoundedRectangle(cornerRadius: 10)
                        .fill(finishColor)
                        .frame(width: 240, height: 60)
                        .padding()
                        .offset(y: 275)
                        .opacity(0.5)
                    Text("Finish").foregroundColor(.white)
                        .offset(y: 275)
                        .onTapGesture {
                            withAnimation {
                                print("Checking Onboarding")
                                if isCardSelectionOnboardingComplete == true && isBabyProfileOnboardingComplete == true
                                {
                                    print("Onboarding Complete")
                                    UserDefaults.standard.set(true, forKey: "projectparent.toggleInAppAds")
                                    //AdCoordinator().enableAds()
                                    showSuccessAlert = true
                                    DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
                                        showWelcomeOnboarding = false
                                    }
                                    
                                }
                                else
                                {
                                    showWarningAlert = true
                                }
                            }
                        }
                }
                CustomAlertView(show: $showSuccessAlert, icon: .success, text: "Success", gradientColor: .green, circleAColor: .green, details: "App is now ready to use", corner: 30)
                CustomAlertView(show: $showWarningAlert, icon: .warning, text: "Incomplete setup", gradientColor: .yellow, circleAColor: .yellow, details: "One or more sections are incomplete", corner: 30)
                CustomAlertView(show: $showErrorAlert, icon: .error, text: "Whoops", gradientColor: .red, circleAColor: .red, details: "Unknown error has occured", corner: 30)
            }
        }
        else {
            withAnimation
            {
                HomeView()
            }
        }
        //Welcome
        //Baby Profile
        //Stat cards to enable
        //Finish (Remember to mention that each stat card has settings to configure in the app settings
    }
}

#Preview {
    InitialOnboardingView(showWelcomeOnboarding: true)
}
