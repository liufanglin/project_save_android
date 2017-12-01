package com.ximai.savingsmore.save.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class MyLodingDialogUtils {

    
    public static ProgressDialog getInstance(Context context,String msg){

        ProgressDialog dialog =new ProgressDialog(context);
        dialog .setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(msg);
        dialog.setCancelable(true);
        
        return  dialog;
 
    }
    


}
