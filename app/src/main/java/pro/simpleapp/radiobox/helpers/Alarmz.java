package pro.simpleapp.radiobox.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;

import com.onurciner.toastox.ToastOXDialog;
import com.tfb.fbtoast.FBToast;

import pro.simpleapp.radiobox.MainActivity;
import pro.simpleapp.radiobox.PhoneLoginActivity;
import pro.simpleapp.radiobox.R;
import pro.simpleapp.radiobox.dialogs.AlertDialog;
import pro.simpleapp.radiobox.loginView.LoginParseActivity;


public class Alarmz {


    public void als(Activity context) {
        new ToastOXDialog.Build(context)
                .setTitle("Oooopsss...")
                .setContent("You must be a registered user to use this function in the application!")
                .setPositiveText("Yes")
                .setPositiveBackgroundColorResource(R.color.atx_green)
                .setPositiveTextColorResource(R.color.white)
                .onPositive(new ToastOXDialog.ButtonCallback() {
                    @Override
                    public void onClick(@NonNull ToastOXDialog toastOXDialog) {
                        Intent intent = new Intent(context, LoginParseActivity.class);
                        context.startActivity(intent);

                    }
                })
                .setNegativeText("No")
                .setNegativeBackgroundColorResource(R.color.atx_green)
                .setNegativeTextColorResource(R.color.white)
                .onNegative(new ToastOXDialog.ButtonCallback() {
                    @Override
                    public void onClick(@NonNull ToastOXDialog toastOXDialog) {
                        AlertDialog alertDialog = new AlertDialog(context, "We are sorry that you do not want to use all the functions in the application.");
                        alertDialog.show();
                        alertDialog.findViewById(R.id.btn_ook).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();

                            }
                        });

                    }
                }).show();
    }


}
