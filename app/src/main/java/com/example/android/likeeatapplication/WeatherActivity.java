package com.example.android.likeeatapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.likeeatapplication.Config.Constant;
import com.example.android.likeeatapplication.Model.WeatherResult;
import com.example.android.likeeatapplication.Retrofit.IOpenWeatherMap;
import com.example.android.likeeatapplication.Retrofit.RetrofitClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class WeatherActivity extends AppCompatActivity {

    private RelativeLayout relativeLayout;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private static final String TAG = "WeatherActivity";
    private boolean mLocationPermissionsGranted = true;

    ImageView img_cuaca;
    TextView txt_kota, txt_suhu, txt_desc, txt_date_time;
    LinearLayout weather_panel;
    ProgressBar loading;
    Location currentLocation;

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        relativeLayout = (RelativeLayout) findViewById(R.id.root_view);

        compositeDisposable = new CompositeDisposable();
        Retrofit  retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);

        img_cuaca = (ImageView)findViewById(R.id.img_cuaca);
        txt_kota = (TextView)findViewById(R.id.txt_kota);
        txt_suhu = (TextView)findViewById(R.id.txt_suhu);
        txt_desc = (TextView)findViewById(R.id.txt_desc);
        txt_date_time = (TextView)findViewById(R.id.txt_date_time);

        weather_panel = (LinearLayout)findViewById(R.id.weather_panel);
        loading = (ProgressBar)findViewById(R.id.loading);

        

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            mLocationPermissionsGranted = true;
                            buildLocationRequest();
                            //buildLocationCallBack();
                            if (ActivityCompat.checkSelfPermission(WeatherActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(WeatherActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(WeatherActivity.this);
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
                                                getWeatherInformation();
                                            } else {
                                                Log.d(TAG, "onComplete: current location is null");
                                                Toast.makeText(WeatherActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
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

    private void getWeatherInformation() {
        compositeDisposable.add(mService.getWeatherByLatLng(String.valueOf(currentLocation.getLatitude()),
                String.valueOf(currentLocation.getLongitude()),
                Constant.App_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResult>() {
                    @Override
                    public void accept(WeatherResult weatherResult) throws Exception {
                        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                                .append(weatherResult.getWeather().get(0).getIcon())
                                .append(".png").toString()).into(img_cuaca);

                        txt_kota.setText(weatherResult.getName());
                        txt_desc.setText(new StringBuilder("Weather in ").append(weatherResult.getName()).toString());
                        txt_suhu.setText(new StringBuilder((String.valueOf(weatherResult.getMain().getTemp()))).append("°C").toString());
                        txt_date_time.setText(convertUnixToDate(weatherResult.getDt()));

                        weather_panel.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE) ;
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(WeatherActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private String convertUnixToDate(long dt) {
        Date date = new Date(dt*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm EEE MM yyyy");
        String formatted = sdf.format(date);
        return formatted;
    }

//    private void buildLocationCallback() {
//        locationCallback = new LocationCallback(){
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                super.onLocationResult(locationResult);
//
//                Constant.current_location = locationResult.getLastLocation();
//
//                Log.d("Location: ", locationResult.getLastLocation().getLatitude()+"/"+locationResult.getLastLocation().getLongitude());
//            }
//        };
//    }


    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10.0f);
    }
}
