package com.theartofdev.edmodo.cropper.test;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import com.example.test.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class MainActivity extends AppCompatActivity {
  private static int id_for_image = 0;
  private static DatabaseHandler databaseHandler;
  private ImageView imageView;
  private AlertDialog.Builder dialogBuilder;
  private AlertDialog dialog;
  private EditText TAG_NAME;
  private Button TAG_BUTTON;
  private FloatingActionButton CANCEL_FAB;
  private Spinner dropDownMenu;
  private SpinnerAdapter spinnerAdapterChoices;
  private static RecyclerView recyclerView;
  private RecyclerView.Adapter mAdapter;
  private RecyclerView.LayoutManager layoutManager;


  @RequiresApi(api = Build.VERSION_CODES.P)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    databaseHandler = new DatabaseHandler(this);
    createRecyclerView();
    //new LoadImageFromDatabaseTask().execute(0);
  }
  public static DatabaseHandler getDatabaseHandler(){
    return databaseHandler;
  }



  @RequiresApi(api = Build.VERSION_CODES.P)
  private void createRecyclerView() {
    recyclerView = findViewById(R.id.my_recycler_view);
    layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);
    mAdapter = new RecyclerViewAdapter(databaseHandler.getDataList(), this);
    recyclerView.setAdapter(mAdapter);
  }

  public void onSelectImageClick(View view) {
    CropImage.activity(null).setGuidelines(CropImageView.Guidelines.ON).start(this);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    // handle result of CropImageActivity
    if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
      CropImage.ActivityResult result = CropImage.getActivityResult(data);
      if (resultCode == RESULT_OK) {
        //Save it to Database and request for the tag
        createPopupDialog(result);
        Toast.makeText(
                this, "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_LONG)
                .show();
      } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
        Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
      }
   }
  }

  private void createPopupDialog(final CropImage.ActivityResult result){
    dialogBuilder = new AlertDialog.Builder(this);
    View view = getLayoutInflater().inflate(R.layout.pop_up, null);
    TAG_NAME = view.findViewById(R.id.tag_editText);
    TAG_BUTTON = view.findViewById(R.id.tag_button);
    CANCEL_FAB = view.findViewById(R.id.floatingActionButton2);
    dialogBuilder.setView(view);
    dialog = dialogBuilder.create();
    dialog.show();
    TAG_BUTTON.setOnClickListener(new View.OnClickListener() {
      @RequiresApi(api = Build.VERSION_CODES.P)
      @Override
      public void onClick(View view) {
        //TODO: SAVE IMAGE TO DATABASE
        saveImageToDB(result, TAG_NAME.getText().toString());
        //TODO: CHANGE THE SCREEN
        dialog.hide();
        Toast.makeText(MainActivity.super.getBaseContext(), "Image Saved Successfully!!!", Toast.LENGTH_SHORT).show();
      }
    });
    CANCEL_FAB.setOnClickListener(new View.OnClickListener() {
      @RequiresApi(api = Build.VERSION_CODES.P)
      @Override
      public void onClick(View view) {
        dialog.hide();
        createRecyclerView();
        for(int i=10; i<16; i++){
          databaseHandler.deleteRows(Integer.toString(i));
        }
      }
    });
  }

  @RequiresApi(api = Build.VERSION_CODES.P)
  public void saveImageToDB(CropImage.ActivityResult result, String tag){
    ImageView i = findViewById(R.id.quick_start_cropped_image);
    i.setImageURI(result.getUri());
    Drawable d = i.getDrawable();
    id_for_image=databaseHandler.getMaximumID()+1;
    databaseHandler.insetImage(d, id_for_image, tag);
    createRecyclerView();
  }
  private class LoadImageFromDatabaseTask extends AsyncTask<Integer, Integer, ImageHelper> {

    private final ProgressDialog LoadImageProgressDialog =  new ProgressDialog(MainActivity.this);

    protected void onPreExecute() {
      this.LoadImageProgressDialog.setMessage("Loading Image from Db...");
      this.LoadImageProgressDialog.show();
    }

    @Override
    protected ImageHelper doInBackground(Integer... integers) {
      Log.d("LoadImageFrom", "");
      return databaseHandler.getImage(Constants.KEY_ID);
    }

    protected void onProgressUpdate(Integer... progress) {
    }

    protected void onPostExecute(ImageHelper imageHelper) {
      if (this.LoadImageProgressDialog.isShowing()) {
        this.LoadImageProgressDialog.dismiss();
      }
      setUpImage(imageHelper.getImageByteArray());
    }

  }


  private void setUpImage(byte[] bytes) {
    Log.d(Constants.KEY_TAG, "Decoding bytes");
    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    imageView.setImageBitmap(bitmap);
  }
}