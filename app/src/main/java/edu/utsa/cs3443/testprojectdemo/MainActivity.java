package edu.utsa.cs3443.testprojectdemo;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer; // MediaPlayer for background music
    private ImageView animatedBackground1, animatedBackground2; // Images for animated background effect
    private Handler handler = new Handler(); // Handler for background animation
    private float backgroundX = 0; // Track background position for scrolling
    private boolean isNavigatingToSecondScreen = false; // Track if navigating to another screen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Set padding to adjust for system bars (top, bottom, etc.)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize MediaPlayer and start playing background music
        initMediaPlayer();

        // Set up two background images for a seamless scrolling effect
        animatedBackground1 = findViewById(R.id.animated_background_1);
        animatedBackground2 = findViewById(R.id.animated_background_2);

        // Start animating the background images
        animateBackgrounds();

        // Setup button to transition to the second screen (SecondActivity)
        Button buttonNext = findViewById(R.id.button_next);
        buttonNext.setOnClickListener(view -> {
            isNavigatingToSecondScreen = false; // Indicate screen transition
            stopMusic(); // Stop music playback on screen transition
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            startActivity(intent);
        });
    }
    // Initialize MediaPlayer with audio file and loop
    private void initMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.milky_theme);
            mediaPlayer.setLooping(true); // Enable looping for continuous play
            mediaPlayer.setVolume(0.1f, 0.1f); // Volume (low for background effect)
        }
        mediaPlayer.start(); // Start music playback
    }
    // Pause and reset the music playback when stopping it (e.g., on screen switch)
    private void stopMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause(); // Pause playback
            mediaPlayer.seekTo(0); // Reset to start position
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause(); // Pause music when app is paused
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Resume music if not transitioning to another screen
        if (!isNavigatingToSecondScreen) {
            if (mediaPlayer == null) {
                initMediaPlayer(); // Re-initialize if MediaPlayer was released
            } else if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start(); // Resume playback
            }
        }
        isNavigatingToSecondScreen = false; // Reset navigation flag
    }
    // Method to animate the background images, creating a looping scroll effect
    private void animateBackgrounds() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                backgroundX -= 1; // Background movement speed
                animatedBackground1.setTranslationX(backgroundX); // Set position of first image
                animatedBackground2.setTranslationX(backgroundX + animatedBackground1.getWidth()); // Position second image after first

                // Reset background position to loop effect
                if (Math.abs(backgroundX) >= animatedBackground1.getWidth()) {
                    backgroundX = 0;
                }
                handler.postDelayed(this, 10); // Continue animation with delay (Maintains loop movement)
            }
        }, 10);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Release MediaPlayer resources
            mediaPlayer = null;
        }
        handler.removeCallbacksAndMessages(null); // Clear all pending animations
    }
}
