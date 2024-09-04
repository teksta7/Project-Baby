//
//  BottomMiniCardView.swift
//  Project Baby
//
//  Created by Jake thompson on 18/08/2024.
//

import SwiftUI

struct BottomMiniCardView: View {
    var body: some View {
        ZStack
        {
            RoundedRectangle(cornerRadius: 25)
                .frame(width: DeviceDimensions().width/1.5, height: DeviceDimensions().height/9)
            HStack
            {
                Image(systemName: "gearshape")
                    .foregroundStyle(.black)
    
                Text("Settings")
                    .font(.title2)
                    .foregroundStyle(.black)
            }
        }
    }
}

#Preview {
    BottomMiniCardView()
}
