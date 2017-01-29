package info.nukoneko.cuc.android.kidspos.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import info.nukoneko.cuc.android.kidspos.AppController;
import info.nukoneko.cuc.android.kidspos.R;

/**
 * Created by TEJNEK on 2015/10/04.
 */
public class KPToast {
    public static void showToast(String text, Integer duration){
        Context context = AppController.get().getApplicationContext();
        try{
            TextView view = (TextView) AppUtils.getInflateView(context, R.layout.textview_toast_corner_rect);
            if(view == null) throw new IllegalArgumentException();
            view.setGravity(Gravity.CENTER_HORIZONTAL);
            view.setText(text);
            Toast toast = new Toast(context);
            toast.setView(view);
            toast.setDuration(duration);
            toast.show();
        }catch (Exception e){
            try{
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }catch (Exception ignore){
            }
        }
    }
    public static void showToast(Integer textID){
        Context context = AppController.get().getApplicationContext();
        showToast(context.getString(textID), Toast.LENGTH_SHORT);
    }
    public static void showToast(String text){
        showToast(text, Toast.LENGTH_LONG);
    }
}
