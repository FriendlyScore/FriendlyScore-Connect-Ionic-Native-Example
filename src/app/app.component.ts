import { Component } from '@angular/core';

import { Platform } from '@ionic/angular';
import { SplashScreen } from '@ionic-native/splash-screen/ngx';
import { StatusBar } from '@ionic-native/status-bar/ngx';

import { Plugins } from "@capacitor/core";
const { FriendlyScoreConnectPlugin } = Plugins;



// Other codes...
@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss']
})
export class AppComponent {
  constructor(
    private platform: Platform,
    private splashScreen: SplashScreen,
    private statusBar: StatusBar
  ) {
    this.initializeApp();
  }

  startfs(){
    this.fsEventHandler()
    FriendlyScoreConnectPlugin.friendlyscoreConnect({ userreference: "your_user_reference" });
    
  }

  fsEventHandler(){
    FriendlyScoreConnectPlugin.addListener("userClosedView", (info:any) => {
      console.log(info)
    });
    FriendlyScoreConnectPlugin.addListener("serverError", (info:any) => {
    });
    FriendlyScoreConnectPlugin.addListener("incompleteConfiguration", (info:any) => {
         console.log(info)
    });
    FriendlyScoreConnectPlugin.addListener("serviceDenied", (info:any) => {
         console.log(info)
    });
    FriendlyScoreConnectPlugin.addListener("userReferenceAuthError", (info:any) => {
    });
  }
  initializeApp() {
    this.platform.ready().then(() => {
      this.statusBar.styleDefault();
      this.splashScreen.hide();
    });
  }
}
