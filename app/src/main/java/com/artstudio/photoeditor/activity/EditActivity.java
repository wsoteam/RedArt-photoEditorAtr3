package com.artstudio.photoeditor.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.artstudio.photoeditor.Utils.AssetsHelper;
import com.google.android.material.tabs.TabLayout;
import com.artstudio.photoeditor.Adapter.ViewAdapter;
import com.artstudio.photoeditor.Fragments.EffectFragment;
import com.artstudio.photoeditor.Fragments.FilterFragment;
import com.artstudio.photoeditor.Fragments.FramesFragment;
import com.artstudio.photoeditor.Fragments.GradientsFragment;
import com.artstudio.photoeditor.Fragments.MirrorFragment;
import com.artstudio.photoeditor.Fragments.ReflectionFragment;
import com.artstudio.photoeditor.Fragments.RotationFragment;
import com.artstudio.photoeditor.Fragments.BrightnessFragment;
import com.artstudio.photoeditor.R;
import com.artstudio.photoeditor.Utils.CombiningPictures;
import com.artstudio.photoeditor.Utils.Variables;

import java.io.IOException;
import java.util.List;

public class EditActivity extends AppCompatActivity {
    private final int IMAGE_REQUEST_CODE = 13;
    private  ViewAdapter adapter;
    @SuppressLint("StaticFieldLeak")
    public static ImageView secondImage, image;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Bitmap resetImage;
    private AssetsHelper assetsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        init();
        image.setImageResource(R.drawable.temple);
        assetsHelper.getStockImageSize(image);
        generateFragments();
    }

    private void init() {
        assetsHelper = new AssetsHelper();
        Variables.context = getApplicationContext();
        image = findViewById(R.id.image);
        secondImage = findViewById(R.id.secondImage);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tab_layout);
        secondImage.setImageResource(R.drawable.transparent_image);
    }

    private void generateFragments() {
        adapter = new ViewAdapter(getSupportFragmentManager());
        adapter.clear();
        adapter.addFragment(new EffectFragment(), "");
        adapter.addFragment(new GradientsFragment(), "");
        adapter.addFragment(new FramesFragment(), "");
        adapter.addFragment(new FilterFragment(), "");
        adapter.addFragment(new RotationFragment(), "");
        adapter.addFragment(new ReflectionFragment(), "");
        adapter.addFragment(new BrightnessFragment(), "");
        adapter.addFragment(new MirrorFragment(), "");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        try {
            setTabIcon();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveImageToGallery(ImageView imageView){
        imageView.setDrawingCacheEnabled(true);
        Bitmap b = imageView.getDrawingCache();
        MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), b,
                getString(R.string.title) + image.getId(), "Image");
        Toast.makeText(this, "Please wait while the photo is being saved", Toast.LENGTH_SHORT).show();
    }

    private void setTabIcon() throws IOException {
        List<String> names = assetsHelper.getImageNames(this, "icons");
        List<Bitmap> array = assetsHelper.getImagesFromAsset(this, names, "icons" + "/");
        for(int i = 0; i < array.size(); i++) {
            Drawable drawable = new BitmapDrawable(getResources(), array.get(i));
            tabLayout.getTabAt(i).setIcon(drawable);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_open) {
            if(ActivityCompat.checkSelfPermission(EditActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(EditActivity.this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 32);
            } else {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, IMAGE_REQUEST_CODE);
            }
            return true;
        }

        if (id == R.id.action_apply) {
            Bitmap bot = CombiningPictures.overlay(image, secondImage);
            image.setImageDrawable(null);
            secondImage.setImageResource(R.drawable.transparent_image);
            image.setImageBitmap(bot);
            Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
            return true;
        }

        if(id == R.id.action_clear) {
            secondImage.setImageResource(R.drawable.transparent_image);
            Toast.makeText(this, "Clear", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.action_reset) {
            if(resetImage != null) {
                image.setImageBitmap(resetImage);
                secondImage.setImageResource(R.drawable.transparent_image);
            } else {
                Toast.makeText(this, "Reset", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        if (id == R.id.action_save) {
            saveImageToGallery(image);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST_CODE) {
            if(data.getData() != null) {
                Uri filePath = data.getData();
                Variables.URI_IMAGE = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath);
                    if (bitmap.getWidth() > 2000) {
                        bitmap = assetsHelper.getResizedBitmap(bitmap, 500);

                        Variables.HEIGHT = bitmap.getWidth();
                        Variables.WIDTH = bitmap.getHeight();
                    } else {
                        Variables.WIDTH = bitmap.getWidth();
                        Variables.HEIGHT = bitmap.getHeight();
                    }
                    image.setImageBitmap(bitmap);
                    BitmapDrawable getResetImage = (BitmapDrawable) image.getDrawable();
                    resetImage = getResetImage.getBitmap();
                    generateFragments();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
    }
}