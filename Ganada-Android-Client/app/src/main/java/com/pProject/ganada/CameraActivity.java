package com.pProject.ganada;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity implements CaptionView {

    private String objectType;
    private PreviewView previewView;
    private ImageButton captureBtn;
    private ImageCapture imageCapture;
    private ProgressDialog progressDialog;
    private File outputFile;
    private CaptionService captionService;
    private ExampleParsingService exampleParsingService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Intent intent = getIntent();
        objectType = intent.getStringExtra("objectType");

        captionService = new CaptionService();
        captionService.setCaptionView(this);

        exampleParsingService = new ExampleParsingService();
        exampleParsingService.setCaptionView(this);

        previewView = (PreviewView) findViewById(R.id.viewFinder);

        outputFile = getOutputDirectory();
        startCamera();  //????????? ??????

        captureBtn = (ImageButton) findViewById(R.id.camera_capture_btn);
        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();    //?????? ?????? ?????? ??????
            }
        });
    }

    //????????? ?????? ??????
    public void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderListenableFuture = ProcessCameraProvider.getInstance(this);
        imageCapture = new ImageCapture.Builder().build();

        cameraProviderListenableFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderListenableFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture);
    }

    //?????? ?????? ??????
    private void takePhoto() {
        //?????? ????????? ????????? ?????? ??????
        File photoFile = new File(
                outputFile,
                new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.KOREA).format(System.currentTimeMillis()) + ".jpg"
        );

        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(ImageCapture.OutputFileResults outputFileResults) {    //?????? ?????? ??????
                        onCaptionLoading();

                        if (objectType.equals("text")) {    //????????? ??????
                            Uri savedUri = Uri.fromFile(photoFile); //file -> Uri ??????
                            extractText(savedUri);
                        } else {
                            captionService.getPictureCaption(photoFile);    //?????? ??????
                        }
                    }

                    @Override
                    public void onError(ImageCaptureException error) {  //?????? ?????? ??????
                        Log.e("CameraActivity", error.toString());
                    }
                }
        );
    }

    //?????? ????????? ???????????? ?????? or ????????????
    private File getOutputDirectory() {
        File mediaDir = this.getFilesDir();

        if (mediaDir != null && mediaDir.exists()) {
            return mediaDir;
        } else {
            return getFilesDir();
        }

    }

    //ML Kit ??? ????????? ????????? ?????? ?????? ???????????? ????????? ???????????? ??????
    public void extractText(Uri uri) {
        Log.d("CameraActivity", "extractText");

        try {
            InputImage image = InputImage.fromFilePath(getApplicationContext(), uri);

            TextRecognizer recognizer =
                    TextRecognition.getClient(new KoreanTextRecognizerOptions.Builder().build());

            recognizer.process(image)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text visionText) {
                            Caption caption = new Caption();
                            caption.setKind(visionText.getText());
                            exampleParsingService.getExample(uri, caption);
                        }
                    })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                }
                            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCaptionLoading() {
        progressDialog = new ProgressDialog(this);
        //???????????? ????????????
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.show();
    }

    @Override
    public void onCaptionSuccess(Uri uri, Caption caption) {
        Intent intent = new Intent(this, LearnWordActivity.class);
        intent.putExtra("type", objectType);  //?????????????????? ????????????????????? ????????? ?????? ??????
        intent.putExtra("uri", uri.toString()); //intent??? ?????? uri ??????
        if (caption.getKind().equals("-1"))
            intent.putExtra("recognizedText", "");
        else
            intent.putExtra("recognizedText", caption.getKind());
        intent.putExtra("exam", caption.getMessage());
        startActivity(intent);  //????????? ??????

        progressDialog.dismiss();
    }
}