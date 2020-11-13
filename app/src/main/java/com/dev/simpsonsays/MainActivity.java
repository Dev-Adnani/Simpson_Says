package com.dev.simpsonsays;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialog;
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialogListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    private ImageView imageViewChar;
    private TextView tvQuote, tvCharName;
    private long lastClickTime = 0;
    private CardView cardView;
    Vibrator vibrator;


    private void statusBar() {
        Window window = MainActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.light_blue));
    }

    public void About(View view) {
        Intent i = new Intent(getApplicationContext(), AboutActivity.class);
        startActivity(i);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void SetWallpaper(View view)
    {
        Bitmap bitmap = getBitmapFromView(cardView);
        WallpaperManager myWallpaperManager = WallpaperManager.getInstance(getApplicationContext());

        new TTFancyGifDialog.Builder(MainActivity.this)
                .setTitle("Wallpaper")
                .setMessage("You Are About To Set The Funniest Wallpaper ")
                .setPositiveBtnText("Home Screen")
                .setPositiveBtnBackground("#22b573")
                .setNegativeBtnText("Lock Screen")
                .setNegativeBtnBackground("#c1272d")
                .setGifResource(R.drawable.alert)      //pass your gif, png or jpg
                .isCancellable(true)
                .OnPositiveClicked(new TTFancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        try {
                            myWallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                            Toasty.success(getApplicationContext(), R.string.success, Toast.LENGTH_SHORT, true).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                })
                .OnNegativeClicked(new TTFancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        try {
                            myWallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);
                            Toasty.success(getApplicationContext(), R.string.success, Toast.LENGTH_SHORT, true).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }                    }
                })
                .build();


    }

    public void NextQuote(View view) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (SystemClock.elapsedRealtime() - lastClickTime < 500) {
            return;
        }

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            init();
        } else {
            Toasty.error(getApplicationContext(), R.string.network_check, Toast.LENGTH_SHORT, true).show();
        }

        lastClickTime = SystemClock.elapsedRealtime();
    }

    public void ShareQuote(View view) {
        Bitmap bitmap = getBitmapFromView(cardView);
        try {
            File file = new File(getApplicationContext().getExternalCacheDir(), File.separator + "abt_simpson.png");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            vibrator.vibrate(100);

            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", file);

            intent.putExtra(Intent.EXTRA_STREAM, photoURI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/png");

            startActivity(Intent.createChooser(intent, "Share image via"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("ResourceAsColor")
    private Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(android.R.color.white);
        }
        view.draw(canvas);

        return returnedBitmap;
    }


    private void callingFunction() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            init();
        } else {
            Toasty.error(getApplicationContext(), R.string.network_check, Toast.LENGTH_SHORT, true).show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageViewChar = findViewById(R.id.image_view_character);
        tvQuote = findViewById(R.id.tv_quote);
        tvCharName = findViewById(R.id.tv_character_name);
        cardView = findViewById(R.id.main_card_view);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        callingFunction();
        statusBar();


    }

    private void init() {

        vibrator.vibrate(75);
        String url = "https://thesimpsonsquoteapi.glitch.me/quotes";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length() > 0) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        getData(jsonArray);
                    } catch (JSONException error) {
                        Toasty.error(getApplicationContext(), "Error" + error.getMessage(), Toast.LENGTH_SHORT, true).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(getApplicationContext(), "Error" + error.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
        queue.add(stringRequest);

    }


    @SuppressLint("SetTextI18n")
    public void getData(JSONArray jsonArray) {
        try {

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String urlImage = jsonObject.getString("image");
                String nameCharacter = jsonObject.getString("character");
                String quote = jsonObject.getString("quote");

                Picasso.get().load(urlImage).into(imageViewChar);
                tvQuote.setText(quote);
                tvCharName.setText("~" + nameCharacter);

            }


        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Error" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void Recall() {
        init();
    }
}