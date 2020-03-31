package edu.mysore.asas.aura_hospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.mysore.asas.aura_hospital.models.HospitalModel;

public class ViewHospitalsAfterSelection extends AppCompatActivity {

    //Declaration of Firebase variables
    FirebaseDatabase database;
    DatabaseReference myRef;

    //Variable to store the data from bundle
    String hospId;

    //Declaration of screen components
    private TextView hosID;
    private TextView hosName;
    private TextView xray;
    private TextView anesthos;
    private TextView cardios;
    private TextView ct;
    private TextView ents;
    private TextView er;
    private TextView mri;
    private TextView pathos;
    private TextView longitude;
    private TextView latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_hospitals_after_selection);

        //Getting the bundle value
        hospId = getIntent().getExtras().getString("hosId");

        Log.d("hospital", hospId);

        //registering the UI components
        hosID = findViewById(R.id.hospitalId);
        hosName = findViewById(R.id.hospitalName);
        xray = findViewById(R.id.xray);
        anesthos = findViewById(R.id.anesthos);
        cardios = findViewById(R.id.cardios);
        ct = findViewById(R.id.ct);
        ents = findViewById(R.id.ents);
        er = findViewById(R.id.erValue);
        mri = findViewById(R.id.mri);
        pathos = findViewById(R.id.pathos);
        longitude = findViewById(R.id.longitude);
        latitude = findViewById(R.id.latitude);

        //Initialisation of Firebase variables
        database = FirebaseDatabase.getInstance();
        myRef = database.getReferenceFromUrl("https://aura-9fb32.firebaseio.com/hospitalslist/" + hospId);

        try {
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    HospitalModel model = dataSnapshot.getValue(HospitalModel.class);
                    hosID.setText(model.getHosid());
                    hosName.setText(model.getHosname());
                    xray.setText(model.getXray());
                    anesthos.setText(model.getAnesthos());
                    cardios.setText(model.getCardios());
                    ct.setText(model.getCt());
                    ents.setText(model.getEnts());
                    er.setText(model.getErvalue());
                    mri.setText(model.getMri());
                    pathos.setText(model.getPathos());
                    longitude.setText(model.getLongitude().toString());
                    latitude.setText(model.getLatitude().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception ex) {
            Log.e("moonjal", ex.toString());
        }
    }
}
