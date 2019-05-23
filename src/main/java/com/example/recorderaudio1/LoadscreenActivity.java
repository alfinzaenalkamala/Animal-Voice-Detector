package com.example.recorderaudio1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoadscreenActivity extends AppCompatActivity {

    TextView textView;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_loadscreen );

        getWindow ().setFlags ( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        textView = findViewById ( R.id.text_view );
        progressBar = findViewById ( R.id.progress_bar );

        progressBar.setMax ( 100 );
        progressBar.setScaleY ( 3f );

        progresAnimation ();


    }

   public void progresAnimation(){
        ProgressbarAnimation anim = new ProgressbarAnimation ( this, progressBar, textView,0f,100f );
        anim.setDuration ( 9000 );
        progressBar.setAnimation ( anim );
    }
}
