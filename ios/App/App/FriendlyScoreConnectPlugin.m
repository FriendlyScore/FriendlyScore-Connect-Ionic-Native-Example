//
//  MyPlugin.m
//  App
//
//  Created by Lukasz Czechowicz on 08/05/2020.
//

#import <Foundation/Foundation.h>

#import <Capacitor/Capacitor.h>

CAP_PLUGIN(FriendlyScoreConnectPlugin, "FriendlyScoreConnectPlugin",
  CAP_PLUGIN_METHOD(friendlyscoreConnect, CAPPluginReturnPromise);
)
