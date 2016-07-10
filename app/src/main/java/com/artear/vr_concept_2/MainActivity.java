package com.artear.vr_concept_2;

import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import com.asha.vrlib.MDVRLibrary;

import java.io.IOException;

public class MainActivity extends AppCompatActivity
{
    private static final Uri VIDEO_URI = Uri.parse("http://vodgc.com/p/130/sp/13000/playManifest/entryId/0_amhvhl2u/format/applehttp/protocol/http/a.m3u8");
    private MDVRLibrary mdvrLibrary;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final View mainView = findViewById(R.id.activity_main);
        final MediaPlayer mediaPlayer = new MediaPlayer();

        mdvrLibrary = MDVRLibrary.with(this)
                .displayMode(MDVRLibrary.DISPLAY_MODE_NORMAL)
                .interactiveMode(MDVRLibrary.INTERACTIVE_MODE_MOTION)
                .motionDelay(SensorManager.SENSOR_DELAY_GAME)
                .ifNotSupport(new MDVRLibrary.INotSupportCallback()
                {
                    @Override
                    public void onNotSupport(int mode)
                    {
                        String tip = mode == MDVRLibrary.INTERACTIVE_MODE_MOTION ? "MOTION not supported" : String.valueOf(mode) + " not supported";
                        Snackbar.make(mainView, tip, Snackbar.LENGTH_INDEFINITE).show();
                    }
                })
                .asVideo(new MDVRLibrary.IOnSurfaceReadyCallback()
                {
                    @Override
                    public void onSurfaceReady(final Surface surface)
                    {
                        mediaPlayer.setSurface(surface);

                        try
                        {
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
                            {
                                @Override
                                public void onPrepared(final MediaPlayer mp)
                                {
                                    mp.start();
                                }
                            });

                            mediaPlayer.setDataSource(MainActivity.this, VIDEO_URI);
                            mediaPlayer.prepareAsync();
                        } catch (IOException e)
                        {
                            Snackbar.make(mainView, R.string.error_video, Snackbar.LENGTH_INDEFINITE).show();
                        }
                    }
                })
                .build(R.id.surface_view);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return mdvrLibrary.handleTouchEvent(event) || super.onTouchEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.switch_mode:
                mdvrLibrary.switchInteractiveMode(this);
                return true;

            case R.id.switch_display_mode:
                mdvrLibrary.switchDisplayMode(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mdvrLibrary.onResume(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mdvrLibrary.onPause(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mdvrLibrary.onDestroy();
    }
}
