package com.android.sergeyfitis.geektalksdemo.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.util.Pair;
import android.support.v7.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.concurrent.ExecutionException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Serhii Yaremych on 31.10.2015.
 */
public class RxUtils {
    public static <T> Observable.Transformer<T, T> applySchedulers() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<Pair<Palette, Bitmap>> generatePalette(@NonNull String url, @NonNull Context context, boolean roundBitmap) {
        return Observable.create(
                new Observable.OnSubscribe<Pair<Palette, Bitmap>>() {
                    @Override
                    public void call(Subscriber<? super Pair<Palette, Bitmap>> subscriber) {
                        try {
                            Bitmap bitmap = Glide.with(context)
                                    .load(url)
                                    .asBitmap()
                                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                    .into(200, 200)
                                    .get();
                            if (bitmap != null) {
                                Palette palette = new Palette.Builder(bitmap)
                                        .generate();
                                if (roundBitmap) {
                                    Bitmap bitmapRound = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

                                    Canvas canvas = new Canvas(bitmapRound);
                                    Paint paint = new Paint();
                                    BitmapShader shader = new BitmapShader(bitmap,
                                            BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
                                    paint.setShader(shader);
                                    paint.setAntiAlias(true);

                                    float r = bitmap.getWidth() / 2f;
                                    canvas.drawCircle(r, r, r, paint);
                                    bitmap = bitmapRound;
                                }
                                subscriber.onNext(new Pair<>(palette, bitmap));
                                subscriber.onCompleted();
                            } else {
                                subscriber.onError(new Throwable("Can't generate palette"));
                            }
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                            subscriber.onError(e);
                        }
                    }
                }
        ).compose(applySchedulers());
    }

}
