package com.example.plantdiseasephenotype.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.example.plantdiseasephenotype.R;
import com.example.plantdiseasephenotype.activities.LoginActivity;

public class ConfirmEmailDialog extends Dialog implements
        android.view.View.OnClickListener{

    public Activity c;

    public ConfirmEmailDialog(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_confirm_email);

        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_okay).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_okay:
                dismiss();
                c.startActivity(new Intent(c, LoginActivity.class));
                c.finish();
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
            default:
                break;
        }
    }

}
