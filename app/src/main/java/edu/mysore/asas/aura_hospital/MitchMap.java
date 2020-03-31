package edu.mysore.asas.aura_hospital;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.Manifest;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.mysore.asas.aura_hospital.models.PlaceInfo;


public class MitchMap extends AppCompatActivity implements OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener,GoogleMap.OnMarkerClickListener,GoogleMap.OnMarkerDragListener {

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    ///Data from AddHospital
    private String hosName;
    private String hosId;
    private String pathos;
    private String immunos;
    private String anesthos;
    private String cardios;
    private String ents;
    private String erVal;
    private String MRI;
    private String CT;
    private String Xray;
    ///

    private static LatLng selectedLatLng=new LatLng(0,0);

    private static final String TAG = "MapActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int PLACE_PICKER_REQUEST=1;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean mLocationPermissionGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private GoogleMap mMap;
    private FusedLocationProviderClient mfusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 15f;
    private GoogleApiClient mGoogleApiClient;
    private static final LatLngBounds LAT_LNG_BOUNDS=new LatLngBounds(new LatLng(-40,-168),new LatLng(71,136));
    private PlaceInfo mPlace;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    //widgets
    private AutoCompleteTextView mSearchText;
    private ImageView mGps,mPlacePicker;

    ///upload
    private Button upload;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabasecheck;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        Log.d("MapActivity", "Maps is ready");
        mMap = googleMap;
        if (mLocationPermissionGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            // to get user location
            mMap.setMyLocationEnabled(true);
            // to hide the default location check button
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            init();
        }
        mMap.setOnMarkerClickListener(MitchMap.this);
        mMap.setOnMarkerDragListener(MitchMap.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mitch_map);
        getLocationPermission();
        mSearchText=(AutoCompleteTextView)findViewById(R.id.input_search);
        mGps=(ImageView)findViewById(R.id.ic_gps);
        mPlacePicker=(ImageView)findViewById(R.id.place_picker);
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://aura-9fb32.firebaseio.com/");
        Intent intent=getIntent();
        try
        {
            Bundle data=getIntent().getExtras();
            hosId=data.getString("hosid");
            hosName=data.getString("hosname");
            pathos=data.getString("pathos");
            immunos=data.getString("immunos");
            anesthos=data.getString("anesthos");
            cardios=data.getString("cardios");
            ents=data.getString("ents");
            erVal=data.getString("ervalue");
            MRI=data.getString("mri");
            CT=data.getString("ct");
            Xray=data.getString("xray");
        }
        catch (Exception e) {
            Log.d("AURA", "Error Getting data " + e.getMessage());
        }

