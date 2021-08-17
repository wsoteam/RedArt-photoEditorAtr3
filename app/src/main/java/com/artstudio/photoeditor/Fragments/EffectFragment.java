package com.artstudio.photoeditor.Fragments;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.artstudio.photoeditor.R;
import com.artstudio.photoeditor.Utils.AssetsHelper;
import com.artstudio.photoeditor.Utils.Variables;
import com.artstudio.photoeditor.activity.EditActivity;

import java.io.IOException;
import java.util.List;

public class EffectFragment extends Fragment {
    private Bitmap bitmap1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_effect, container, false);
        List<Bitmap> arrayListFrames;
        List<String> arrayListFramesNames = null;

        AssetsHelper assetsHelper = new AssetsHelper();
        try {
            arrayListFramesNames = assetsHelper.getImageNames(Variables.context, "photoeffects");
        } catch (IOException e) {
            e.printStackTrace();
        }
        arrayListFrames = assetsHelper.getImagesFromAsset(Variables.context, arrayListFramesNames, "photoeffects" + "/");

        ConstraintLayout parentContainer = (ConstraintLayout)view.findViewById(R.id.effectLayout);

        HorizontalScrollView scrollView = new HorizontalScrollView(Variables.context);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        parentContainer.addView(scrollView);


        LinearLayout linearLayout = new LinearLayout(Variables.context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutForOuter = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutForOuter);

        scrollView.addView(linearLayout);

        for (int i = 0; i < arrayListFrames.size(); i++) {
            ImageView imageView = new ImageView(Variables.context);
            Bitmap bitmap = arrayListFrames.get(i);

            imageView.setId(i);
            imageView.setAdjustViewBounds(true);

            imageView.setImageBitmap(bitmap);
/////////////////////////////////////////////
            BitmapDrawable drawable = (BitmapDrawable) EditActivity.image.getDrawable();
            Bitmap a = drawable.getBitmap();
            Drawable drawable1 = new BitmapDrawable(getResources(), a);
            imageView.setBackgroundDrawable(drawable1);
//////////////////////////////////////////////
            linearLayout.addView(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bitmap1 = Bitmap.createScaledBitmap(bitmap, Variables.WIDTH, Variables.HEIGHT, false);
                    EditActivity.secondImage.setImageBitmap(bitmap1);
                }
            });
        }
        return view;
    }
}