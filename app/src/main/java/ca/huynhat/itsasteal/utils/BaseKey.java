package ca.huynhat.itsasteal.utils;


import android.os.Bundle;
import android.os.Parcelable;

/**
 * Ref: https://github.com/Zhuinden/simple-stack/blob/master/simple-stack-example-basic-fragment/src/main/java/com/zhuinden/navigationexamplefrag/BaseKey.java
 */
public abstract class BaseKey implements Parcelable {
    public String getFragmentTag(){
        return toString();
    }

    public final BaseFragment newFragment() {
        BaseFragment fragment = createFragment();
        Bundle bundle = fragment.getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putParcelable("KEY", this);
        fragment.setArguments(bundle);
        return fragment;
    }

    protected abstract BaseFragment createFragment();
}
