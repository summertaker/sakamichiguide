package com.summertaker.sakamichiguide.common;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.util.ImageUtil;
import com.summertaker.sakamichiguide.util.Util;

import java.io.UnsupportedEncodingException;

public class ImageViewActivity extends BaseActivity {

    String mTitle;
    String mUrl;
    String mThumbnailUrl;
    String mImageUrl;

    LinearLayout mLoLoading;
    ImageView mImageView;
    //ProportionalImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_view_activity);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mContext = ImageViewActivity.this;

        Intent intent = getIntent();
        mTitle = intent.getStringExtra("title");
        mUrl = intent.getStringExtra("url");
        mThumbnailUrl = intent.getStringExtra("thumbnailUrl");
        mImageUrl = intent.getStringExtra("imageUrl");

        if (mImageUrl == null || mImageUrl.isEmpty()) {
            mImageUrl = mThumbnailUrl;
        } else {
            if (mImageUrl.contains("%")) {
                try {
                    mImageUrl = java.net.URLDecoder.decode(mImageUrl, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    //Log.e(mTag, "ERROR: " + e.getMessage());
                    //e.printStackTrace();
                    onError();
                }
            }
        }
        initBaseToolbar(Config.TOOLBAR_ICON_BACK, mTitle);

        mLoLoading = (LinearLayout) findViewById(R.id.loLoading);
        ProgressBar pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        Util.setProgressBarColor(pbLoading, Config.PROGRESS_BAR_COLOR_NORMAL, null);

        // http://www.ryadel.com/en/android-proportionally-stretch-imageview-fit-whole-screen-width-maintaining-aspect-ratio/
        mImageView = (ImageView) findViewById(R.id.imageView);
        //mImageView = (ProportionalImageView) findViewById(R.id.imageView);

        final String cacheId = Util.urlToId(mImageUrl);
        final String cacheUri = ImageUtil.getValidCacheUri(cacheId);
        if (cacheUri != null) {
            mImageUrl = cacheUri;
        }

        // https://futurestud.io/blog/glide-callbacks-simpletarget-and-viewtarget-for-custom-view-classes
        Glide.with(mContext).load(mImageUrl).asBitmap().dontAnimate().diskCacheStrategy(DiskCacheStrategy.RESULT)
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        onError();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                        mLoLoading.setVisibility(View.GONE);
                        mImageView.setImageBitmap(bitmap);
                        mImageView.setVisibility(View.VISIBLE);

                        if (cacheUri == null) {
                            ImageUtil.saveBitmapToPng(bitmap, cacheId); // 캐쉬 저장
                        }
                    }
                });
        /*
        ImageLoader.getInstance().displayImage(mImageUrl, mImageView, mDisplayImageOptions, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mLoLoading.setVisibility(View.GONE);
                //mScrollView.setVisibility(View.VISIBLE);
                mImageView.setVisibility(View.VISIBLE);

                if (cacheUri == null) {
                    ImageUtil.saveBitmapToPng(loadedImage, cacheId); // 캐쉬 저장
                }

                //String size = "("+ Util.numberFormat(loadedImage.getWidth()) + " x " + Util.numberFormat(loadedImage.getHeight()) + ")";
                //TextView tvSize = (TextView) findViewById(R.id.tvSize);
                //tvSize.setText(size);
            }
            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                //mLoLoading.setVisibility(View.GONE);
                //alertNetworkErrorAndFinish(null);
                onError();
            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {
            }
        });
        */

        //TextView tvUrl = (TextView) findViewById(R.id.tvUrl);
        //tvUrl.setText(mUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_view, menu);
        //mSaveMenuItem = menu.findItem(R.id.action_save);
        //mSaveMenuItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onError() {
        // https://futurestud.io/blog/glide-callbacks-simpletarget-and-viewtarget-for-custom-view-classes
        Glide.with(mContext).load(mThumbnailUrl).dontAnimate().diskCacheStrategy(DiskCacheStrategy.RESULT)
                .override(120, 147)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        mLoLoading.setVisibility(View.GONE);
                        LinearLayout loError = (LinearLayout) findViewById(R.id.loError);
                        loError.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        mLoLoading.setVisibility(View.GONE);
                        mImageView.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .into(mImageView);
        /*
        ImageLoader.getInstance().displayImage(mThumbnailUrl, mImageView, mDisplayImageOptions, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                mLoLoading.setVisibility(View.GONE);
                LinearLayout loError = (LinearLayout) findViewById(R.id.loError);
                loError.setVisibility(View.VISIBLE);
                //TextView tvUrl = (TextView) findViewById(R.id.tvUrl);
                //tvUrl.setText(mImageUrl);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mLoLoading.setVisibility(View.GONE);
                //mScrollView.setVisibility(View.VISIBLE);
                mImageView.setVisibility(View.VISIBLE);

                //String size = "("+ Util.numberFormat(loadedImage.getWidth()) + " x " + Util.numberFormat(loadedImage.getHeight()) + ")";
                //TextView tvSize = (TextView) findViewById(R.id.tvSize);
                //tvSize.setText(size);
            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {
            }
        });
        */
    }

    private void goSite() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUrl));
        startActivity(intent);
    }

    @Override
    public void onBackPressed() { // only sdk 2.2 and higher supports
        doFinish();
        super.onBackPressed();
    }
}
