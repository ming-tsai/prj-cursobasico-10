package com.certificate.basic.android.altice.simon;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Animation animation;

    private int index = 0;

    private ArrayList<Integer> buttonArray;

    private ProgressBar progressBarPlayer1;
    private ProgressBar progressBarPlayer2;

    private final HashMap<String, String> hashMap = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCenter.start(getApplication(), "7b64438a-0ed6-4d01-a2a7-37a7abc6b2b1"
                , Analytics.class, Crashes.class);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate

        progressBarPlayer1 = findViewById(R.id.my_progress_bar_1);
        progressBarPlayer2 = findViewById(R.id.my_progress_bar_2);

        if (buttonArray == null) {
            buttonArray = new ArrayList<>();
            progressBarPlayer1.setVisibility(View.VISIBLE);
            progressBarPlayer2.setVisibility(View.GONE);
        }
    }

    /*
     * Method that make object animation, add value check if player is making difference with the
     * sequences
     * @param view the object for make animation and check the value of view
     *
     * */
    public void onClickTextView(@NonNull View view) {
        try {
            doOnClickTextView(view);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void doOnClickTextView(View view) {
        view.startAnimation(animation);

        String color = null;

        switch (view.getId()) {
            case R.id.green_button:
                playSound(R.raw.green);
                color = "Green";
                break;
            case R.id.red_button:
                playSound(R.raw.red);
                color = "Red";
                break;
            case R.id.yellow_button:
                playSound(R.raw.yellow);
                color = "Yellow";
                break;
            case R.id.blue_button:
                playSound(R.raw.blue);
                color = "Blue";
                break;
        }

        if (buttonArray.size() == index) {

            hashMap.put(String.valueOf(index + 1), color);

            index = 0;
            buttonArray.add(view.getId());
            changeStatusOfProgressBar();

        } else {

            if (buttonArray.get(index) != view.getId()) {

                makeAlertMessage(hashMap);
            } else {
                index++;
            }
        }
    }

    private void changeStatusOfProgressBar() {
        if (progressBarPlayer1.getVisibility() == View.VISIBLE) {
            progressBarPlayer1.setVisibility(View.GONE);
            progressBarPlayer2.setVisibility(View.VISIBLE);
        } else {
            progressBarPlayer1.setVisibility(View.VISIBLE);
            progressBarPlayer2.setVisibility(View.GONE);
        }
    }

    private void makeAlertMessage(Map<String, String> hashMap) {

        String message = (progressBarPlayer1.getVisibility() != View.VISIBLE) ?
                "Player 1" : "Player 2";
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        hashMap.put("Total Round", String.valueOf(index + 1));
        hashMap.put("Winner",message);
        Analytics.trackEvent("Simon Game", hashMap);

        builder.setMessage(message + " is Winner")
                .setPositiveButton("Replay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Finish", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                })
                .show();
    }

    private void playSound(int idOfSound) {
        try {
            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), idOfSound);
            if (mediaPlayer != null) {
                mediaPlayer.start();
            }

            assert mediaPlayer != null;
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(@NonNull MediaPlayer mp) {
                    if (mp.isPlaying()) {
                        mp.stop();
                    }

                    mp.reset();
                    mp.release();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
