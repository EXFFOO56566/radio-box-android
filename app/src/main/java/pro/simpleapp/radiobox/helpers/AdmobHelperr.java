package pro.simpleapp.radiobox.helpers;

import static pro.simpleapp.radiobox.prfs.Prfs.ADS_OFF;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Arrays;

import pro.simpleapp.radiobox.R;


public class AdmobHelperr {

    AdRequest adRequest;
    AdView adView;





    public void setsAdmob(Activity context, LinearLayout ads_lay) {









        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        MobileAds.setRequestConfiguration(
                new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("8337C5C6D61B420A95446DB430DCAE77","BC37E9C4D9F7776D675EA3C0A7862750"))
                        .build());

        adView = new AdView(context);


        adRequest = new AdRequest.Builder().build();
        adView.setAdUnitId(context.getString(R.string.banner_id));

        adView.setAdSize(AdSize.BANNER);
        ads_lay.addView(adView);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);

                System.out.println("ERROR = " + loadAdError.toString());
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();


            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }
        });


        if (ADS_OFF) {

            ViewGroup.LayoutParams params = ads_lay.getLayoutParams();
            params.height = 0;
            params.width = 100;
            ads_lay.setLayoutParams(params);

        } else {
            adView.loadAd(adRequest);


        }

    }




}


