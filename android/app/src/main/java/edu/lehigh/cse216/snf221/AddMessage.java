package edu.lehigh.cse216.snf221;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddMessage extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    Integer REQUEST_CAMERA=1, SELECT_FILE=0;
    ImageView ivImage;
    String encodedImage;
    String imageName;
    String imageType;
    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Creating activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addmessage_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle("Add Message");

        //Title and message text fields along with image box and upload button
        final EditText title = (EditText) findViewById(R.id.title);
        final EditText message = (EditText) findViewById(R.id.messageBody);
        final Button uploadBtn = (Button) findViewById(R.id.uploadButton);
        ivImage = (ImageView)findViewById(R.id.image);

        //getting the name of the image
        imageName = getResources().getResourceName(R.id.image);

        imageType = "JPEG";

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                SelectImage();
            }
        });

        //Dropdown for locations
        final Spinner dropdown = findViewById(R.id.spinner1);
        String[] locations = new String[]{"","Rathbone Hall", "Lower/Upper Court", "Common Grounds Cafe", "FML The Grind Cafe", "Williams Global Cafe", "Lucy's Cafe"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, locations);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);

        //Submit button checks if both title and message fields have been filled
        //before actually submitting data to the database
        Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String mTitle = title.getText().toString();
                final String mMessage = message.getText().toString();
                final String mFileString = encodedImage;
                final String mFileName = imageName;
                final String fileContent = imageType;
                final String mLink = "https://www.google.com";
                final String mFileLink = "egahkglwEBFIWEG";
                final String mLocation = location;



                //If either title or message is missing, then tell user that they are missing
                if (mTitle.equals("") || mMessage.equals("") || mLocation.equals("")) {
                    Toast.makeText(AddMessage.this, "Title, location, or message is missing", Toast.LENGTH_LONG).show();

                } else {
                       RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        String url = "https://cse216project.herokuapp.com/messages";
                        //String url = "http://cse216-phase1.herokuapp.com/messages/" + UserID.userID;

                        //Setting up HashMap to create a JSON to send POST request
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("mUserID", UserID.userID);
                        params.put("mTitle", mTitle);
                        params.put("mMessage", mMessage);
                        params.put("mLikes", "0");
                        params.put("mDislikes", "0");
                        params.put("mLink", mLink);
                        params.put("mFileLink", mFileLink);
                        params.put("fileName", mFileName);
                        params.put("fileContent", fileContent);
                        params.put("file",mFileString);
                        params.put("mLocation", mLocation);



                    if(encodedImage != null && imageName != null) {
                            Log.d("encoded image current", encodedImage);
                            Log.d("filename", imageName);
                            Log.d("fileContent", fileContent);
                        }



                        //params.put("mUserID", UserID.userID);

                        //Setting up POST request
                        JsonObjectRequest newMessageRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        //Log.d("anh222", "SUCESS I HAVE POSTED A MESSAGE");
                                        Log.d("anh222", mTitle);
                                        Log.d("anh222", mMessage);
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                VolleyLog.e("Error: ", error.getMessage());
                                Log.d("anh222", "HELPPPP ERROR WITH POSTING MESSAGE!");
                                Log.d("anh222", mTitle);
                                Log.d("anh222", mMessage);
                                 error.printStackTrace();
                            }
                        });

                        // Add the request to the RequestQueue.
                    queue.add(newMessageRequest);
                    //VolleySingleton.getInstance(AddMessage.this).addToRequestQueue(newMessageRequest);

                  // alternative way to post using the StringRequest instead of JsonObject request
                 /* try{
                      RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                      //RequestQueue requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
                        String url = "https://cse216project.herokuapp.com/messages";

                        JSONObject jsonBody = new JSONObject();
                        jsonBody.put("userId", UserID.userID);
                        jsonBody.put("mTitle", "mTitle");
                        jsonBody.put("mMessage", "mMessage");
                        jsonBody.put("mLikes", "mLikes");
                        jsonBody.put("mDislikes", "mDislikes");
                        jsonBody.put("mLink", "mLink");
                        jsonBody.put("mFileLink", "mFileLink");
                        jsonBody.put("fileName", mFileName);
                        jsonBody.put("fileContent",fileContent);
                        jsonBody.put("file", mFileString);



                        final String requestBody = jsonBody.toString();

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.i("VOLLEY good, sucess", response);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("VOLLEY error, failure", error.toString());
                                error.printStackTrace();

                            }
                        });
                        queue.add(stringRequest);
                        //VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

                        if(encodedImage != null) {
                            Log.d("encoded image current", encodedImage);
                        }
                    }catch(JSONException e){
                        e.printStackTrace();
                    } */


                        //Return back to main activity
                        Intent intent = new Intent();
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                }
            }
        });

        //Cancel button
        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //If user cancels, return back to main activity
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }
    //Setting actions for selection on location spinner
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        location = (parent.getItemAtPosition(pos)).toString();
    }
    public void onNothingSelected(AdapterView<?> parent) {
        location = "";
    }
    //This method enables us to select either camera, gallery or cancel once the upload image button is clicked.
    private void SelectImage(){
        final CharSequence[] items={"Camera","Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(AddMessage.this);
        builder.setTitle("Add Image");

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(items[which].equals("Camera")){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                }else if (items[which].equals("Gallery")){
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent, SELECT_FILE);
                }else if(items[which].equals("Cancel")){
                    dialog.dismiss();
//                    setResult(Activity.RESULT_CANCELED);
//                    finish();
                }

            }
        });
        builder.show();

    }

    @Override
    public  void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Bundle bundle = data.getExtras();
                Bitmap bmp = (Bitmap) bundle.get("data");

                //converting an ivImage to a bytestream
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                byte[] bb = bos.toByteArray();

                encodedImage = Base64.encodeToString(bb, Base64.DEFAULT);

                ivImage.setImageBitmap(bmp);
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();

                InputStream imageStream = null;

                try{
                    imageStream = getContentResolver().openInputStream(selectedImageUri);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }

                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                encodedImage = encodeThisImage(selectedImage);
                ivImage.setImageURI(selectedImageUri);
            }
        }
    }

    //a method to get the bytestream for the image given a bitmap
    private String encodeThisImage(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        int id = item.getItemId();
//
//        if (id == R.id.action_uploadImage) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
