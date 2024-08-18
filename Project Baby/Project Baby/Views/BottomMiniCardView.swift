//
//  BottomMiniCardView.swift
//  Project Baby
//
//  Created by Jake thompson on 18/08/2024.
//

import SwiftUI

struct BottomMiniCardView: View {
    var body: some View {
        RoundedRectangle(cornerRadius: 25)
            .frame(width: DeviceDimensions().width/1.5, height: DeviceDimensions().height/10)
    }
}

#Preview {
    BottomMiniCardView()
}
