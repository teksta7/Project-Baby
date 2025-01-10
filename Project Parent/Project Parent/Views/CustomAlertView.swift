//
//  CustomAlertView.swift
//  Project Parent
//
//  Created by Jake thompson on 30/08/2024.
//

import SwiftUI

struct CustomAlertView: View {
    @Binding var show: Bool
    @State var animateCircle = false
    var icon:UIImage = .success
    var text = "Success"
    var gradientColor: Color = .red
    var circleAColor: Color = .green
    var details: String = "Message"
    var corner: CGFloat = 30
    var body: some View {
        VStack
        {
            Spacer()
            ZStack
            {
                RoundedRectangle(cornerRadius: corner)
                    .frame(height: 300)
                    .foregroundStyle(LinearGradient(gradient: Gradient(colors: [.clear, .clear, gradientColor]), startPoint: .top, endPoint: .bottom))
                    .opacity(0.5)
                    //.offset(y: show ? 0: DeviceDimensions().height/2.5)
                    .offset(y: show ? 0: 300)

                ZStack
                {
                    RoundedRectangle(cornerRadius: corner)
                        .frame(height: 280 )
                        .shadow(color: /*@START_MENU_TOKEN@*/.black/*@END_MENU_TOKEN@*/.opacity(0.01), radius: 20, x: 0.0, y: 0.0 )
                        .shadow(color: /*@START_MENU_TOKEN@*/.black/*@END_MENU_TOKEN@*/.opacity(0.1), radius: 30, x: 0.0, y: 0.0 )
//                        .foregroundStyle(.white)
//                        .opacity(0.3)
                    VStack(spacing: 15)
                    {
                        ZStack {
                            Circle().stroke(lineWidth: 2).foregroundStyle(circleAColor).frame(width: 105, height: 105)
                                .scaleEffect(animateCircle ? 1.3 : 0.90)
                                .opacity(animateCircle ? 0 : 1)
                                .animation(.easeInOut(duration: 2).delay(1).repeatForever(autoreverses: false), value: animateCircle)
                            Circle().stroke(lineWidth: 2).foregroundStyle(circleAColor).frame(width: 105, height: 105)
                                .scaleEffect(animateCircle ? 1.3 : 0.90)
                                .opacity(animateCircle ? 0 : 1)
                                .animation(.easeInOut(duration: 2).delay(1.5).repeatForever(autoreverses: false), value: animateCircle)
                                .onAppear()
                            {
                                animateCircle.toggle()
                            }
                            Image(uiImage: icon)
                        }
                        Text(text).bold().font(.system(size: 30))
                            .foregroundStyle(.black)
                        Text(details)
                            .foregroundStyle(.black)
                            .opacity(0.5)
                            .padding(.horizontal)
                        Button("Dismiss") {
                            withAnimation {
                                show = false
                            }
                        }
                    }
                }
                .padding(.horizontal, 10)
                //.offset(y: show ? -30: 300)
                .offset(y: show ? -30: DeviceDimensions().height/2)
            }
            .onChange(of: show) { oldValue, newValue in
                if newValue {
                    DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
                        withAnimation
                        {
                            show = false
                        }
                    }
                }
                
            }
        }
        .ignoresSafeArea()
    }
}

#Preview {
    CustomAlertView(show: .constant(true))
}
