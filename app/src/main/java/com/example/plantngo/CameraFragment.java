package com.example.plantngo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.CAMERA;

public class CameraFragment extends Fragment {

    private final int CAMERA_PERMISSION_CODE = 300;
    private final int RESULT_CAMERA_LOAD_IMG = 1890;
    private final int RESULT_GALLERY_LOAD_IMG = 1890;

    private FrameLayout frameLayout;
    private Activity activity;

    private List<String> imagesFilesPaths = new ArrayList<>();

    private ImageView photoImageView;
    private Button galleryButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        photoImageView = view.findViewById(R.id.photoImageView);
        galleryButton = view.findViewById(R.id.galleryButton);

        galleryButton.setOnClickListener(v -> openGallery());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        frameLayout = view.findViewById(R.id.blankFragment);
        takePhotoFromCamera();
    }

    private void takePhotoFromCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(CAMERA)) {
                // Show an explanation to the user
                // You can use a dialog, Snackbar, or any other UI element to explain the need for the permission.
            } else {
                // Request the permission
                requestCameraPermissionLauncher.launch(CAMERA);
            }
        } else {
            openCameraIntent();
        }
    }

    // Use the new ActivityResultLauncher for requesting permissions
    private final ActivityResultLauncher<String> requestCameraPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    openCameraIntent();
                } else {
                    // Permission not granted, handle accordingly (e.g., show a message)
                    // You may want to show a Snackbar or a dialog explaining the need for the permission.
                }
            }
    );

    private final ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // The picture was taken successfully
                    String tempImageFilePath = imagesFilesPaths.get(imagesFilesPaths.size() - 1);
                    Uri tempImageURI = Uri.fromFile(new File(tempImageFilePath));
                    resizeThanLoadImage(tempImageFilePath, tempImageURI);

                    // Show the captured photo in the ImageView
                    photoImageView.setVisibility(View.VISIBLE);
                    photoImageView.setImageURI(tempImageURI);
                } else {
                    // Handle the case where the picture was not taken
                }
            }
    );

    private void openCameraIntent() {
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(requireContext(), "com.example.plantngo.provider", photoFile);

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

            takePictureLauncher.launch(takePictureIntent);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        imagesFilesPaths.add(image.getAbsolutePath());
        return image;
    }

    private void resizeThanLoadImage(String tempImageFilePath, Uri tempImageURI) {
        // Implement the logic to resize and load the image
    }

    private final ActivityResultLauncher<Intent> pickFromGalleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri selectedImageUri = data.getData();
                        // Handle the selected image URI, e.g., load it into an ImageView
                        displaySelectedImage(selectedImageUri);
                    }
                }
            }
    );

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        pickFromGalleryLauncher.launch(galleryIntent);
    }

    private void displaySelectedImage(Uri imageUri) {
        // Update your UI to display the selected image, e.g., set it to an ImageView
        photoImageView.setVisibility(View.VISIBLE);
        photoImageView.setImageURI(imageUri);
    }

}
