package ca.huynhat.itsasteal.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import ca.huynhat.itsasteal.models.Deal;

public class HomeFeedViewModel extends ViewModel {
    private static final String TAG = HomeFeedViewModel.class.getSimpleName();

    private MutableLiveData<List<Deal>> deals;

    public LiveData<List<Deal>> getDeals(){
        if(deals == null){
            deals = new MutableLiveData<List<Deal>>();
            loadDeals();
        }
        return deals;
    }

    private void loadDeals(){
        // Do an asynchronous operation to fetch users.
        // do async operation to fetch users

        Handler myHandler = new Handler();
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<Deal> dealList = new ArrayList<>();
                dealList.add(new Deal("1", "Apple iPhone 6"
                        ,19, 300.0 ,"BestBuy",
                        1.1,1.1,"","","1" ));
                dealList.add(new Deal("2", "Apple iPhone 7",
                        19,400.0, "The Source",
                        1.1,1.1,"","","2" ));
                dealList.add(new Deal("3", "Apple iPhone 8",
                      19,500.0 ,"Canada Computers",
                        1.1,1.1,"","","3" ));


//            long seed = System.nanoTime();
//            Collections.shuffle(dealList, new Random(seed));

                deals.setValue(dealList);
            }
        } , 3000);

    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "on cleared called");
    }
}
