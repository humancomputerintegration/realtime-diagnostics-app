package com.example.mobilehealthprototype;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.widget.Button;

public class CustomButton {

    public static Button createButton(Context cm, int button_id, int text_id, int fill_col,
                                      int stroke_width, int stroke_col){
        Button temp = new Button(cm);

        GradientDrawable design = (GradientDrawable) cm.getResources().getDrawable(button_id);
        design.setColor(fill_col);
        design.setStroke(stroke_width, stroke_col);
        temp.setBackground(design);
        temp.setText(text_id);
        return temp;
    }

    public static Button createButton(Context cm, int button_id, String button_text, int fill_col,
                                      int stroke_width, int stroke_col){
        Button temp = new Button(cm);

        GradientDrawable design = (GradientDrawable) cm.getResources().getDrawable(button_id);
        design.setColor(fill_col);
        design.setStroke(stroke_width, stroke_col);
        temp.setBackground(design);
        temp.setText(button_text);
        return temp;
    }

    public static Button createButton(Context cm, String button_text){
        Button temp = new Button(cm);
        temp.setText(button_text);
        return temp;
    }

    public static void changeButtonColor(Button button, int fill_color){
        GradientDrawable gd = (GradientDrawable) button.getBackground();
        gd.setColor(fill_color);
        return;
    }

    public static void changeButtonColor(Button button, int fill_color, int stroke, int stroke_col){
        GradientDrawable gd = (GradientDrawable) button.getBackground();
        gd.setColor(fill_color);
        gd.setStroke(stroke, stroke_col);
    }


}
