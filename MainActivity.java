

package com.baby.sleep.musicx;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.baby.sleep.musicx.ads.AdmobHelper;
import com.baby.sleep.musicx.ads.FanHelper;
import com.baby.sleep.musicx.ads.GdprHelper;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private FrameLayout adContainerView;
    private String ads_name;
    private Button bt_start, bt_privacy, bt_share, bt_rate;
    private TextView tx_info;
    private AdmobHelper admobHelper;
    private FanHelper fanHelper;

    public static boolean isNetworkConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
            } else {
                @SuppressWarnings("deprecation")
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                //noinspection deprecation
                return activeNetwork != null && activeNetwork.isConnected();
            }
        }

        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();
        adContainerView = findViewById(R.id.ads_container);
        bt_start = findViewById(R.id.bt_start);
        bt_privacy = findViewById(R.id.bt_privacy);
        bt_share = findViewById(R.id.bt_share);
        bt_rate = findViewById(R.id.bt_rate);
        tx_info = findViewById(R.id.tx_info);
        String info = getString(R.string.app_name) + "\n" + getString(R.string.app_info);
        tx_info.setText(info);
        ads_name = getString(R.string.ads_name);
        if (isNetworkConnected(this)) {
            showBanner();
        } else {
            bt_start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, ListActivity.class));
                }
            });
        }
        bt_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PrivacyActivity.class));
            }
        });
        bt_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareApp();
            }
        });
        bt_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateApps();
            }
        });
    }

    private void showBanner() {
        switch (ads_name) {
            case "admob":
                admobHelper = new AdmobHelper();
                GdprHelper gdprHelper = new GdprHelper();
                gdprHelper.InitGdpr(this, new MCallback() {
                    @Override
                    public void onAction() {
                        admobHelper.InitAdmob(MainActivity.this, adContainerView);
                    }
                });
                bt_start.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        admobHelper.getInterestialAdmob(MainActivity.this, new MCallback() {
                            @Override
                            public void onAction() {
                                startActivity(new Intent(MainActivity.this, ListActivity.class));
                            }
                        });
                    }
                });
                break;
            case "fan":
                fanHelper = new FanHelper();
                fanHelper.showBannerFan(MainActivity.this, adContainerView);
                bt_start.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fanHelper.showInterstitialFan(MainActivity.this, new MCallback() {
                            @Override
                            public void onAction() {
                                startActivity(new Intent(MainActivity.this, ListActivity.class));
                            }
                        });
                    }
                });
                break;
            default:
        }
    }

    public void shareApp() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + getPackageName());
        startActivity(Intent.createChooser(share, "Share This App"));
    }

    public void rateApps() {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Rate This App!");
        alert.setMessage("If you enjoy playing this app, would you mind taking a moment to rate it? It won\'t take more than a minute. Thanks for your support!");
        alert.setPositiveButton("Rate", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent rate = new Intent(Intent.ACTION_VIEW);
                rate.setData(Uri.parse("market://details?id=" + getPackageName()));
                startActivity(rate);
            }
        });

        alert.setNegativeButton("Later", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        Dialog mDialog = alert.create();
        mDialog.show();
    }
}