package com.example.kapnes.a1_grupa_2_1_darbs;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link Camera#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Camera extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1888;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1337;
    ImageView imageView;
    GridView gridView;
    ArrayList<File> arraylist;
    String  PhotoPath;



    public Camera() {
        // Required empty public constructor
    }


    public static Camera newInstance() {
        Camera fragment = new Camera();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View camera = inflater.inflate(R.layout.fragment_camera, container, false);
        Button takePhoto = camera.findViewById(R.id.btnTakePhoto);
        imageView = (ImageView) camera.findViewById(R.id.imageView);
        gridView = camera.findViewById(R.id.GridView);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    // lets request permission
                    String[] permissinRequest = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permissinRequest, CAMERA_PERMISSION_REQUEST_CODE);
                }
            }
        });
        return camera;
    }

    //searching for files in external memory, if found save them inside arraylist
    private ArrayList<File> imageReader(File externalStoragePublicDirectory) {
        ArrayList<File> list = new ArrayList<>();

        File[] files = externalStoragePublicDirectory.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                list.addAll(imageReader(files[i]));
            } else {
                if (files[i].getName().endsWith(".jpg")) {
                    list.add(files[i]);
                }
            }
        }
        return list;
    }

    class GridAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return arraylist.size();
        }

        @Override
        public Object getItem(int position) {
            return arraylist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
           view = getLayoutInflater().inflate(R.layout.fragment_layout, parent, false);
            ImageView image = view.findViewById(R.id.imageView2);
            image.setImageURI(Uri.parse(getItem(position).toString()));

            return view;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            // we have heard back from camera
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {

            }
        }
    }

    private void openCamera() {
        //get file uri
        Uri pictureUri = FileProvider.getUriForFile(getActivity().getApplicationContext(),
                getActivity().getApplicationContext().getPackageName() + ".provider", createImageFile());

        Intent Storeintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //tell camera where to save image
        Storeintent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);

        //tell the camera to request right permission

        Storeintent.setFlags(Storeintent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(Storeintent, REQUEST_IMAGE_CAPTURE);
    }

    private File createImageFile() {
        //public picture directory
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        //timestamp makes unique name
        SimpleDateFormat sdf = new SimpleDateFormat("yyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date());

        //put together the directory and the timestamp
        File imageFile = new File(pictureDirectory, "picture" + timestamp + ".jpg");
        PhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE: {
                    if (resultCode == Activity.RESULT_OK) {
                        File file = new File( PhotoPath);
                        Bitmap bitmap = MediaStore.Images.Media
                                .getBitmap(getContext().getContentResolver(), Uri.fromFile(file));
                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap);
                        }
                    }
                    arraylist = imageReader(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
                    gridView.setAdapter(new GridAdapter());
                    break;
                }
            }

        } catch (Exception e) {
        }
    }
    }

