package edu.lehigh.cse216.snf221;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Messages extends AppCompatActivity {


    ImageView ivImage;
    Integer REQUEST_CAMERA=1, SELECT_FILE=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Create instance of activity
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_message);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        //Change title of toolbar as Messages
        this.setTitle("Messages");

        //Get intent sent from MainActivity and receive data
        Intent input = getIntent(); //contains contents of 'mData'
        String mTitle = input.getStringExtra("mTitle");
        String mMessage = input.getStringExtra("mMessage");
        String mUsername = input.getStringExtra("mUsername");
        String mLocation = input.getStringExtra("mLocation");
        double[] mCoords = input.getDoubleArrayExtra("mCoords");
        int mId = input.getIntExtra("mId", 0);

        //making the map url
        String coords = mCoords[0]+"" + ',' + mCoords[1];
        final String mapURL = "https://www.google.com/maps/@?api=1&map_action=map&center=" + coords + "&zoom=18";
        //Use data to print the title and message.

        //Button for opening maps
        Button mapButton = (Button) findViewById(R.id.location_button);
        mapButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent viewIntent = new Intent("android.intent.action.VIEW",
                        Uri.parse(mapURL));
                startActivity(viewIntent);
            }
        });
        //mId could also be used to add a like button on this
        //activity instead of the main activity if desired
        final TextView titleBody = (TextView) findViewById(R.id.titleBody);
        final TextView messageBody = (TextView) findViewById(R.id.messageBody);
        final TextView username = (TextView) findViewById(R.id.userBody);
        final TextView locationBody = (TextView) findViewById(R.id.locationBody);
        ivImage = (ImageView)findViewById(R.id.image);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SelectImage();
            }
        });
        //final TextView userNameBody = (TextView) findViewById(R.id.UserBody);
        //final TextView commentBody = (TextView) findViewById(R.id.datum_list_view);
        //Set text fields with title and the message
        titleBody.setText(mTitle);
        messageBody.setText(mMessage);
        username.setText(mUsername);
        locationBody.setText(mLocation);
    }

    private void SelectImage(){
        final CharSequence[] items={"Camera","Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(Messages.this);
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
                    setResult(Activity.RESULT_CANCELED);
                    finish();
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
                final Bitmap bmp = (Bitmap) bundle.get("data");
                ivImage.setImageBitmap(bmp);
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                ivImage.setImageURI(selectedImageUri);
            }
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//       getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

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
