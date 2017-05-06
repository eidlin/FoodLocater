package com.ronyonatan.foodlocater;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.orm.SugarContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class MainFragA extends Fragment implements LocationListener, View.OnCreateContextMenuListener {
    RecyclerView placesRV;
    CheckBox nearCB;
    Boolean near = true;
    EditText foodET;
    String keyword = " ";
    ImageButton goBTN;
    double lat;
    double lon;
    LocationManager locationManager;
    MyPlaceAdapter adapter;
    MyPlace favoplace;
    // Boolean getall = true;
    Intent intent1;
    int currentposition;
    // Flag for GPS status
    boolean isGPSEnabled = false;

    // Flag for network status
    boolean isNetworkEnabled = false;
    // Flag for GPS status
    boolean canGetLocation = false;
    Location temp = null;
    Location location; // Location
    double latitude; // Latitude
    double longitude; // Longitude
    MyPlace place=null;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    public MainFragA() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        placesRV = (RecyclerView) view.findViewById(R.id.placesRV);
        foodET = (EditText) view.findViewById(R.id.foodET);
        nearCB = (CheckBox) view.findViewById(R.id.nearCB);
        SearchNearMe(nearCB);


        goBTN = (ImageButton) view.findViewById(R.id.goBTN);
        SugarContext.init(getActivity());
        //   Gson gson= new Gson();
        // List<GitHubUser> allusers = gson.fromJson(json, new TypeToken<List<GitHubUser>>(){}.getType());

        //  MyPlace adapter1= new MyUsersAdapter(allusers, MainActivity.this, mainLayout);
/*
        ArrayList<MyPlace> allplaces = (ArrayList<MyPlace>) .listAll();
        MyPlaceAdapter adapter=new MyPlaceAdapter( allplaces,getContext());
        placesRV.setAdapter(adapter);
*/


        CheckHasBeenFinished checkHasBeenFinished = new CheckHasBeenFinished();
        IntentFilter filter = new IntentFilter("com.example.ronyonatan.localbroadcast.FINISHED!");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(checkHasBeenFinished, filter);

        List<MyPlace> allplaces = MyPlace.listAll(MyPlace.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            adapter = new MyPlaceAdapter(allplaces, getContext());
        }

        placesRV.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            intent1 = new Intent(getContext(), MyIntentServiceSearch.class);
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);

        placesRV.setLayoutManager(gridLayoutManager);


        //connect to the location service and get the location manager object
        locationManager = (LocationManager) getActivity().getSystemService(Service.LOCATION_SERVICE);

        //check if has permission
        int permissionCheck = 0;

        permissionCheck = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);


        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            //requestLocationUpdates(the provider used to get location - gps/network , refresh time milliseconds ,minimum refresh distance,
            //location listener)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, this);


        } else {
            //request permission 12 is the request number

            requestPermissions( new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 12);


        }


        startWorkingWithLocation();


        goBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                    intent1 = new Intent(getContext(), MyIntentServiceSearch.class);
                keyword = foodET.getText().toString();
                //getall = false;
                // intent1.putExtra("all",getall);
                intent1.putExtra("keyWord", keyword);
                intent1.putExtra("lat", lat);
                intent1.putExtra("lon", lon);
                getActivity().startService(intent1);


            }
        });
/*
        if (ActivityCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
            Log.e("DB", "PERMISSION GRANTED");
        }

        */

        return view;


    }


    private void startWorkingWithLocation() {

        if (temp != null) {
            lat = temp.getLatitude();
            lon = temp.getLongitude();

            intent1.putExtra("lat", lat);
            intent1.putExtra("lon", lon);
            intent1.putExtra("keyWord", "");
            getActivity().startService(intent1);

        }


    }


    public Location getLocation() {
        try {


            // Getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // Getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // No network provider is enabled
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return null;
                    }
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // If GPS enabled, get latitude/longitude using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    @Override
    public void onLocationChanged(Location location) {

        lat = location.getLatitude();
        lon = location.getLongitude();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check if the only required permission has been granted
        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, this);
                temp = getLocation();
                startWorkingWithLocation();
            }
        } else {
            Toast.makeText(getActivity(), "please allow location ", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    class CheckHasBeenFinished extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {


            Toast.makeText(context, "finished !!", Toast.LENGTH_SHORT).show();
            try {
                JSONObject mainObject = new JSONObject(result);
                JSONArray resultsArray = mainObject.getJSONArray("results");


                Log.d("dsd", "dsds");
                if (place != null) {
                    place.delete();
                }

                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject currentObject = resultsArray.getJSONObject(i);

                    String name = currentObject.getString("name");
                    String vicinity = currentObject.getString("vicinity");
                    String icon = currentObject.getString("icon");
                    JSONObject geometry = currentObject.getJSONObject("geometry");
                    JSONObject location = geometry.getJSONObject("location");
                    double lat1 = location.getDouble("lat");
                    double lng1 = location.getDouble("lng");


                    int r = 6371; // average radius of the earth in km
                    double dLat = Math.toRadians(lat - lat1);
                    double dLon = Math.toRadians(lon - lng1);
                    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat))
                                    * Math.sin(dLon / 2) * Math.sin(dLon / 2);
                    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                    double d = r * c;
                    double currentdistance = d;
                    place = new MyPlace(keyWord, name, vicinity, icon, currentdistance);

                    place.save();


                }
                // adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();

            }

            List<MyPlace> allplaces = MyPlace.listAll(MyPlace.class);

            adapter = new MyPlaceAdapter(allplaces, getContext());

            placesRV.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
     currentposition = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
        getActivity().getMenuInflater().inflate(R.menu.menu_onitem, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.addToFavo){
// add to favorites activity and save on data base
  //          favoplace = new MyPlace(keyWord, name, vicinity, icon,currentdistance);
 //           favoplace.save();

        }
        else if (item.getItemId()==R.id.openMap){

//open mapfragment and shot the place on the map



        }
return true;

    }

    public void SearchNearMe(CheckBox nearCB) {
        if (nearCB.isChecked()) {
            near = true;
        } else {

            near = false;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        List<MyPlace> allplaces = MyPlace.listAll(MyPlace.class);


        adapter = new MyPlaceAdapter(allplaces, getContext());


        placesRV.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        // 32.297497, 34.845067


    }

}
