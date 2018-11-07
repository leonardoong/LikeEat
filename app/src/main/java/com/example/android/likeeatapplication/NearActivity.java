package com.example.android.likeeatapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.android.likeeatapplication.Adapter.RestaurantAdapter;
import com.example.android.likeeatapplication.Model.Restaurant;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NearActivity extends AppCompatActivity {

    private RelativeLayout relativeLayout;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private RecyclerView mRestaurantRecyclerView;
    private RestaurantAdapter mAdapter;
    private ArrayList<Restaurant> mRestaurantCollection;
    private LocationRequest locationRequest;
    private static final String TAG = "WeatherActivity";
    private boolean mLocationPermissionsGranted = true;

    Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near);

        init();

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            mLocationPermissionsGranted = true;
                            buildLocationRequest();
                            //buildLocationCallBack();
                            if (ActivityCompat.checkSelfPermission(NearActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(NearActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(NearActivity.this);
                            //fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                            try {
                                if (mLocationPermissionsGranted) {
                                    final Task location = fusedLocationProviderClient.getLastLocation();
                                    location.addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "onComplete: found current location");
                                                currentLocation = (Location) task.getResult();
                                                Log.d("Location: ", currentLocation.getLatitude()+"/"+currentLocation.getLongitude());
                                                new FetchDataTask().execute();
                                            } else {
                                                Log.d(TAG, "onComplete: current location is null");
                                                Toast.makeText(NearActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            } catch (SecurityException e) {
                                Log.e(TAG, "getDeviceLocation: Security Exception " + e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        Snackbar.make(relativeLayout,  "Permission Denied", Snackbar.LENGTH_SHORT);
                    }
                }).check();

        
    }

    private void init() {
        mRestaurantRecyclerView = (RecyclerView) findViewById(R.id.restaurant_rv);
        mRestaurantRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRestaurantRecyclerView.setHasFixedSize(true);
        mRestaurantCollection = new ArrayList<>();
        mAdapter = new RestaurantAdapter(mRestaurantCollection, this);
        mRestaurantRecyclerView.setAdapter(mAdapter);
    }

    public class FetchDataTask extends AsyncTask<Void, Void, Void> {
        private String mZomatoString;

        @Override
        protected Void doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            StringBuilder zomato_api = new StringBuilder("https://developers.zomato.com/api/v2.1/search?count=10&lat=")
                    .append(currentLocation.getLatitude()).append("&lon=").append(currentLocation.getLongitude());
            Uri builtUri = Uri.parse(zomato_api.toString());
            URL url;
            try {
                url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("user-key", "641240974e3dd378ebdcd3dd147cb9fe");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    //Nothing to do
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                mZomatoString = buffer.toString();
                JSONObject jsonObject = new JSONObject(mZomatoString);

                Log.v("Response", jsonObject.toString());

                JSONArray restaurantsArray = jsonObject.getJSONArray("restaurants");

                //list = new ArrayList<>();
                for (int i = 0; i < restaurantsArray.length(); i++) {

                    Log.v("BRAD_", i + "");
                    String name;
                    String address;
                    String currency;
                    String imageUrl;
                    long lon;
                    long lat;
                    long cost;
                    float rating;


                    JSONObject jRestaurant = (JSONObject) restaurantsArray.get(i);
                    jRestaurant = jRestaurant.getJSONObject("restaurant");
                    JSONObject jLocattion = jRestaurant.getJSONObject("location");
                    JSONObject jRating = jRestaurant.getJSONObject("user_rating");


                    name = jRestaurant.getString("name");
                    address = jLocattion.getString("address");
                    lat = jLocattion.getLong("latitude");
                    lon = jLocattion.getLong("longitude");
                    currency = jRestaurant.getString("currency");
                    cost = jRestaurant.getInt("average_cost_for_two");
                    imageUrl = jRestaurant.getString("thumb");
                    rating = (float) jRating.getDouble("aggregate_rating");


                    Restaurant restaurant = new Restaurant();
                    restaurant.setName(name);
                    restaurant.setAddress(address);
                    restaurant.setLatitiude(lat);
                    restaurant.setLongitude(lon);
                    restaurant.setImageUrl(imageUrl);
                    restaurant.setRating(String.valueOf(rating));

                    mRestaurantCollection.add(restaurant);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("MainActivity", "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10.0f);
    }
}
