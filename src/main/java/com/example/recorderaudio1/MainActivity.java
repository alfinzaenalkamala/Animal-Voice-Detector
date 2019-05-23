package com.example.recorderaudio1;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;

import android.net.nsd.NsdManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;

public class MainActivity extends AppCompatActivity  {

    Button buttonStart, buttonStop, buttonPlayLastRecordAudio,
            buttonStopPlayingRecording ;
    String AudioSavePathInDevice = null;
    FloatingActionButton fabRec, fabStop;

    private TextView txtRecord, readAmplitudo;
    MediaRecorder mediaRecorder= null; ;
    Random random ;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer ;
    public static final int REPEAT_INTERVAL = 40;
    File audioDirTemp;
    VisualizerView visualizerView;
    private boolean isRecording = false;

    public static final String DIRECTORY_NAME_TEMP = "AudioTemp";

    private Handler handler;

    private boolean rekam = false;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonStart = (Button) findViewById(R.id.button);
        buttonStop = (Button) findViewById(R.id.button2);
        buttonPlayLastRecordAudio = (Button) findViewById(R.id.button3);
        buttonStopPlayingRecording = (Button)findViewById(R.id.button4);



        buttonStop.setEnabled(false);
        buttonPlayLastRecordAudio.setEnabled(false);
        buttonStopPlayingRecording.setEnabled(false);

        readAmplitudo = (TextView) findViewById(R.id.readAlpmitudo);

        random = new Random();

        visualizerView = (VisualizerView) findViewById(R.id.visualizer);








        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rekam = true;
                String nama;

                if(!isRecording) {

                    AudioSavePathInDevice =
                            Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                    CreateRandomAudioFileName(5) + "AudioRecording.3gp";

                    MediaRecorderReady();

                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                        isRecording = true;
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    buttonStart.setEnabled(false);
                    buttonStop.setEnabled(true);

                    Toast.makeText(MainActivity.this, "Recording started",
                            Toast.LENGTH_LONG).show();
                } else {
                    requestPermission();


                    releaseRecorder();
                }
                handler.post(updateVisualizer);

            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaRecorder.stop();
                buttonStop.setEnabled(false);
                buttonPlayLastRecordAudio.setEnabled(true);
                buttonStart.setEnabled(true);
                buttonStopPlayingRecording.setEnabled(false);

                Toast.makeText(MainActivity.this, "Recording Completed",
                        Toast.LENGTH_LONG).show();
            }
        });

        buttonPlayLastRecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws IllegalArgumentException,
                    SecurityException, IllegalStateException {

                buttonStop.setEnabled(false);
                buttonStart.setEnabled(false);
                buttonStopPlayingRecording.setEnabled(true);

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(AudioSavePathInDevice);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();
                Toast.makeText(MainActivity.this, "Recording Playing",
                        Toast.LENGTH_LONG).show();


            }
        });

        buttonStopPlayingRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonStop.setEnabled(false);
                buttonStart.setEnabled(true);
                buttonStopPlayingRecording.setEnabled(false);
                buttonPlayLastRecordAudio.setEnabled(true);

                if(mediaPlayer != null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    MediaRecorderReady();
                }
            }
        });

        audioDirTemp = new File(Environment.getExternalStorageDirectory(),
                DIRECTORY_NAME_TEMP);
        if (audioDirTemp.exists()) {
            deleteFilesInDir(audioDirTemp);
        } else {
            audioDirTemp.mkdirs();
        }

        // create the Handler for visualizer update
        handler = new Handler();

    }


    public void MediaRecorderReady(){
        mediaRecorder= new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
        mediaRecorder.setOutputFile(audioDirTemp + "/audio_file" + ".mp3");





        MediaRecorder.OnErrorListener errorListener = null;
        mediaRecorder.setOnErrorListener(errorListener);
        MediaRecorder.OnInfoListener infoListener = null;
        mediaRecorder.setOnInfoListener(infoListener);
    }

    public String CreateRandomAudioFileName(int string){
        StringBuilder stringBuilder = new StringBuilder( string );
        int i = 0 ;
        while(i < string ) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));

            i++ ;
        }
        return stringBuilder.toString();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(MainActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void releaseRecorder() {
        if (mediaRecorder != null) {
            isRecording = false; // stop recording
            handler.removeCallbacks(updateVisualizer);
            visualizerView.clear();
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    public static boolean deleteFilesInDir(File path) {

        if( path.exists() ) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for(int i=0; i<files.length; i++) {

                if(files[i].isDirectory()) {

                }
                else {
                    files[i].delete();
                }
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseRecorder();
    }

    Runnable updateVisualizer = new Runnable() {
        @Override
        public void run() {
            if (isRecording) // if we are already recording
            {
                // get the current amplitude
                int x = mediaRecorder.getMaxAmplitude();

               if (x >= 2000){
                   readAmplitudo.setText(Integer.toString( x));
                   visualizerView.addAmplitude(x); // update the VisualizeView
                   visualizerView.invalidate(); // refresh the VisualizerView


                   if (x <= 4000){
                       Toast.makeText(MainActivity.this, "Bukan KUCING", Toast.LENGTH_SHORT).show();
                   }
                   else {
                       Toast.makeText(MainActivity.this, "Suara KUCING", Toast.LENGTH_SHORT).show();
                   }
               }
//                    if (x >= 2000){
//                        Toast.makeText(MainActivity.this, "Suara KUCING", Toast.LENGTH_SHORT).show();
//
////                        if (x <= 5000) {
////                            Toast.makeText(MainActivity.this, "KUCING", Toast.LENGTH_SHORT).show();
////
////                        }
//                    }

//                    else {
//                        Toast.makeText(MainActivity.this, "Suara KUCING", Toast.LENGTH_SHORT).show();
//
//                    }
//                double db = 0;// ??
//                    if (x > 1)
//                        db = 30 * Math.log10(x);
//                readAmplitudo.setText((int) (db));

                // update in 40 milliseconds
                handler.postDelayed(this, REPEAT_INTERVAL);


            }
        }
    };



}
