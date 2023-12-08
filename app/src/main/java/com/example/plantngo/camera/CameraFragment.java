package com.example.plantngo.camera;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.example.plantngo.R;
import com.example.plantngo.authentication.LoginActivity;
import com.example.plantngo.storage.JsonReader;
import com.example.plantngo.storage.SharedPreferencesStorage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

/**
 * Fragment for handling camera functionalities, including taking pictures and uploading them to Firebase.
 */
public class CameraFragment extends Fragment {

    // Constants for permissions and request codes
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;

    private ImageView displayImageView;
    public String addPlantName;
    private String imagesFilesPaths;
    private StorageReference storageReference;
    String userId = LoginActivity.userID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        // Initialize UI elements
        Button cameraButton = view.findViewById(R.id.camera_button);
        Button galleryButton = view.findViewById(R.id.gallery_button);
        displayImageView = view.findViewById(R.id.display_imageView);

        // Initialize Firebase storage reference
        storageReference = FirebaseStorage.getInstance().getReference();

        // Set click listener for camera button
        cameraButton.setOnClickListener(view1 -> {
        try {
            askCameraPermissions();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        });

        // Set click listener for gallery button
        galleryButton.setOnClickListener(view12 -> {
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(gallery, GALLERY_REQUEST_CODE);
        });

        return view;
    }

    /**
     * Requests camera and storage permissions.
     *
     * @throws FileNotFoundException if an error occurs while accessing files
     */
    private void askCameraPermissions() throws FileNotFoundException {
        if ((ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) &&
            ((ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED))) {
            // Request camera and storage permissions
            requestPermissions(new String[] {android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERM_CODE);
        } else {
            // Permission granted, proceed with taking a picture
            takePictureIntent();
        }
    }

    /**
     * Handles the result of requesting permissions.
     *
     * @param requestCode  The request code passed to requestPermissions()
     * @param permissions  The requested permissions
     * @param grantResults The grant results for the corresponding permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERM_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    takePictureIntent();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Toast.makeText(getContext(), "Camera Permission Required to Use Camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Handles the result of activities started for result, such as taking a picture or choosing from the gallery.
     *
     * @param requestCode The request code associated with the started activity
     * @param resultCode  The result code returned by the started activity
     * @param data        The data returned by the started activity
     */
    @Override
    public void onActivityResult (int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == CAMERA_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                File f = new File(imagesFilesPaths);
//                Log.d("tag", "Absolute Url of Image is " + Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                requireContext().sendBroadcast(mediaScanIntent);

                uploadImageToFirebase(f.getName(), contentUri);
                getPlantName();
            }
        }

        if(requestCode == GALLERY_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                Uri contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
                String imageFileName = "IMG_" + timeStamp + "." + getFileExt(contentUri);
//                Log.d("tag", "onActivityResult: Gallery Image Uri: " + imageFileName);

                uploadImageToFirebase(imageFileName, contentUri);
                getPlantName();
            }
        }
    }

    /**
     * Uploads the selected image to Firebase storage.
     *
     * @param name       The name of the image file
     * @param contentUri The URI of the image content
     */
    private void uploadImageToFirebase(String name, Uri contentUri) {
        final StorageReference imageRef = storageReference.child("pictures/" + name);

        imageRef.putFile(contentUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(displayImageView));
                    Toast.makeText(getContext(), "Image is Uploaded", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Upload Failed", Toast.LENGTH_SHORT).show());
    }

    /**
     * Retrieves the file extension of the given content URI.
     *
     * @param contentUri The URI of the content
     * @return The file extension
     */
    private String getFileExt(Uri contentUri) {
        ContentResolver contentResolver = requireContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(contentUri));
    }

    /**
     * Creates a temporary image file to store the captured picture.
     *
     * @return The created image file
     * @throws IOException if an error occurs while creating the file
     */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        imagesFilesPaths = image.getAbsolutePath();
        return image;
    }

    /**
     * Initiates the camera intent to capture a picture.
     *
     * @throws FileNotFoundException if an error occurs while accessing files
     */
    private void takePictureIntent() throws FileNotFoundException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            File photoFile = null;

            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(requireContext(), "com.example.plantngo.provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    /**
     * Obtains the plant name from the JSON file containing plant identification API output.
     */
    public void getPlantName() {
        JsonReader jsonReader = new JsonReader();
        String jsonContent = jsonReader.readJsonFile(getContext(), R.raw.plant_identification_api_output);

        try {
            addPlantName = jsonReader.parsePlantNameJson(jsonContent);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // Save the plant name to the garden
        savePlantToGarden(addPlantName);
    }

    /**
     * Saves the identified plant name to the user's garden using an AlertDialog.
     *
     * @param plantName The identified plant name
     */
    public void savePlantToGarden(String plantName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Plant Name: " + plantName)
                .setCancelable(false)
                .setPositiveButton("Add to Garden", (dialog, which) -> {
                    // Save plant name to shared preferences
                    SharedPreferencesStorage storage = new SharedPreferencesStorage();
                    storage.addPlantNameToSharedPreferences(requireContext(), userId, addPlantName);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
