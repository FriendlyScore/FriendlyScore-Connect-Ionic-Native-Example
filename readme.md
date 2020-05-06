# Ionic Implementation

## Overview

Here you can find instructions on how to integrate and use FriendlyScore Connect for Ionic.

To get started quickly with FriendlyScore Connect for Ionic, clone the [GitHub repository](https://github.com/FriendlyScore/FriendlyScore-Connect-Ionic-Native-Example) and run the example. You need to [sign-up](https://friendlyscore.com/getting-started) for the free API keys through our Developer Console.

## Requirements
- [FriendlyScore API keys](https://friendlyscore.com/company/keys)
### Ionic
- Ionic CLI(demo uses version 6.7.0)            
- Ionic Framework(demo uses @ionic/angular 5.1.0)
- Capacitor:
   Capacitor CLI   : 2.0.2
   @capacitor/core : 2.0.2
### Android
- Install or update Android Studio version 3.2 or greater
- Android 5.0 and greater


## Quickstart Demo App

Clone and run the demo project from our [GitHub repository](https://github.com/FriendlyScore/FriendlyScore-Connect-Ionic-Native-Example). 

## Ionic Android Implementation 

We will create the `Plugin` within the `android` section of the App that will enable interaction between the UI component and Native Android code.

If you do not have an `android` directory in your project, execute
```bash
    ionic capacitor add android
```


### Installation

Please follow the instructions below to install FriendlyScore Connect Android Native library, provide the necessary configuration and understand the flow.


#### Add the following values to your project-level build.gradle file

In your project-level Gradle file (you can find an example in the demo [build.gradle](https://github.com/FriendlyScore/FriendlyScore-Connect-Ionic-Native-Example/blob/master/android/build.gradle)), add rules to include the Android Gradle plugin. The version should be equal to or greater than `3.2.1`.

```groovy
buildscript {
    dependencies {
        classpath 'com.android.tools.build:gradle:3.2.1'
    }
}  
```
  
#### Add the following values to your project-level build.gradle file

In your project-level Gradle file (you can find an example in the demo, [build.gradle](https://github.com/FriendlyScore/FriendlyScore-Connect-Ionic-Native-Example/blob/master/android/build.gradle)), add the Jitpack Maven repository:

```groovy
allprojects {
  repositories {
    maven { url 'https://jitpack.io' } // Include to import FriendlyScore Connect dependencies
  }
}
```
#### Add configuration to your app
   
Go to the [Redirects](https://friendlyscore.com/company/redirects) section of the FriendlyScore developer console and provide your `App Package Id` and `App Redirect Scheme`.
   
You will also need your [Client Id](https://friendlyscore.com/company/keys) for the specific environment (SANDBOX, DEVELOPMENT, PRODUCTION). 
   
In the project-level properties file (you can find an example in the demo [gradle.properties](https://github.com/FriendlyScore/FriendlyScore-Connect-Ionic-Native-Example/blob/master/android/gradle.properties)), please add the following configuration values:

```bash
# Client Id value is specified in the keys section of the developer console.
# Use the Client Id for the correct ENVIRONMENT.

CLIENT_ID=client_id

# App Redirect Scheme value is in the Redirects section of the developer console.
# You must specify the value the SDK will use for android:scheme to redirect back to your app. https://developer.android.com/training/app-links/deep-linking

APP_REDIRECT_SCHEME=app_redirect_scheme
```

#### Add the following values to your App Level build.gradle file(In the demo, [app/build.gradle](https://github.com/FriendlyScore/FriendlyScore-Connect-Ionic-Native-Example/blob/master/android/app/build.gradle))
  
Now we must read the configuration to create the string resources that will be used by the FriendlyScore Connect Android library. Also we will include the FriendlyScore Connect Library.

```groovy
android {
  compileOptions {
  sourceCompatibility 1.8
  targetCompatibility 1.8
  }

  defaultConfig {
    resValue "string", "fs_client_id", (project.findProperty("CLIENT_ID") ?: "NO_CLIENT_ID")
    resValue "string", "fs_app_redirect_scheme", (project.findProperty("APP_REDIRECT_SCHEME") ?: "NO_APP_REDIRECT_SCHEME_PROVIDED")
  }
}

dependencies {
   api 'com.github.friendlyscore.fs-android-sdk:friendlyscore-connect:latest.release'
}
```

You can select the environment you want to use:

| Environment  |   Description   |
| :----       | :--             |
| Environments.SANDBOX     | Use this environment to test your integration with Unlimited API Calls |
| Environments.DEVELOPMENT | Use this your environment to test your integration with live but limited Production API Calls |
| Environments.PRODUCTION  | Production API environment |

### Plugin Implemenation

The `plugin` provides a way for native code to interact with cross-platform Ionic components.

The [FriendlyScoreConnectPlugin.java](https://github.com/FriendlyScore/FriendlyScore-Connect-Ionic-Native-Example/blob/master/android/app/src/main/java/com/demo/friendlyscore/connect/FriendlyScoreConnectPlugin.java) provides function that can be called from the Ionic components.

You must pass `userReference` that identifies the user to the Plugin.

Also you must declare the plugin such that it is able to handle the result when the FriendlyScore Connect flow is completed.

```java
@NativePlugin(
        requestCodes={FriendlyScoreConnectPlugin.REQUEST_CODE_FRIENDLY_SCORE}
)
public class FriendlyScoreConnectPlugin extends Plugin {

    /**
     In order to listen when the user returns from the FriendlyScoreView in your `onActivityResult`, you must provide the `requestcode` that you will be using.
     */

     public static final int REQUEST_CODE_FRIENDLY_SCORE = 11;
    /**
     In order to initialize FriendlyScore for your user you must have the `userReference` for that user.
     The `userReference` uniquely identifies the user in your systems.
     This `userReference` can then be used to access information from the FriendlyScore [api](https://friendlyscore.com/developers/api).
     */
    public String userReference = "your_user_reference";


    @PluginMethod
    public void friendlyscoreConnect(PluginCall call){
        /**
         You must save the call, it is required for Null check in onHandleActivityResult
        */
        saveCall(call);
        userReference = call.getString("userreference");
        startFriendlyScore();
    }

     /**
     * Declare the environment to use the FriendlyScore Connect.
     * The client_id declared in gradle.properties must be for the same environment
     */
    public Environments environment = Environments.PRODUCTION;

    /**
        The method that triggers the FriendlyScore Connect Flow.

        The Plugin by default works in the background thread, thus you must call the ui on the runOnUiThread as shown below.
    */
    public void startFriendlyScore() {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                FriendlyScoreView.Companion.startFriendlyScoreView( getActivity(),  getActivity().getString(R.string.fs_client_id), userReference, REQUEST_CODE_FRIENDLY_SCORE, environment);
            }
        });
    }
```
### Plugin Handle Result
In order to get the result back from the FriendlyScore Connect Flow you must override the `onHandleActivityResult` method of the `Plugin`

```java
     /**
     In order to listen when the user returns from the FriendlyScoreView in your `onActivityResult`, you must provide the `requestcode` that you will be using.
     */
    
    @Override
    protected void handleOnActivityResult(int requestCode, int resultCode, Intent data)  {
        super.handleOnActivityResult(requestCode, resultCode, data);
        JSObject connectObject = new JSObject();
        PluginCall savedCall = getSavedCall();

        if (savedCall == null) {
            return;
        }
        if(requestCode == REQUEST_CODE_FRIENDLY_SCORE){
            if(data!=null){

                //Present if there was error in creating an access token for the supplied userReference.
                if(data.hasExtra("userReferenceAuthError")){
                    connectObject = new JSObject();
                    notifyListeners("userReferenceAuthError",connectObject);
                    //Do Something
                }

                //Present if there was service denied.
                if( data.hasExtra("serviceDenied")){
                    connectObject = new JSObject();
                    if(data.hasExtra("serviceDeniedMessage")){
                        String serviceDeniedMessage = data.getStringExtra("serviceDeniedMessage");
                        if(serviceDeniedMessage!=null)
                        connectObject.put("serviceDeniedMessage", serviceDeniedMessage);
                    }
                    notifyListeners("serviceDenied",connectObject);
                }
                //Present if the configuration on the server is incomplete.
                if(data!=null && data.hasExtra("incompleteConfiguration")){
                    connectObject = new JSObject();
                    if(data.hasExtra("incompleteConfigurationMessage")){
                        String errorDescription = data.getStringExtra("incompleteConfigurationMessage");
                        if(errorDescription!=null)
                        connectObject.put("incompleteConfigurationMessage", errorDescription);

                    }
                    notifyListeners("incompleteConfiguration",connectObject);
                }
                //Present if there was error in obtaining configuration from server
                if(data.hasExtra("serverError")){
                    connectObject = new JSObject();
                    notifyListeners("serverError",connectObject);
                }
                //Present if the user closed the flow
                if(data.hasExtra("userClosedView")){
                    connectObject = new JSObject();
                    //The user closed the process
                    notifyListeners("userClosedView",connectObject);
                }
            }
        }
    }
```
The native component dispatches events to pass the data in the `onHandleActivityResult` to the Ionic Cross Platform component.

## Ionic UI Component

### Add a Button 

This includes the ui elements such as button that a user will click to trigger the FriendlyScore Connect Flow.
In your app component html file (in the demo [app.component.html](https://github.com/FriendlyScore/FriendlyScore-Connect-Ionic-Native-Example/blob/master/src/app/app.component.html)) add a button that triggers the FriendlyScore Connect flow.
```html
    <ion-content>
        <div style="display: flex; justify-content: center; " text-center align-items-center>
            <ion-button (click)="startfs()" color="primary">Start FriendlyScore Connect</ion-button>
        </div>
    </ion-content>

```

### Plugin interaction

The plugin declared earlier in the `Native Implementation` has to be called with the `userReference` to initiate the flow.

In your component script file(in the demo[app.component.ts](https://github.com/FriendlyScore/FriendlyScore-Connect-Ionic-Native-Example/blob/master/src/app/app.component.ts)) create a function that will be called when the button is clicked.

```javascript
import { Plugins } from "@capacitor/core";
const { FriendlyScoreConnectPlugin } = Plugins;

/**
 * This method is called when the user clicks a button.
 * 1. It initiates a listener for events passed from the Plugin.
 * 2. Initiates the call with the userReference as the parameter to the Plugin to start the flow.
 * 
*/
startfs(){
    this.fsEventHandler()
    FriendlyScoreConnectPlugin.friendlyscoreConnect({ userreference: "your_user_reference" });
}

fsEventHandler(){
    FriendlyScoreConnectPlugin.addListener("userClosedView", (info:any) => {
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

```


## Error Definition
| Error                     | Definitions  | 
| -------------             | -------------|
| userReferenceAuthError   | Present if there was an authentication error for the supplied `userReference`. 
| serviceDenied             | Present if service was denied. Please check the description for more information.
| incompleteConfiguration   | Present if the configuration on the server is incomplete. Please check the description for more information.
| serverError               | Present if there was a critical error on the server.      

## Response State Definition
| State                    | Definitions  | 
| -------------             | -------------|
| userClosedView            | Present if the user closed the FriendlyScore flow.      

## Next Steps

### Access to Production Environment

You can continue to integrate FriendlyScore Connect in your app in our sandbox and development environments. Once you have completed testing, you can request access to the production environment in the developer console or speak directly to your account manager.

### Support 

Find commonly asked questions and answers in our [F.A.Q](https://friendlyscore.com/developers/faq). You can also contact us via email at [developers@friendlyscore.com](mailto:developers@friendlyscore.com) or speak directly with us on LiveChat.

You can find all the code for FriendlyScore Connect for Web component, iOS and Android on our [GitHub](https://github.com/FriendlyScore).
