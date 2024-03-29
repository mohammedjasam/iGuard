package com.sadaf.iguardindia.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sadaf.iguardindia.App;
import com.sadaf.iguardindia.ClarifaiUtil;
import com.sadaf.iguardindia.R;
import com.sadaf.iguardindia.adapter.RecognizeConceptsAdapter;
import com.google.firebase.database.FirebaseDatabase;
import com.sadaf.iguardindia.activity.Viz;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiImage;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.ConceptModel;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public final class RecognizeConceptsActivity extends BaseActivity {

  public static final int PICK_IMAGE = 100;
  private static final int TAKE_PHOTO = 200;

  // The URI of photo taken with camera
  private Uri mUriPhotoTaken;

  // the list of results that were returned from the API
  @BindView(R.id.resultsList)
  RecyclerView resultsList;

  // the view where the image the user selected is displayed
  @BindView(R.id.image)
  ImageView imageView;

  // switches between the text prompting the user to hit the FAB, and the loading spinner
  @BindView(R.id.switcher)
  ViewSwitcher switcher;

  // the FAB that the user clicks to select an image
  @BindView(R.id.fab)
  View fab;

  // Firebase parameters
  private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
  private DatabaseReference mRootReference = firebaseDatabase.getReference();
  private String Username;
//  private DatabaseReference mUserName = mRootReference.child(Username);
  private DatabaseReference mUserName;
//  DatabaseReference mCurrImage = mUserName.child("CurrentImage");
  DatabaseReference mCurrImage;


    public void setmCurrImage(DatabaseReference mCurrImage) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(!prefs.getBoolean("firstTime", false)) {
            // run your one time code
            this.mCurrImage.setValue("0");
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }

    }


    @NonNull private final RecognizeConceptsAdapter adapter = new RecognizeConceptsAdapter();

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Button photoButton = (Button) this.findViewById(R.id.button1);

//    Bundle loginData = getIntent().getExtras();
    Username = getIntent().getExtras().get("name").toString();
    mUserName = mRootReference.child(Username);
    mCurrImage = mUserName.child("CurrentImage");
    Toast.makeText(getApplicationContext(), Username, Toast.LENGTH_LONG).show();

    photoButton.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, TAKE_PHOTO);
      }
    });

    setmCurrImage(mCurrImage);
  }


  int CurrentImageNumber;


  @Override
  protected void onStart() {
    super.onStart();

    resultsList.setLayoutManager(new LinearLayoutManager(this));
    resultsList.setAdapter(adapter);

    mUserName.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {

        CurrentImageNumber = Integer.parseInt(dataSnapshot.child("CurrentImage").getValue(String.class));
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });

  }

  @OnClick(R.id.fab)
  void pickImage() {
    startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), PICK_IMAGE);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode != RESULT_OK) {
      return;
    }
    switch(requestCode) {
      case PICK_IMAGE:
        final byte[] imageBytes = ClarifaiUtil.retrieveSelectedImage(this, data);
        if (imageBytes != null) {
          onImagePicked(imageBytes);
        }
        break;
      case TAKE_PHOTO:
        Bitmap photo = (Bitmap) data.getExtras().get("data");
        imageView.setImageBitmap(photo);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        onImagePicked(byteArray);
        break;
    }
  }

  // Save the activity state when it's going to stop.
  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable("ImageUri", mUriPhotoTaken);
  }

  // Recover the saved state when the activity is recreated.
  @Override
  protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    mUriPhotoTaken = savedInstanceState.getParcelable("ImageUri");
  }

  // When the button of "Take a Photo with Camera" is pressed.
  public void takePhoto(View view) {
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    if(intent.resolveActivity(getPackageManager()) != null) {
      // Save the photo taken to a temporary file.
      File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
      try {
        File file = File.createTempFile("IMG_", ".jpg", storageDir);
        mUriPhotoTaken = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUriPhotoTaken);
        startActivityForResult(intent, TAKE_PHOTO);
      } catch (IOException e) {
      }
    }
  }
  private void onImagePicked(@NonNull final byte[] imageBytes) {
    // Now we will upload our image to the Clarifai API
    setBusy(true);

    // Make sure we don't show a list of old concepts while the image is being uploaded
    adapter.setData(Collections.<Concept>emptyList());

    new AsyncTask<Void, Void, ClarifaiResponse<List<ClarifaiOutput<Concept>>>>() {
      @Override
      protected ClarifaiResponse<List<ClarifaiOutput<Concept>>> doInBackground(Void... params) {
        // The default Clarifai model that identifies concepts in images
        final ConceptModel generalModel = App.get().clarifaiClient().getDefaultModels().generalModel();

        // Use this model to predict, with the image that the user just selected as the input
        return App.get().clarifaiClient().getModelByID("EmotionPredictor").executeSync().get().asConceptModel().predict()
                .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(imageBytes)))
                .executeSync();
      }

      @Override
      protected void onPostExecute(ClarifaiResponse<List<ClarifaiOutput<Concept>>> response) {
        setBusy(false);
        if (!response.isSuccessful()) {
          showErrorSnackbar(R.string.error_while_contacting_api);
          return;
        }
        final List<ClarifaiOutput<Concept>> predictions = response.get();
        if (predictions.isEmpty()) {
          showErrorSnackbar(R.string.no_results_from_api);
          return;
        }
        adapter.setData(predictions.get(0).data());
        //Store results from here to the Realtime Database
        String newImageNumber = String.valueOf(CurrentImageNumber + 1);

        mCurrImage.setValue(newImageNumber);

        HashMap<String, Float> Result = new HashMap<String, Float>();

        for (int i = 0; i < 6; i++)
            Result.put(predictions.get(0).data().get(i).name().toString(), predictions.get(0).data().get(i).value());

        mUserName.child(newImageNumber).child("Anger").setValue(Result.get("Anger"));
        mUserName.child(newImageNumber).child("Disgust").setValue(Result.get("Disgust"));
        mUserName.child(newImageNumber).child("Fear").setValue(Result.get("Fear"));
        mUserName.child(newImageNumber).child("Happiness").setValue(Result.get("Happiness"));
        mUserName.child(newImageNumber).child("Sadness").setValue(Result.get("Sadness"));
        mUserName.child(newImageNumber).child("Surprise").setValue(Result.get("Surprise"));








        imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
      }

      private void showErrorSnackbar(@StringRes int errorString) {
        Snackbar.make(
            root,
            errorString,
            Snackbar.LENGTH_INDEFINITE
        ).show();
      }
    }.execute();
  }


  @Override
  protected int layoutRes() { return R.layout.activity_recognize; }

  private void setBusy(final boolean busy) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        switcher.setDisplayedChild(busy ? 1 : 0);
        imageView.setVisibility(busy ? GONE : VISIBLE);
        fab.setEnabled(!busy);
      }
    });
  }

}
