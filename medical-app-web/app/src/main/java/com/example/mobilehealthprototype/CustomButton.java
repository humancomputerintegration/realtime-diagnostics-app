package com.example.mobilehealthprototype;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.widget.Button;

public class CustomButton {

    public static Button createButton(Context cm, int button_id, int text_id, int fill_col,
                                      int stroke_width, int stroke_col){
        Button temp = new Button(cm);

        GradientDrawable design = (GradientDrawable) cm.getResources().getDrawable(button_id);
        design.setColor(cm.getResources().getColor(fill_col));
        design.setStroke(stroke_width, cm.getResources().getColor(stroke_col));
        temp.setBackground(design);
        temp.setText(text_id);
        return temp;
    }

    public static Button createButton(Context cm, int button_id, String button_text, int fill_col,
                                      int stroke_width, int stroke_col){
        Button temp = new Button(cm);

        GradientDrawable design = (GradientDrawable) cm.getResources().getDrawable(button_id);
        design.setColor(cm.getResources().getColor(fill_col));
        design.setStroke(stroke_width, cm.getResources().getColor(stroke_col));
        temp.setBackground(design);
        temp.setText(button_text);
        return temp;
    }

    public static Button createButton(Context cm, String button_text){
        Button temp = new Button(cm);
        temp.setText(button_text);
        return temp;
    }

    public static void changeButtonColor(Context cm, Button button, int res_col){
        GradientDrawable gd = (GradientDrawable) button.getBackground();
        gd.setColor(cm.getResources().getColor(res_col));
        return;
    }

    public static void changeButtonColor(Context cm, Button button, int res_col, int stroke, int res_stroke){
        GradientDrawable gd = (GradientDrawable) button.getBackground();
        gd.setColor(cm.getResources().getColor(res_col));
        gd.setStroke(stroke, cm.getResources().getColor(res_stroke));
    }

    public static void changeButtonText(Context cm, Button button, int text_col){
        button.setTextColor(cm.getResources().getColor(text_col));
    }

    public static void changeButtonText(Context cm, Button button, int text_col, int font_size){
        button.setTextColor(cm.getResources().getColor(text_col));
        button.setTextSize(font_size);
    }


}
