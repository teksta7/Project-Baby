//
//  TestView.swift
//  Project Parent
//
//  Created by Jake thompson on 14/02/2025.
//

import SwiftUI
import Combine

struct TestView: View {
    @StateObject private var activityController = ActivityController.shared
        
        var body: some View {
            VStack {
                VStack(spacing: 8) {
                    Text("Activity ID:")
                        .font(.title3)
                    Text("\(activityController.activityID ?? "-")")
                        .font(.caption2)
                    Text("Activity Token:")
                        .font(.title3)
                    Text("\(activityController.activityToken ?? "-")")
                        .font(.caption2)
                }
                Spacer()
                
                if (activityController.activityID?.isEmpty == false) {
                    VStack {
                    Button("UPDATE LIVE ACTIVITY") {
                        Task {
                            await activityController.updateActivity()
                        }
                    }
                    .font(.headline)
                    .foregroundColor(.black)
                    .frame(maxWidth: .infinity, minHeight: 70)
                    }
                    .background(Color.orange)
                    .frame(maxWidth: .infinity)
                    VStack {
                        Button("STOP LIVE ACTIVITY") {
                            Task {
                                await activityController.cancelAllRunningActivities()
                            }
                        }
                        .font(.headline)
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity, minHeight: 70)
                    }
                    .background(Color.red)
                    .frame(maxWidth: .infinity)
                }
                else {
                    VStack {
                        Button("START LIVE ACTIVITY") {
                            Task {
                                await activityController.start()
                            }
                        }
                        .font(.headline)
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity, minHeight: 70)
                    }
                    .background(Color.blue)
                    .frame(maxWidth: .infinity)
                }
            }
            .padding()
        }
    }

//#Preview {
//    TestView()
//}
struct TestView_Previews: PreviewProvider {
    static var previews: some View {
        TestView()
    }
}

