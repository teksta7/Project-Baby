//
//  BottleListView.swift
//  Project Baby
//
//  Created by Jake thompson on 26/08/2024.
//

import SwiftUI

struct BottleListView: View {
    var body: some View {
        NavigationView {
            VStack
            {
                Text("List of bottles to be displayed here")
                List {
                    Text("Example A")
                    Text("Example B")
                    Text("Example C")
                }
            }
            .navigationTitle("Bottle History")
        }
        .preferredColorScheme(.dark)
       
    }
}

#Preview {
    BottleListView()
}
