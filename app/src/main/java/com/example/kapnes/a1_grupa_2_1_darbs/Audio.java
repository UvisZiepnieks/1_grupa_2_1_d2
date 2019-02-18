package com.example.kapnes.a1_grupa_2_1_darbs;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.os.Environment;
import android.Manifest;
import android.content.Intent;
import android.provider.MediaStore;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import android.widget.EditText;
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
    private Button play, stop, record;
    private MediaRecorder myAudioRecorder;
    private String outputFile;




    public Audio() {
        // Required empty public constructor
    }


    public static Audio newInstance() {
        Audio fragment = new Audio();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_audio, container, false);
    }


    }


