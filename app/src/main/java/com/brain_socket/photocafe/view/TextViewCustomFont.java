package com.brain_socket.photocafe.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.brain_socket.photocafe.PhotoCafeApp;
import com.brain_socket.photocafe.PhotoCafeApp.SUPPORTED_LANGUAGE;
import com.brain_socket.photocafe.R;

/**
 * <p> This is a normal TextView widget that can have a custom Font.
 * The fonts are referenced by an id listed below:
 *
 * <p> font dinarOneLight: fontId = 1
 * <p> font scDubai: fontId = 2
 *
 * <p> The fontId attribute can be set in the XML definition of the custom TextView
 * by setting app:fontId="id"
 *
 * @author Nabil Souk
 *
 */
public class TextViewCustomFont extends TextView
{

    // fonts
    private static Typeface fontFaceRegular = null;
    private static Typeface fontFaceBold = null;
    private static Typeface fontFaceXBold = null;

    public TextViewCustomFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }
    public TextViewCustomFont(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs){
        try {
            if(!isInEditMode()) {
                // get the typed array for the custom attrs
                TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);
                // get fontId set in the XML
                int fontId = a.getInteger(R.styleable.CustomFontTextView_fontId, 0);
                // check fontId if equal to any or the predefined ids for the custom fonts
                switch (fontId) {
                    case 1:
                        this.setTypeface(getTFRegular(getContext()));
                        break;
                    case 2:
                        this.setTypeface(getTFBold(getContext()));
                        break;
                    case 3:
                        this.setTypeface(getTFXBold(getContext()));
                        break;
                    default:
                        this.setTypeface(getTFRegular(getContext()));
                        break;
                }

                //Don't forget this
                a.recycle();
            }
        }
        catch (Exception ignored) {}
    }

    public static Typeface getTFRegular(Context context){
        try {
            if(fontFaceRegular == null) {
                String path = "fonts/Antipasto_extralight.otf";
                if(PhotoCafeApp.getCurrentLanguage() == SUPPORTED_LANGUAGE.AR)
                    path = "fonts/DroidKufiRegular.ttf";
                fontFaceRegular = Typeface.createFromAsset(context.getAssets(), path);
            }
        }catch (Exception e) {
            fontFaceRegular = Typeface.DEFAULT;
        }
        return fontFaceRegular;
    }

    /**
     * @return Returns bold typeface used in the app based on the current active lang
     */
    public static Typeface getTFBold(Context context){
        try {
            if(fontFaceBold == null) {
                String path = "fonts/Antipasto_regular.otf";
                if(PhotoCafeApp.getCurrentLanguage() == SUPPORTED_LANGUAGE.AR)
                    path = "fonts/DroidKufiRegular.ttf";
                fontFaceBold = Typeface.createFromAsset(context.getAssets(), path);
            }
        }catch (Exception e) {
            fontFaceBold = Typeface.DEFAULT;
        }
        return fontFaceBold;
    }

    public static Typeface getTFXBold(Context context){
        try {
            if(fontFaceXBold == null) {
                String path = "fonts/Antipasto_extrabold.otf";
                if(PhotoCafeApp.getCurrentLanguage() == SUPPORTED_LANGUAGE.AR)
                    path = "fonts/DroidKufiRegular.ttf";
                fontFaceXBold = Typeface.createFromAsset(context.getAssets(), path);
            }
        }catch (Exception e) {
            fontFaceXBold = Typeface.DEFAULT;
        }
        return fontFaceXBold;
    }
}