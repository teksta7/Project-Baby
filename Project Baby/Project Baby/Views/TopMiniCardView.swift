//
//  TopMiniCardView.swift
//  Project Baby
//
//  Created by Jake thompson on 16/08/2024.
//

import SwiftUI

struct TopMiniCardView: View {
    var body: some View {
        RoundedRectangle(cornerRadius: 25)
            .frame(width: DeviceDimensions().width/1.5, height: DeviceDimensions().height/10)
    }
}

#Preview {
    TopMiniCardView()
}
