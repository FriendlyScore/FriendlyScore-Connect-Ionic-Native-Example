//
//  MyPlugin.swift
//  App
//
//  Created by Lukasz Czechowicz on 08/05/2020.
//

import Foundation
import FriendlyScoreConnect
import FriendlyScoreCore
import Capacitor

@objc(FriendlyScoreConnectPlugin)
public class FriendlyScoreConnectPlugin: CAPPlugin {
  @objc func friendlyscoreConnect(_ call: CAPPluginCall) {
    DispatchQueue.main.async {
        let userReference = call.getString("userreference") ?? ""
        let myCredentials = Credentials(clientId: ClientId(stringLiteral: "[YOUR_CLIENT_ID]"), userReference: userReference, environment: .production)
        FriendlyScore.show(with: myCredentials)
        
        FriendlyScore.eventsHandler = { event in
            switch event {
            case .userClosedView:
                self.notifyListeners("userClosedView", data: [:])
            default:
                break
            }
        }
        
        FriendlyScore.errorsHandler = { error in
            switch error {
            case .userReferenceAuth:
                self.notifyListeners("userReferenceAuthError", data: [:])
            case .server:
                self.notifyListeners("serverError", data: [:])
            case .serviceDenied:
                self.notifyListeners("serviceDenied", data: [:])
     
            @unknown default:
                break
            }
        }
    
    }
  }
}
