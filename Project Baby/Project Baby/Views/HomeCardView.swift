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
                VStack
                {
                    Image(systemName: homeCard.imageToDisplay)
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(width: DeviceDimensions().width/2, height: DeviceDimensions().height/4)
                        .foregroundStyle(.white)
                    Text(homeCard.presentedString).font(.system(size: 50))
                        .foregroundStyle(.white)
                }
                //ATTEMPT DIAGONAL TEXT
                
//                RoundedRectangle(cornerRadius: 10)
//                    .fill(.black)
//                    .frame(width: /*@START_MENU_TOKEN@*/100/*@END_MENU_TOKEN@*/, height: 100)
                
            }
            
        }
    }
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
    HomeCardView(HomeCard(id: UUID(), color: .green, viewString: "ABC", presentedString: "ABC", imageToDisplay: "waterbottle"))
}