package pro.simpleapp.radiobox.dialogs;

import android.animation.ObjectAnimator;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;



public class TopAlert {

    public void build(String txt, LottieAnimationView anim, TextView txtview, String animFile, final View view) {
        slideDown(view);
        anim.setAnimation(animFile);
        anim.playAnimation();
        txtview.setText(txt);
        Handler ha = new Handler();
        ha.postDelayed(new Runnable() {
            @Override
            public void run() {
                slideUp(view);
            }
        }, 6000);

    }


    private void slideUp(View view) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationY", -250);
        objectAnimator.setDuration(250);
        objectAnimator.start();

    }


    private void slideDown(View view) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationY", 250);
        objectAnimator.setDuration(250);
        objectAnimator.start();

    }


}
