package edu.lehigh.cse216.snf221;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MessageListAdapter.OnAddMessageListener {

    /**
     * mData holds the data we get from Volley
     */
    ArrayList<Datum> mData = new ArrayList<>();

    /**
     * RecyclerView and adapter to display messages
     */
    RecyclerView rv;
    MessageListAdapter adapter;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getting the google account
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Setting title of activty
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);


        this.setTitle("Welcome "+ acct.getDisplayName());

        //Set RecyclerView that is going to be used
        rv = (RecyclerView) findViewById(R.id.datum_list_view);

        // Instantiate the RequestQueue from Volley Singleton
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        String url = "https://cse216project.herokuapp.com/messages ";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        populateListFromVolley(response);
//                        Log.d("anh222", "Response is: " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("anh222", "That didn't work!");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    private void populateListFromVolley(String response){
        Log.d("anh222", "populatingListFromVolley");
        try {
            //Get JSON data from database
            JSONObject json= new JSONObject(response);
            JSONArray jsonArray = new JSONArray(json.getString("mData"));
            Log.d("anh222", json.toString());
            Log.d("anh222", jsonArray.toString());

            //Store data in to local datastore mData
            for (int i = 0; i < jsonArray.length(); i++) {
                int id = jsonArray.getJSONObject(i).getInt("mId");
                String userId = jsonArray.getJSONObject(i).getString("mUserId");
                String userName=jsonArray.getJSONObject(i).getString("mUsername");
                String subject = jsonArray.getJSONObject(i).getString("mSubject");
                String message = jsonArray.getJSONObject(i).getString("mMessage");
                int likes = jsonArray.getJSONObject(i).getInt("mLikes");
                int dislikes = jsonArray.getJSONObject(i).getInt("mDislikes");
                String imageURL = jsonArray.getJSONObject(i).getString("mImageURL");    //changed from mImgURL while debugging
                String link = jsonArray.getJSONObject(i).getString("mLink");
                //String fileID = jsonArray.getJSONObject(i).getString("mFileId");
                //String myFileString = jsonArray.getJSONObject(i).getString("mFileString");
                //String myFileName = jsonArray.getJSONObject(i).getString("mFileName");
                //String fileLink = jsonArray.getJSONObject(i).getString("mFileLink");
                String location = jsonArray.getJSONObject(i).getString("mLocation");
                JSONArray Jcoords = jsonArray.getJSONObject(i).getJSONArray("mCoords");
                double[] coords = {Jcoords.getDouble(0), Jcoords.getDouble(1)};

                //Insert data into local datastore
                mData.add(new Datum(id, userId, userName, subject, message,likes,dislikes, imageURL, link, null, null, null, null, location, coords));
               // Log.d("anh222", id + " " + subject + " " + message + " " + likes + " " + dislikes+ " "+ userId+ " "+userName + " "+myFileString + " "+myFileName);
            }

        } catch (final JSONException e) {
            Log.d("anh222", "Error parsing JSON file: " + e.getMessage());
            return;
        }
        Log.d("anh222", "Successfully parsed JSON file.");
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageListAdapter(this, mData, this);
        rv.setAdapter(adapter);

        //This clickListener is for the like button
        adapter.setClickListener(new MessageListAdapter.ClickListener() {
            /**
             * onClick for likes
             */
            @Override
            public void onLike(Datum d) {
                //Update database to like message
                RequestQueue queue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();

                String url = "https://cse216project.herokuapp.com/messages/" + d.mId + "/" + d.mUserId + "/likes";

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                        new Response.Listener<String>() {
                            public void onResponse(String response) {
                                Log.d("anh222", "Response is: " + response);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("abc", "Like didn't work!");
                    }
                });

                // Add the request to the RequestQueue.
                queue.add(stringRequest);

                //Need to let timeout thread so that previous request finishes first before updating the view
                threadSleep();

                //When like/dislike button is clicked, the mData gets updated with updated data
                //from database and then refreshes the RecyclerView to display updated data
                getUpdatedDataAndRefreshView();
            }

            /**
             * onClick for dislikes
             */
            @Override
            public void onDislike(Datum d) {
                //Update database to like message
                RequestQueue queue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();

                //NEED URL
                String url = "https://cse216project.herokuapp.com/messages/" + d.mId + "/" + d.mUserId + "/dislikes";

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                        new Response.Listener<String>() {
                            public void onResponse(String response) {
                                Log.d("anh222", "Response is: " + response);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("abc2", "Dislike didn't work!");
                    }
                });

                // Add the request to the RequestQueue.
                queue.add(stringRequest);

                //Need to let timeout thread so that previous request finishes first before updating the view
                threadSleep();

                //When like/dislike button is clicked, the mData gets updated with updated data
                //from database and then refreshes the RecyclerView to display updated data
                getUpdatedDataAndRefreshView();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Adding message feature in options menu
        if (id == R.id.action_addMessage) {
            Intent i = new Intent(getApplicationContext(), AddMessage.class);

            //Start new activity for adding message
            startActivityForResult(i, 789); // 789 is the number that will come back to us

            //Update view with new data
            getUpdatedDataAndRefreshView();
            return true;
        }
        else if(id ==R.id.action_signOut){
            signOut();
       }
//        else if (id == R.id.action_uploadImage){
//            Intent i = new Intent(getApplicationContext(), UploadImage.class);
//
//            //Start new activity for adding message
//            startActivityForResult(i, 1); // 789 is the number that will come back to us
//
//            //Update view with new data
//            getUpdatedDataAndRefreshView();
//            return true;
//
//        }

        return super.onOptionsItemSelected(item);
    }
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this, "Signed out successfully",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                        startActivity(intent);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == 789) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // Get the "extra" string of data
                Log.d("anh222", "onActivityResult: Message added");

                //Wait for POST request when adding
                //message to finish before
                //updating the view
                threadSleep();

                //Update view with updated database when returning to main
                //activity where all messages are shown
                getUpdatedDataAndRefreshView();
            } else if (resultCode == RESULT_CANCELED) {
                Log.d("anh222", "onActivityResult: Message canceled");

                //Wait for POST request when canceling
                //message to finish before
                //updating the view
                threadSleep();

                //Update view with updated database when returning to main
                //activity where all messages are shown
                getUpdatedDataAndRefreshView();
            }
        } else if (requestCode == 1){
            if(resultCode == RESULT_OK){
                Log.d("anh222", "onActivityResult: image uploaded");
                //threadSleep();
                //getUpdatedDataAndRefreshView();
            }
        }
    }

    /**
     * onClick that changes activity when each message is clicked
     * New activity should display the message
     * Each ViewHolder (each message) maintains OnMessageListener
     * The param is used by RecyclerView when going through all the data
     * from the mData local datastore
     *
     * Look at MessageListAdapter to see how this works with the
     * adapter and RecyclerView
     * @param position The position of each ViewHolder (Each message)
     */
    @Override
    public void onAddMessageClick(int position) {
        //Need to pass on data for each message to each ViewHolder
        Intent intent = new Intent(getApplicationContext(), Messages.class);
        intent.putExtra("mId", mData.get(position).mId);
        intent.putExtra("mTitle", mData.get(position).mTitle);
        intent.putExtra("mMessage", mData.get(position).mMessage);
        intent.putExtra("mUsername",mData.get(position).mUsername);
        intent.putExtra("mLocation", mData.get(position).mLocation);//support location display
        intent.putExtra("mCoords", mData.get(position).mCoords);
        startActivity(intent);
    }

    /**
     * Method for updating data in RecyclerView for messages whenever like/dislike occurs
     * Or new message is added
     */
    public void getUpdatedDataAndRefreshView() {
        Log.d("anh222", "GET UPDATED DATA");
        final List<Datum> newData = new ArrayList<>();
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();

        String url = "https://cse216project.herokuapp.com/messages";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        try {
                            //Get JSON data from database
                            JSONObject json= new JSONObject(response);

                            //Make request synchronous
                            //Need to let thread timeout so that previous
                            //request finishes first before updating the view
                            threadSleep();

                            JSONArray jsonArray = new JSONArray(json.getString("mData"));
                            Log.d("anh222", json.toString());
                            Log.d("anh222", jsonArray.toString());

                            for (int i = 0; i < jsonArray.length(); i++) {
                                int id = jsonArray.getJSONObject(i).getInt("mId");
                                String userId = jsonArray.getJSONObject(i).getString("mUserId");
                                String userName=jsonArray.getJSONObject(i).getString("mUsername");
                                String subject = jsonArray.getJSONObject(i).getString("mSubject");
                                String message = jsonArray.getJSONObject(i).getString("mMessage");
                                int likes = jsonArray.getJSONObject(i).getInt("mLikes");
                                int dislikes = jsonArray.getJSONObject(i).getInt("mDislikes");
                                String imageURL = jsonArray.getJSONObject(i).getString("mImageURL");    //changed to image
                                String link = jsonArray.getJSONObject(i).getString("mLink");
                                //String fileID = jsonArray.getJSONObject(i).getString("mFileId");
                                //String myFileString = jsonArray.getJSONObject(i).getString("mFileString");
                                //String myFileName = jsonArray.getJSONObject(i).getString("mFileName");
                                //String fileLink = jsonArray.getJSONObject(i).getString("mFileLink");
                                String location = jsonArray.getJSONObject(i).getString("mLocation");
                                JSONArray Jcoords = jsonArray.getJSONObject(i).getJSONArray("mCoords");
                                double[] coords = {Jcoords.getDouble(0), Jcoords.getDouble(1)};

                                //Insert data into local datastore
                                newData.add(new Datum(id, userId, userName, subject, message,likes,dislikes, imageURL, link, null, null, null, null, location, coords));


                            }

                            //Updating data in mData instance variable
                            //because the adapter gets data from mData
                            mData.clear();
                            mData.addAll(newData);
                            adapter.notifyDataSetChanged();
                        } catch (final JSONException e) {
                            Log.d("anh222", "Error parsing JSON file: " + e.getMessage());
                            return;
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                threadSleep();
                Log.e("anh222", "getUpdatedDataAndRefreshView didn't work!");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    /**
     * Used for delaying execution of program so that request for
     * new data can finish first before updating the view
     * Way network requests work with Volley is that when
     * waiting for requests to finsih, it will continue execution of
     * the rest of the program.
     */
    private void threadSleep() {
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Log.d("anh222", "Thread interrupted");
        }
    }
}
