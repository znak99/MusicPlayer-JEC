package com.jec.ac.jp.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private int pausedTime = 0;
    private boolean isPlaying = false;
    private MediaPlayer mediaPlayer;
    private ImageButton playButton, sdCardButton, stopButton;
    private TextView currentSelectedMedia;
    private FrameLayout visualizerFrameLayout;

    private ImageView visuallizerImage1, visuallizerImage2, visuallizerImage3;
    private ObjectAnimator visualizerAnimator1, visualizerAnimator2, visualizerAnimator3;
    
    private static final int EXTERNAL_STORAGE = 1;

    public static int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (Build.VERSION.SDK_INT >= 23) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
//                    ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this,
//                        new String[]{
//                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                                Manifest.permission.READ_EXTERNAL_STORAGE
//                        }, EXTERNAL_STORAGE);
//            }
//        }


        visualizerFrameLayout = findViewById(R.id.xmlVisualizerFrame);

        currentSelectedMedia = findViewById(R.id.xmlPlayingMusicInfo);
        currentSelectedMedia.setText("SDカードから音楽を選択してください");

        sdCardButton = findViewById(R.id.xmlShowSDCardButton);
        sdCardButton.setOnClickListener(new ButtonActions());

        playButton = findViewById(R.id.xmlPlayButton);
        playButton.setOnClickListener(new ButtonActions());

        stopButton = findViewById(R.id.xmlStopButton);
        stopButton.setOnClickListener(new ButtonActions());

        visuallizerImage1 = createImageView(250, 250);
        visuallizerImage2 = createImageView(300, 300);
        visuallizerImage3 = createImageView(350, 350);

        visualizerAnimator1 = visualizerAnim(visuallizerImage1, 0);
        visualizerAnimator2 = visualizerAnim(visuallizerImage2, 500);
        visualizerAnimator3 = visualizerAnim(visuallizerImage3, 1000);
    }

    private ImageView createImageView(int width, int height) {
        ImageView view = new ImageView(MainActivity.this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                width,height, Gravity.CENTER);

        view.setBackground(getDrawable(R.drawable.visualizer_circle));
        view.setAlpha(0f);
        view.setLayoutParams(params);

        visualizerFrameLayout.addView(view, 0);

        return view;
    }

    private ObjectAnimator visualizerAnim(ImageView view, long delay) {

        ObjectAnimator visualizerAnimation = ObjectAnimator.ofPropertyValuesHolder(
                view,
                PropertyValuesHolder.ofFloat("alpha", 0f, 1f, 0f)
        );
        visualizerAnimation.setDuration(1500);
        visualizerAnimation.setRepeatCount(-1);
        visualizerAnimation.setStartDelay(delay);
        visualizerAnimation.setRepeatMode(ValueAnimator.RESTART);

        return visualizerAnimation;
    }

    private void pauseAnim() {
        if (visualizerAnimator1 == null || visualizerAnimator2 == null || visualizerAnimator3 == null)
            return;
        visualizerAnimator1.cancel();
        visualizerAnimator2.cancel();
        visualizerAnimator3.cancel();
    }

    private void stopAnim() {
        visualizerAnimator1.end();
        visualizerAnimator2.end();
        visualizerAnimator3.end();
    }

    private void startAnim() {
        if (visualizerAnimator1.isStarted() || visualizerAnimator2.isStarted() || visualizerAnimator3.isStarted())
            return;
        visualizerAnimator1.start();
        visualizerAnimator2.start();
        visualizerAnimator3.start();
    }

    private void showAlert(String title, String msg, String btnTxt, DialogInterface.OnClickListener action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(btnTxt, action);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length <= 0) {
            return;
        }
        switch (requestCode) {
            case EXTERNAL_STORAGE: {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    showAlert("終了", "アプリを起動できません", "確認", (dialogInterface, i) -> {
                        finish();
                    });
                }
            }
        }
        return ;
    }

    final class ButtonActions implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int viewId = view.getId();
            if (viewId == R.id.xmlShowSDCardButton) {
                // SDCard
                if (mediaPlayer != null) {
                    mediaPlayer.pause();
                    pausedTime = mediaPlayer.getCurrentPosition();
                    isPlaying = false;
                    pauseAnim();
                }
                Intent intent = new Intent(MainActivity.this, SDListActivity.class);
                startActivity(intent);
            } else if (viewId == R.id.xmlPlayButton) {
                // Play
                if (mediaPlayer == null) {
                    showAlert("", "音楽を選択してください", "確認", null);
                    return;
                }
            } else if (viewId == R.id.xmlStopButton) {
                // Stop
                if (mediaPlayer == null || !isPlaying) {
                    return;
                }
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                pausedTime = 0;
                isPlaying = false;
            }
        }
    }
}