        upload=(Button) findViewById(R.id.btnloadtofirebase);
    }


    private void init()
    {
        Log.d(TAG,"Init: initializing");
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this,this)
                .build();

        mSearchText.setOnItemClickListener(mAutocompleteClickListener);

        mPlaceAutocompleteAdapter=new PlaceAutocompleteAdapter(this,mGoogleApiClient,LAT_LNG_BOUNDS,null);
        mSearchText.setAdapter(mPlaceAutocompleteAdapter);
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                if(actionId==EditorInfo.IME_ACTION_SEARCH
                        || actionId==EditorInfo.IME_ACTION_DONE
                        ||keyEvent.getAction()==KeyEvent.ACTION_DOWN
                        ||keyEvent.getAction()==KeyEvent.KEYCODE_ENTER)
                {
                    //executing search
                    geolocate();
                }
                return false;
            }
        });

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"OnClick: GPS Location");
                getDeviceLocation();

            }
        });
        mPlacePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PlacePicker.IntentBuilder builder=new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(MitchMap.this),PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    Log.d(TAG,"onClick: GooglePlayServicesRepairableException: "+e.getMessage());
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.d(TAG,"onClick: GooglePlayServicesNotAvailableException: "+e.getMessage());
                }
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(selectedLatLng.longitude==0) && !(selectedLatLng.latitude==0)) {
                    Toast.makeText(MitchMap.this,"Please wait while all values are uploaded",Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Uploading values"
                            + "\nHosname:" + hosName
                            + "\nHosId:" + hosId
                            + "\nPathos:" + pathos
                            + "\nImmunos:" + immunos
                            + "\nAnesthos:" + anesthos
                            + "\nCardios:" + cardios
                            + "\nENTs:" + ents
                            + "\nERValue:" + erVal
                            + "\nMRI:" + MRI
                            + "\nCT:" + CT
                            + "\nX-Ray:" + Xray
                            + "\nLatitude:" + selectedLatLng.latitude
                            + "\nLongitude:" + selectedLatLng.longitude);

                    try
                    {
                        mDatabasecheck = FirebaseDatabase.getInstance().getReferenceFromUrl("https://aura-9fb32.firebaseio.com/hospitalslist/"+hosId);
                        mDatabase.child("hospitalslist").child(hosId).child("hosid").setValue(hosId,
                                //Toast.makeText(MitchMap.this, "Added Hospital Name", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Added Hospital Name");
                        mDatabase.child("hospitalslist").child(hosId).child("pathos").setValue(pathos, new DatabaseReference.CompletionListener() {
                            public void onComplete(DatabaseError error, DatabaseReference ref) {
                                //Toast.makeText(MitchMap.this, "Added Pathologists", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Added Pathologists");new DatabaseReference.CompletionListener() {
                            public void onComplete(DatabaseError error, DatabaseReference ref) {
                                //Toast.makeText(MitchMap.this, "Added Hospital ID", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Added Hospital ID");
                                mDatabase.child("hospitalslist").child(hosId).child("hosname").setValue(hosName, new DatabaseReference.CompletionListener() {
                                    public void onComplete(DatabaseError error, DatabaseReference ref) {
                                                mDatabase.child("hospitalslist").child(hosId).child("immunos").setValue(immunos, new DatabaseReference.CompletionListener() {
                                                    public void onComplete(DatabaseError error, DatabaseReference ref) {
                                                        //Toast.makeText(MitchMap.this, "Added Immunologists", Toast.LENGTH_SHORT).show();
                                                        Log.d(TAG, "Added Immunologists");
                                                        mDatabase.child("hospitalslist").child(hosId).child("anesthos").setValue(anesthos, new DatabaseReference.CompletionListener() {
                                                            public void onComplete(DatabaseError error, DatabaseReference ref) {
                                                                //Toast.makeText(MitchMap.this, "Added Anesthologists", Toast.LENGTH_SHORT).show();
                                                                Log.d(TAG, "Added Anesthologists");
                                                                mDatabase.child("hospitalslist").child(hosId).child("cardios").setValue(cardios, new DatabaseReference.CompletionListener() {
                                                                    public void onComplete(DatabaseError error, DatabaseReference ref) {
                                                                        //Toast.makeText(MitchMap.this, "Added Cardiologists", Toast.LENGTH_SHORT).show();
                                                                        Log.d(TAG, "Added Cardiologists");
                                                                        mDatabase.child("hospitalslist").child(hosId).child("ents").setValue(ents, new DatabaseReference.CompletionListener() {
                                                                            public void onComplete(DatabaseError error, DatabaseReference ref) {
                                                                                //Toast.makeText(MitchMap.this, "Added ENT Specialists", Toast.LENGTH_SHORT).show();
                                                                                Log.d(TAG, "Added ENT Specialists");
                                                                                mDatabase.child("hospitalslist").child(hosId).child("ervalue").setValue(erVal, new DatabaseReference.CompletionListener() {
                                                                                    public void onComplete(DatabaseError error, DatabaseReference ref) {
                                                                                        //Toast.makeText(MitchMap.this, "Added Emergency Rooms", Toast.LENGTH_SHORT).show();
                                                                                        Log.d(TAG, "Added ER Values");
                                                                                        mDatabase.child("hospitalslist").child(hosId).child("mri").setValue(MRI, new DatabaseReference.CompletionListener() {
                                                                                            public void onComplete(DatabaseError error, DatabaseReference ref) {
                                                                                                //Toast.makeText(MitchMap.this, "Added MRI Scanners", Toast.LENGTH_SHORT).show();
                                                                                                Log.d(TAG, "Added MRI");
                                                                                                mDatabase.child("hospitalslist").child(hosId).child("ct").setValue(CT, new DatabaseReference.CompletionListener() {
                                                                                                    public void onComplete(DatabaseError error, DatabaseReference ref) {
                                                                                                        //Toast.makeText(MitchMap.this, "Added CT Scanners", Toast.LENGTH_SHORT).show();
                                                                                                        Log.d(TAG, "Added CT");
                                                                                                        mDatabase.child("hospitalslist").child(hosId).child("xray").setValue(Xray, new DatabaseReference.CompletionListener() {
                                                                                                            public void onComplete(DatabaseError error, DatabaseReference ref) {
                                                                                                                //Toast.makeText(MitchMap.this, "Added XRay Machines", Toast.LENGTH_SHORT).show();
                                                                                                                Log.d(TAG, "Added Xray");
                                                                                                                mDatabase.child("hospitalslist").child(hosId).child("latitude").setValue(selectedLatLng.latitude, new DatabaseReference.CompletionListener() {
                                                                                                                    public void onComplete(DatabaseError error, DatabaseReference ref) {
                                                                                                                        //Toast.makeText(MitchMap.this, "Added Latitude", Toast.LENGTH_SHORT).show();
                                                                                                                        Log.d(TAG, "Added Latitude");
                                                                                                                        mDatabase.child("hospitalslist").child(hosId).child("longitude").setValue(selectedLatLng.longitude, new DatabaseReference.CompletionListener() {
                                                                                                                            public void onComplete(DatabaseError error, DatabaseReference ref) {
                                                                                                                                //Toast.makeText(MitchMap.this, "Added Longitude", Toast.LENGTH_SHORT).show();
                                                                                                                                Log.d(TAG, "Added Longitude");
                                                                                                                            }
                                                                                                                        });

                                                                                                                    }
                                                                                                                });

                                                                                                            }
                                                                                                        });

                                                                                                    }
                                                                                                });

                                                                                            }
                                                                                        });

                                                                                    }
                                                                                });

                                                                            }
                                                                        });

                                                                    }
                                                                });

                                                            }
                                                        });

                                                    }
                                                });

                                            }
                                        });

                                    }
                                });
                            }
                        });
                        mDatabase.child("hospitalslist").child("AllHospitals").child(hosId).setValue(hosId, new DatabaseReference.CompletionListener() {
                            public void onComplete(DatabaseError error, DatabaseReference ref) {
                                Toast.makeText(MitchMap.this, "Hospital Added Successfully", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(getApplicationContext(), Home.class));
                            }
                        });

                    }
                    catch (DatabaseException e)
                    {
                        Toast.makeText(MitchMap.this, "Use Alpha-Numericals only for hospital ID", Toast.LENGTH_SHORT).show();
                        Log.d("Database error",e.getMessage());
                    }
                    catch (NullPointerException e)
                    {
                        try {
                            mDatabase.child("hospitalslist").child(hosId).child("hosid").setValue(hosId, new DatabaseReference.CompletionListener() {
                                public void onComplete(DatabaseError error, DatabaseReference ref) {
                                    Toast.makeText(MitchMap.this, "Added Hospital ID", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Added Hospital ID");
                                    mDatabase.child("hospitalslist").child(hosId).child("hosname").setValue(hosName, new DatabaseReference.CompletionListener() {
                                        public void onComplete(DatabaseError error, DatabaseReference ref) {
                                            Toast.makeText(MitchMap.this, "Added Hospital Name", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "Added Hospital Name");
                                            mDatabase.child("hospitalslist").child(hosId).child("pathos").setValue(pathos, new DatabaseReference.CompletionListener() {
                                                public void onComplete(DatabaseError error, DatabaseReference ref) {
                                                    Toast.makeText(MitchMap.this, "Added Pathologists", Toast.LENGTH_SHORT).show();
                                                    Log.d(TAG, "Added Pathologists");
                                                  mDatabase.child("hospitalslist").child(hosId).child("immunos").setValue(immunos, new DatabaseReference.CompletionListener() {
                                                        public void onComplete(DatabaseError error, DatabaseReference ref) {
                                                            Toast.makeText(MitchMap.this, "Added Immunologists", Toast.LENGTH_SHORT).show();
                                                            Log.d(TAG, "Added Immunologists");
                                                            mDatabase.child("hospitalslist").child(hosId).child("anesthos").setValue(anesthos, new DatabaseReference.CompletionListener() {
                                                                public void onComplete(DatabaseError error, DatabaseReference ref) {
                                                                    Toast.makeText(MitchMap.this, "Added Anesthologists", Toast.LENGTH_SHORT).show();
                                                                    Log.d(TAG, "Added Anesthologists");
                                                                    mDatabase.child("hospitalslist").child(hosId).child("cardios").setValue(cardios, new DatabaseReference.CompletionListener() {
                                                                        public void onComplete(DatabaseError error, DatabaseReference ref) {
                                                                            Toast.makeText(MitchMap.this, "Added Cardiologists", Toast.LENGTH_SHORT).show();
                                                                            Log.d(TAG, "Added Cardiologists");
                                                                            mDatabase.child("hospitalslist").child(hosId).child("ents").setValue(ents, new DatabaseReference.CompletionListener() {
                                                                                public void onComplete(DatabaseError error, DatabaseReference ref) {
                                                                                    Toast.makeText(MitchMap.this, "Added ENT Specialists", Toast.LENGTH_SHORT).show();
                                                                                    Log.d(TAG, "Added ENT Specialists");
                                                                                    mDatabase.child("hospitalslist").child(hosId).child("ervalue").setValue(erVal, new DatabaseReference.CompletionListener() {
                                                                                        public void onComplete(DatabaseError error, DatabaseReference ref) {
                                                                                            Toast.makeText(MitchMap.this, "Added Emergency Rooms", Toast.LENGTH_SHORT).show();
                                                                                            Log.d(TAG, "Added ER Values");
                                                                                            mDatabase.child("hospitalslist").child(hosId).child("mri").setValue(MRI, new DatabaseReference.CompletionListener() {
                                                                                                public void onComplete(DatabaseError error, DatabaseReference ref) {
                                                                                                    Toast.makeText(MitchMap.this, "Added MRI Scanners", Toast.LENGTH_SHORT).show();
                                                                                                    Log.d(TAG, "Added MRI");
                                                                                                    mDatabase.child("hospitalslist").child(hosId).child("ct").setValue(CT, new DatabaseReference.CompletionListener() {
                                                                                                        public void onComplete(DatabaseError error, DatabaseReference ref) {
                                                                                                            Toast.makeText(MitchMap.this, "Added CT Scanners", Toast.LENGTH_SHORT).show();
                                                                                                            Log.d(TAG, "Added CT");
                                                                                                            mDatabase.child("hospitalslist").child(hosId).child("xray").setValue(Xray, new DatabaseReference.CompletionListener() {
                                                                                                                public void onComplete(DatabaseError error, DatabaseReference ref) {
                                                                                                                    Toast.makeText(MitchMap.this, "Added XRay Machines", Toast.LENGTH_SHORT).show();
                                                                                                                    Log.d(TAG, "Added Xray");
                                                                                                                    mDatabase.child("hospitalslist").child(hosId).child("latitude").setValue(selectedLatLng.latitude, new DatabaseReference.CompletionListener() {
                                                                                                                        public void onComplete(DatabaseError error, DatabaseReference ref) {
                                                                                                                            Toast.makeText(MitchMap.this, "Added Latitude", Toast.LENGTH_SHORT).show();
                                                                                                                            Log.d(TAG, "Added Latitude");
                                                                                                                            mDatabase.child("hospitalslist").child(hosId).child("longitude").setValue(selectedLatLng.longitude, new DatabaseReference.CompletionListener() {
                                                                                                                                public void onComplete(DatabaseError error, DatabaseReference ref) {
                                                                                                                                    Toast.makeText(MitchMap.this, "Added Longitude", Toast.LENGTH_SHORT).show();
                                                                                                                                    Log.d(TAG, "Added Longitude");
                                                                                                                                }
                                                                                                                            });

                                                                                                                        }
                                                                                                                    });

                                                                                                                }
                                                                                                            });

                                                                                                        }
                                                                                                    });

                                                                                                }
                                                                                            });

                                                                                        }
                                                                                    });

                                                                                }
                                                                            });

                                                                        }
                                                                    });

                                                                }
                                                            });

                                                        }
                                                    });

                                                }
                                            });

                                        }
                                    });
                                }
                            });
                            mDatabase.child("hospitalslist").child("AllHospitals").child(hosId).setValue(hosId, new DatabaseReference.CompletionListener() {
                                public void onComplete(DatabaseError error, DatabaseReference ref) {
                                    Toast.makeText(MitchMap.this, "Hospital Added Successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), Home.class));
                                }
                            });

                        }
                        catch (DatabaseException ex)
                        {
                            Toast.makeText(MitchMap.this, "Use Alpha-Numericals only", Toast.LENGTH_SHORT).show();
                            Log.d("Database error", ex.getMessage());
                        }
                    }
                }
                else
                {
                    Log.d(TAG, "Didnt retreive all values");
                    Toast.makeText(MitchMap.this,"Location selection error",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                PendingResult<PlaceBuffer> placeResult=Places.GeoDataApi.getPlaceById(mGoogleApiClient,place.getId());
                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
                selectedLatLng=new LatLng(place.getViewport().getCenter().latitude,place.getViewport().getCenter().longitude);
            }
        }
    }



    private void geolocate()
    {
        Log.d(TAG,"Geolocate: geo locating");
        String searchString=mSearchText.getText().toString();
        Geocoder geocoder=new Geocoder(MitchMap.this);
        List<Address> list=new ArrayList<>();
        try
        {
            list=geocoder.getFromLocationName(searchString,1);
        }
        catch (IOException e)
        {
            Log.d(TAG,"Geolocate:  IOException"+e.getMessage());
        }
        if(list.size()>0)
        {
            Address address=list.get(0);
            Log.d(TAG,"Geolocate: found location"+address.toString());
            //Toast.makeText(this,address.toString(),Toast.LENGTH_SHORT).show();
            selectedLatLng=new LatLng(address.getLatitude(),address.getLongitude());
            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,address.getAddressLine(0));
        }
    }

    private void getDeviceLocation()
    {
        Log.d(TAG,"getDeviceLocation: Getting device's current location");
        mfusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this);
        try
        {
            if(mLocationPermissionGranted)
            {
                Task location=mfusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful())
                        {
                            Log.d(TAG,"onComplete: Found location");
                            Location currentLocation=(Location) task.getResult();
                            selectedLatLng=new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                            moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),DEFAULT_ZOOM,"Current Location");
                        }
                        else
                        {
                            Log.d(TAG,"onComplete: Current Location is null");
                            Toast.makeText(MitchMap.this,"Unable to find current location",Toast.LENGTH_SHORT).show();;
                        }
                    }
                });
            }
        }
        catch (SecurityException e)
        {
            Log.d(TAG,"getDeviceLocation: Security Exception" + e.getMessage());
        }
    }


    private void moveCamera(LatLng latlng, float zoom,String title)
    {
        Log.d(TAG,"Moving the camera to: lat: "+ latlng.latitude +" lon: "+ latlng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,zoom));

        if(!title.equals("Current Location")) {
            MarkerOptions options = new MarkerOptions()
                    .position(latlng)
                    .title(title);
            mMap.addMarker(options);
        }
        selectedLatLng=latlng;
    }

    private void initMap()
    {
        Log.d("MapActivity","Initialising Map");
        SupportMapFragment mapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map1);
        mapFragment.getMapAsync(MitchMap.this);
    }

    private void getLocationPermission(){
        String[] permissions={Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED)
            {
                mLocationPermissionGranted=true;
                initMap();
            }
            else
            {
                ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else
        {
            ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted=false;
        switch (requestCode)
        {
            case LOCATION_PERMISSION_REQUEST_CODE:
            {
                if (grantResults.length>0)
                {
                    for (int i=0;i<grantResults.length;i++)
                    {
                        if (grantResults[i]!=PackageManager.PERMISSION_GRANTED)
                        {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted=true;
                    initMap();
                }
            }
        }
    }
//
    //
    //
    // Auto complete functions
    //
    //

    private AdapterView.OnItemClickListener mAutocompleteClickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final AutocompletePrediction item=mPlaceAutocompleteAdapter.getItem(position);
            final String placeId=item.getPlaceId();
            PendingResult<PlaceBuffer> placeResult=Places.GeoDataApi.getPlaceById(mGoogleApiClient,placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback=new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess())
            {
                Log.d(TAG,"onResult: Place query didnt complete successfully"+places.getStatus().toString());
                places.release();
                return;
            }
            final Place place=places.get(0);

            try {
                mPlace = new PlaceInfo();
                mPlace.setName(place.getName().toString());
                mPlace.setAddress(place.getAddress().toString());
                //mPlace.setAttributions(place.getAttributions().toString());//causing nullpointer exception
                mPlace.setId(place.getId().toString());
                mPlace.setLatLng(place.getLatLng());
                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
                mPlace.setWebsiteUri(place.getWebsiteUri());
            }
            catch (NullPointerException e)
            {
                Log.d(TAG,"onResult:NullPointer Exception:"+e.getMessage());
            }

            Log.d(TAG,"onResult:Place details:"+mPlace.toString());

            selectedLatLng=new LatLng(place.getViewport().getCenter().latitude, place.getViewport().getCenter().longitude);
            moveCamera(new LatLng(place.getViewport().getCenter().latitude, place.getViewport().getCenter().longitude),DEFAULT_ZOOM,mPlace.getName());

            places.release();
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            finish();
            startActivity(new Intent(getApplicationContext(), AddHospital.class));
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.setDraggable(true);
        moveCamera(new LatLng(marker.getPosition().latitude,marker.getPosition().longitude),DEFAULT_ZOOM,marker.getTitle());
        selectedLatLng=new LatLng(marker.getPosition().latitude,marker.getPosition().longitude);
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        moveCamera(new LatLng(marker.getPosition().latitude,marker.getPosition().longitude),DEFAULT_ZOOM,marker.getTitle());
        selectedLatLng=new LatLng(marker.getPosition().latitude,marker.getPosition().longitude);
    }
}
