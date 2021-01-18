package com.example.plantdiseasephenotype.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.example.plantdiseasephenotype.R;

public class CameraInstructionsDialog extends Dialog implements
        android.view.View.OnClickListener{

    public Activity c;

    public CameraInstructionsDialog(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_camera_instructions);

        findViewById(R.id.btn_okay).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_okay:
                dismiss();
                break;
            default:
                break;
        }
    }

}
