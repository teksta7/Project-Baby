//
//  IAPController.swift
//  Project Parent
//
//  Created by Jake thompson on 15/12/2024.
//

import Foundation
import StoreKit


@MainActor
class IAPController: ObservableObject
{
    private let productIds = ["com.teksta.projectparent.noads"]
    
    @Published
    private(set) var products: [Product] = []
    private var productsLoaded = false
    
    @Published
    private(set) var purchasedProductIDs = Set<String>()
    
    var hasUnlockedNoAds: Bool {
        print(self.purchasedProductIDs.isEmpty)
        return !self.purchasedProductIDs.isEmpty
    }
    
    private var updates: Task<Void, Never>? = nil
    
    init() {
        updates = observeTransactionUpdates()
    }
    
    deinit {
        updates?.cancel()
    }
    
    private func observeTransactionUpdates() -> Task<Void, Never> {
        Task(priority: .background) { [unowned self] in
            for await verificationResult in Transaction.updates {
                // Using verificationResult directly would be better
                // but this way works for this tutorial
                //await verificationResult.
                await self.updatePurchasedProducts()
            }
        }
    }
        
        func loadProducts() async throws
        {
            print("loading app products that can be purchased")
            guard !self.productsLoaded else { return }
            self.products = try await Product.products(for: productIds)
            self.productsLoaded = true
        }
        
        func updatePurchasedProducts() async {
            for await result in Transaction.currentEntitlements {
                guard case .verified(let transaction) = result else {
                    continue
                }
                
                if transaction.revocationDate == nil {
                    self.purchasedProductIDs.insert(transaction.productID)
                } else {
                    self.purchasedProductIDs.remove(transaction.productID)
                }
            }
        }
        
        func purchase(_ product: Product) async throws
        {
            let result = try await product.purchase()
            
            switch result
            {
            case let .success(.verified(transaction)):
                //Successful Purchase
                await transaction.finish()
                await self.updatePurchasedProducts()
                AdCoordinator().disableAds()
            case let .success(.unverified(_, error)):
                //Successful Purchase but transaction cannot be verified
                break
            case .pending:
                //Transaction waiting or requires approval from ask to buy
                break
            case .userCancelled:
                //^^^
                break
            @unknown default:
                break
            }
        }
    }
