package ca.huynhat.itsasteal.ui;


import com.google.auto.value.AutoValue;

import ca.huynhat.itsasteal.utils.BaseFragment;
import ca.huynhat.itsasteal.utils.BaseKey;


@AutoValue
public abstract class FragmentHomeKey extends BaseKey {

    public static AutoValue_FragmentHomeKey create(){
        return new AutoValue_FragmentHomeKey();
    }

    @Override
    protected BaseFragment createFragment() {
        return new FragmentHome();
    }
}
