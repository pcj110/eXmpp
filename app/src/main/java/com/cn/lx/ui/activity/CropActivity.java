package com.cn.lx.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.cn.lx.ui.base.BaseActivity;
import com.cn.lx.util.FileUtil;
import com.steelkiwi.cropiwa.CropIwaView;
import com.steelkiwi.cropiwa.config.ConfigChangeListener;
import com.steelkiwi.cropiwa.config.CropIwaSaveConfig;

import java.io.File;

import estar.com.xmpptest.R;

public class CropActivity extends BaseActivity {

    private static final String EXTRA_URI = "https://pp.vk.me/c637119/v637119751/248d1/6dd4IPXWwzI.jpg";

    public static Intent callingIntent(Context context, Uri imageUri) {
        Intent intent = new Intent(context, CropActivity.class);
        intent.putExtra(EXTRA_URI, imageUri);
        return intent;
    }

    private CropIwaView cropView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        Uri imageUri = getIntent().getParcelableExtra(EXTRA_URI);
        cropView = (CropIwaView) findViewById(R.id.crop_view);
        cropView.setImageUri(imageUri);

        cropView.setCropSaveCompleteListener(new CropIwaView.CropSaveCompleteListener() {
            @Override
            public void onCroppedRegionSaved(Uri bitmapUri) {
                Intent intent = new Intent();
                intent.putExtra("data",bitmapUri.getPath());
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        cropView.setErrorListener(new CropIwaView.ErrorListener() {
            @Override
            public void onError(Throwable e) {
                Toast.makeText(CropActivity.this,"裁剪失败"+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static Uri createNewEmptyFile() {
        return Uri.fromFile(new File(
                FileUtil.getCacheDir(),
                System.currentTimeMillis() + ".png"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_one,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            cropView.crop(new CropIwaSaveConfig.Builder(createNewEmptyFile())
                    .setCompressFormat(Bitmap.CompressFormat.PNG)
                    .setSize(300,300)
                    .setQuality(85)
                    .build());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
