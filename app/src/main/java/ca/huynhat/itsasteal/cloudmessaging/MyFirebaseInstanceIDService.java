package ca.huynhat.itsasteal.cloudmessaging;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String instanceId = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + instanceId);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null){

            //TODO: replace with firestore collection
//            FirebaseDatabase.getInstance().getReference().child("users")
//                    .child(firebaseUser.getUid()).child("instanceId").setValue(instanceId);
        }
    }
}
