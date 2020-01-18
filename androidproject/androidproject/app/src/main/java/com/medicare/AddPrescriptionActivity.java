package com.medicare;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medicare.model.Cart;
import com.medicare.utils.Constants;
import com.medicare.utils.Preferences;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class AddPrescriptionActivity extends AppCompatActivity {

    private static final String TAG = "AddPrescriptionActivity";
    LinearLayout btn_camera, btn_gallery, all_pre;
    ImageView select_image;
    private List<String> listPermissionsNeeded1 = new ArrayList<>();
    private List<String> listPermissionsNeeded = new ArrayList<>();

    private static final int CAMERA_REQUEST = 1888;
    private static final int SELECT_PICTURE = 1;

    private String selectedImagePath;

    private final int STORAGE_PERMISSION_CODE = 0x23;
    private final int CAMERA_PERMISSION_CODE = 0x22;

    FirebaseStorage storage;
    Bitmap photo;
    public  static  Activity mActivity;
    ImageView iv_back;
    Button il_continue;
    ProgressDialog progressDialog;

    ArrayList<String> saveedURI = new ArrayList<>();

    boolean isFromAvaiable = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_prescription);

        mActivity = AddPrescriptionActivity.this;
        storage = FirebaseStorage.getInstance("gs://medicare-45f85.appspot.com");

        btn_camera = findViewById(R.id.btn_camera);
        btn_gallery = findViewById(R.id.btn_gallery);
        all_pre = findViewById(R.id.btn_show);

        select_image = findViewById(R.id.select_image);

        il_continue = findViewById(R.id.il_continue);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        String serializedObject = Preferences.getStringPref(mActivity,Preferences.save_uri);
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            saveedURI = gson.fromJson(serializedObject, type);
        }

        if(saveedURI == null){
            saveedURI = new ArrayList<>();
        }

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAndRequestCameraPermissions(CAMERA_PERMISSION_CODE)) {

                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);

                }
            }
        });

        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAndRequestPermissions(STORAGE_PERMISSION_CODE)) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,
                            "Select Picture"), SELECT_PICTURE);
                }
            }
        });

        all_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddPrescriptionActivity.this,SavePrescriptionActivity.class));
            }
        });

        il_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: " );
                if(photo != null){
                    progressDialog.setMessage("Uploading image...");
                    if(isFromAvaiable){

                        startActivity(new Intent(AddPrescriptionActivity.this,AddressActivity.class).putExtra("image_uri",Constants.select_image_url).putExtra("mode","advance"));

                    }else {
                        showDialog();
                        uploadImage(photo);
                    }
                }else{
                    Toast.makeText(mActivity, "Please select image", Toast.LENGTH_SHORT).show();
                }
            }
        });
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        isFromAvaiable = false;
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap OutImage = (Bitmap) data.getExtras().get("data");
            photo = Bitmap.createScaledBitmap(OutImage, OutImage.getWidth() * 10, OutImage.getHeight() * 10, true);
            select_image.setImageBitmap(photo);

        } else {
            if (resultCode == RESULT_OK) {
                if (requestCode == SELECT_PICTURE) {
                    Uri selectedImageUri = data.getData();
                    //selectedImagePath = getPath(selectedImageUri);

                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    photo = bitmap;
                    select_image.setImageBitmap(photo);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }


    private boolean checkAndRequestCameraPermissions(int code) {

        if (ContextCompat.checkSelfPermission(AddPrescriptionActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddPrescriptionActivity.this, new String[]{android.Manifest.permission.CAMERA}, code);
            return false;
        } else {
            return true;
        }
    }

    private boolean checkAndRequestPermissions(int code) {

        if (ContextCompat.checkSelfPermission(AddPrescriptionActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(AddPrescriptionActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddPrescriptionActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    code);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (permissions.length == 0) {
            return;
        }
        boolean allPermissionsGranted = true;
        if (grantResults.length > 0) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
        }
        if (!allPermissionsGranted) {
            boolean somePermissionsForeverDenied = false;
            for (String permission : permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    //denied
                    Log.e("denied", permission);

                    switch (requestCode) {
                        case CAMERA_PERMISSION_CODE:
                            ActivityCompat.requestPermissions(AddPrescriptionActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                            break;
                        case STORAGE_PERMISSION_CODE:
                            ActivityCompat.requestPermissions(AddPrescriptionActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                            break;
                        default:
                            break;
                    }

                } else {
                    if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                        //allowed
                        Log.e("allowed", permission);
                    } else {
                        //set to never ask again
                        Log.e("set to never ask again", permission);
                        somePermissionsForeverDenied = true;
                    }
                }
            }
            if (somePermissionsForeverDenied) {

                String title = "";
                switch (requestCode) {
                    case CAMERA_PERMISSION_CODE:
                        title = "camera";
                        break;
                    case STORAGE_PERMISSION_CODE:
                        title = "storage";
                        break;
                    default:
                        break;
                }

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Permissions Required")
                        .setMessage("Please allow permission for " + title + ".")
                        .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", getPackageName(), null));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            }
        } else {
            switch (requestCode) {
                case CAMERA_PERMISSION_CODE:
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    break;

                case STORAGE_PERMISSION_CODE:
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,
                            "Select Picture"), SELECT_PICTURE);
                    break;
                default:
                    break;
            }
        }
    }


    public void uploadImage(Bitmap bitmap) {
        StorageReference storageRef = storage.getReference();

        final StorageReference ref = storageRef.child("a1"+ System.currentTimeMillis() + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = ref.putBytes(data);


        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                Log.e("vimal", "then: " + ref.getDownloadUrl());
                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();

                    Log.e("vimal", "onComplete: " + downloadUri);

                    saveedURI.add(downloadUri.toString());

                    Gson gson = new Gson();

                    String s = gson.toJson(saveedURI);

                    Preferences.setStringPref(mActivity,Preferences.save_uri,s);

                    hideDialog();

                    startActivity(new Intent(AddPrescriptionActivity.this,AddressActivity.class).putExtra("image_uri",downloadUri.toString()).putExtra("mode","advance"));

                } else {
                    Toast.makeText(mActivity, "Fail to load", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public String getPath(Uri uri) {
        // just some safety built in
        if (uri == null) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        // this is our fallback here
        return uri.getPath();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Constants.selected_image){
            Constants.selected_image  = false;

            Picasso.with(mActivity).load(Uri.parse(Constants.select_image_url)).into(select_image, new Callback() {
                @Override
                public void onSuccess() {
                    photo = ((BitmapDrawable)select_image.getDrawable()).getBitmap();
                    isFromAvaiable = true;
                }

                @Override
                public void onError() {

                }
            });

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
