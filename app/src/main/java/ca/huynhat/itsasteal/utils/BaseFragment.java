package ca.huynhat.itsasteal.utils;

import android.os.Parcelable;
import android.support.v4.app.Fragment;

/**
 * Ref: https://github.com/Zhuinden/simple-stack/blob/master/simple-stack-example-basic-fragment/src/main/java/com/zhuinden/navigationexamplefrag/BaseFragment.java
 */


public class BaseFragment extends Fragment {
    public final <T extends BaseKey & Parcelable> T getKey(){
        return getArguments() != null ? getArguments().<T>getParcelable("KEY"):null;
    }


}
