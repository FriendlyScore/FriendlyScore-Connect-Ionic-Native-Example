package com.demo.friendlyscore.connect;

import android.content.Intent;
import android.util.Log;

import com.friendlyscore.base.Environments;
import com.friendlyscore.ui.obp.FriendlyScoreView;
import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import io.ionic.starter.R;

@NativePlugin(
        requestCodes={FriendlyScoreConnectPlugin.REQUEST_CODE_FRIENDLY_SCORE}
)
public class FriendlyScoreConnectPlugin extends Plugin {
    public static String TAG = FriendlyScoreConnectPlugin.class.getSimpleName();
    @PluginMethod
    public void friendlyscoreConnect(PluginCall call){
        saveCall(call);
        userReference = call.getString("userreference");
        startFriendlyScore();
    }

    /**
     In order to initialize FriendlyScore for your user you must have the `userReference` for that user.
     The `userReference` uniquely identifies the user in your systems.
     This `userReference` can then be used to access information from the FriendlyScore [api](https://friendlyscore.com/developers/api).
     */
    public String userReference = "your_user_reference";
    /**
     * Declare the environment to use the FriendlyScore Connect.
     * The client_id declared in gradle.properties must be for the same environment
     */
    public Environments environment = Environments.PRODUCTION;

    /**
     In order to listen when the user returns from the FriendlyScoreView in your `onActivityResult`, you must provide the `requestcode` that you will be using.
     */
    public static final int REQUEST_CODE_FRIENDLY_SCORE = 11;

    public void startFriendlyScore() {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                FriendlyScoreView.Companion.startFriendlyScoreView( getActivity(),  getActivity().getString(R.string.fs_client_id), userReference, REQUEST_CODE_FRIENDLY_SCORE, environment);
            }
        });
    }


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

                    //Try again later
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
}
