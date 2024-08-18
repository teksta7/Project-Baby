//
//  HomeCardView.swift
//  Project Baby
//
//  Created by Jake thompson on 15/08/2024.
//

import SwiftUI

@ViewBuilder
func HomeCardView(_ homeCard: HomeCard) -> some View {
    @State var isSheetPresented = false
    ZStack {
        NavigationLink(value: homeCard.viewString)
        {
            ZStack{
                RoundedRectangle(cornerRadius: 15.0)
                    .fill(homeCard.color.gradient)
                Circle()
                    .frame(width: DeviceDimensions().width/2)
                    .foregroundStyle(.black)
                    .offset(y: -DeviceDimensions().height/5)
//                Circle()
//                    .frame(width: DeviceDimensions().width/2)
//                    .foregroundStyle(.black)
//                    .offset(y: DeviceDimensions().height/3)
//                    .offset(x: DeviceDimensions().height/9)
//                Circle()
//                    .frame(width: DeviceDimensions().width/2)
//                    .foregroundStyle(.black)
//                    .offset(y: DeviceDimensions().height/3)
//                    .offset(x: -DeviceDimensions().height/9)
                
            }
            
        }
       
       
//        NavigationLink("More info", value: homeCard.viewString)
//            .foregroundStyle(.white)

//        Text(homeCard.viewString)
//            .foregroundStyle(.black)
        //Circle()
            //.fill(.black)
    }
//    .onTapGesture {
//        print("changed")
//        isSheetPresented = true
//    }
//    .sheet(isPresented: $isSheetPresented, content: {
//        BottlesView()
//    })
    
}


func minX( _ proxy: GeometryProxy) -> CGFloat {
    let minX = proxy.frame(in: .scrollView(axis: .horizontal)).minX
    return minX < 0 ? 0 : -minX
}

func progress(_ proxy: GeometryProxy, limit: CGFloat = 2) -> CGFloat {
    let maxX = proxy.frame(in: .scrollView(axis: .horizontal)).maxX
    let width = proxy.bounds(of: .scrollView(axis: .horizontal))?.width ?? 0
    
    let progress = (maxX / width) - 1.0
    let cappedProgress = min(progress, limit)
    print(cappedProgress)
    //HomeView().activeCard = cappedProgress
    
    return cappedProgress
}

func scale(_ proxy: GeometryProxy, scale: CGFloat = 0.1) -> CGFloat {
    let progress = progress(proxy, limit: 5)
    
    return 1 - (progress * scale)
}

func excessMinX(_ proxy: GeometryProxy, offset: CGFloat = 10) -> CGFloat {
    let progress = progress(proxy)
    
    return progress * offset
}

func rotation(_ proxy: GeometryProxy, rotation: CGFloat = 5) -> Angle {
    let progress = progress(proxy)
    
    return .init(degrees: progress * rotation)
}

#Preview {
    HomeCardView(HomeCard(id: UUID(), color: .green, viewString: "ABC"))
}
