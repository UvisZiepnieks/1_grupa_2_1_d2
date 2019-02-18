package com.example.kapnes.a1_grupa_2_1_darbs;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.os.Environment;
import android.Manifest;
import android.content.Intent;
import android.provider.MediaStore;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import java.io.IOException;
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link Audio#newInstance} factory method to
 * create an instance of this fragment.
 */

public class Audio extends Fragment {

    private static final int REQUEST_AUDIO_CAPTURE = 1234;
    private static final int RECORD_AUDIO_PERMISSION_REQUEST_CODE = 222;
    Button play, stop, record, recordStop;
    MediaRecorder myAudioRecorder;
    String outputFile;
    ListView listView;
    MediaPlayer myMediaPlayer;
    EditText txtFailName;
    ArrayList<File> list;


    public Audio() {
        // Required empty public constructor
    }


    public static Audio newInstance() {
        Audio fragment = new Audio();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View audio = inflater.inflate(R.layout.fragment_audio, container, false);
        listView = audio.findViewById(R.id.listView);
        record = audio.findViewById(R.id.btnRecordAudio);
        recordStop = audio.findViewById(R.id.btnStopRecording);
        play = audio.findViewById(R.id.btnPlay);
        stop = audio.findViewById(R.id.btnStopPlaying);
        txtFailName = audio.findViewById(R.id.txtAudioNosaukums);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    startRecording();
                    recordStop.setEnabled(true);
                    play.setEnabled(false);
                    stop.setEnabled(false);
                } else {
                    // lets request permission
                    String[] permissinRequest = {Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permissinRequest, RECORD_AUDIO_PERMISSION_REQUEST_CODE);
                }
            }
        });
        recordStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAudioRecorder.stop();
                stop.setEnabled(false);
                record.setEnabled(true);
                play.setEnabled(true);
                stop.setEnabled(false);
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop.setEnabled(true);
                recordStop.setEnabled(false);
                record.setEnabled(false);
                myMediaPlayer = new MediaPlayer();
                try {
                    myMediaPlayer.setDataSource(outputFile);
                    myMediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                myMediaPlayer.start();
                Toast.makeText(getActivity(), getString(R.string.name_playing), Toast.LENGTH_SHORT).show();
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop.setEnabled(false);
                record.setEnabled(true);
                play.setEnabled(true);
                recordStop.setEnabled(false);

                if (myMediaPlayer != null) {
                    myMediaPlayer.stop();
                    myMediaPlayer.release();
                    setupMediaRecorder();
                }
            }
        });
        return audio;
        //return inflater.inflate(R.layout.fragment_audio, container, false);
    }

    class recordAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.list_items, parent, false);
            TextView audio = convertView.findViewById(R.id.textView);
            audio.setText(list.get(position).toString());

            return convertView;
        }
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RECORD_AUDIO_PERMISSION_REQUEST_CODE) {
            // we have heard back from audio recorder
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                startRecording();
            } else {
                Toast.makeText(getActivity(), getString(R.string.name_cant_record), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private ArrayList<File> audioReader(File externalStoragePublicDirectory) {
        ArrayList<File> list = new ArrayList<>();

        File[] files = externalStoragePublicDirectory.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                list.addAll(audioReader(files[i]));
            } else {
                if (files[i].getName().endsWith(".3gp")) {
                    list.add(files[i]);
                }
            }
        }
        return list;
    }

    private void setupMediaRecorder() {
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);
    }
    private void startRecording() {
        //get file path
        //timestamp makes unique name
        SimpleDateFormat sdf = new SimpleDateFormat("yyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date());
        outputFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/"+timestamp+"_audio_record.3gp";
        setupMediaRecorder();
        try{
            myAudioRecorder.prepare();
            myAudioRecorder.start();
        }catch (Exception e){
            e.printStackTrace();
        }
        Toast.makeText(getActivity(),getString(R.string.name_inAction),Toast.LENGTH_SHORT).show();
        txtFailName.setText(outputFile);
        list = audioReader( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC));
        listView.setAdapter( new recordAdapter());

    }
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        audioDeleter(file);
    }
    private void audioDeleter(File file) {
        if (file.exists()) {
            File[] theData = file.listFiles();
            for (int i = 0; i < theData.length; i++) {
                File oneFile = theData[i];
                if (oneFile.isDirectory()) {
                } else {
                    if (oneFile.getName().endsWith(".3gp")) {
                        oneFile.delete();
                    }
                }
            }
        }
    }
}


