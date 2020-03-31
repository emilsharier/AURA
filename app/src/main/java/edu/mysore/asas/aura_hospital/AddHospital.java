package edu.mysore.asas.aura_hospital;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddHospital extends AppCompatActivity implements OnClickListener{

    DatabaseReference mDatabase;
    Button btnlocate;
    EditText txthosname;
    EditText txthosid;
    Spinner txtpathocount;
    Spinner txtimmunocount;
    Spinner txtanesthocount;
    Spinner txtcardiocount;
    Spinner txtentcount;
    Spinner txtmricount;
    Spinner txtctcount;
    Spinner txtxraycount;
    Spinner checkERval;

    String hosName;
    String hosId;
    String pathos;
    String immunos;
    String anesthos;
    String cardios;
    String ents;
    String erVal;
    String MRI;
    String CT;
    String Xray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hospital);
        mDatabase=FirebaseDatabase.getInstance().getReferenceFromUrl("https://aura-9fb32.firebaseio.com");
        btnlocate=findViewById(R.id.btnfetchloc);
        btnlocate.setOnClickListener(AddHospital.this);
        txthosname=findViewById(R.id.hosname);
        txthosid=findViewById(R.id.hosid);
        String[] items = new String[]{"1","2","3","4","5","6","7","8","9","10"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        txtpathocount=findViewById(R.id.pathocount);
        txtpathocount.setAdapter(adapter);
        txtimmunocount=findViewById(R.id.immunocount);
        txtimmunocount.setAdapter(adapter);
        txtanesthocount=findViewById(R.id.anesthocount);
        txtanesthocount.setAdapter(adapter);
        txtcardiocount=findViewById(R.id.cardiocount);
        txtcardiocount.setAdapter(adapter);
        txtentcount=findViewById(R.id.entcount);
        txtentcount.setAdapter(adapter);
        checkERval=findViewById(R.id.ervalue);
        checkERval.setAdapter(adapter);
        txtmricount=findViewById(R.id.mricount);
        txtmricount.setAdapter(adapter);
        txtctcount=findViewById(R.id.ctcount);
        txtctcount.setAdapter(adapter);
        txtxraycount=findViewById(R.id.xraycount);
        txtxraycount.setAdapter(adapter);

        Intent intent=getIntent();
        try
        {
            Bundle data = getIntent().getExtras();
            hosId = data.getString("hosid");
            txthosid.setText(hosId);
            txthosid.setEnabled(false);
        }
        catch (Exception e)
        {
            txthosid.setText("");
            txthosid.setEnabled(true);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            finish();
            startActivity(new Intent(getApplicationContext(), Home.class));
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        if(v==btnlocate){
            hosName = txthosname.getText().toString().trim();
            hosId = txthosid.getText().toString().trim();
            pathos = txtpathocount.getSelectedItem().toString().trim();
            immunos = txtimmunocount.getSelectedItem().toString().trim();
            anesthos = txtanesthocount.getSelectedItem().toString().trim();
            cardios = txtcardiocount.getSelectedItem().toString().trim();
            ents = txtentcount.getSelectedItem().toString().trim();
            MRI = txtmricount.getSelectedItem().toString().trim();
            CT = txtctcount.getSelectedItem().toString().trim();
            Xray = txtxraycount.getSelectedItem().toString().trim();
            erVal=checkERval.getSelectedItem().toString().trim();
            Toast.makeText(AddHospital.this, "Adding", Toast.LENGTH_SHORT).show();
            if (TextUtils.isEmpty(hosName) || TextUtils.isEmpty(hosId) || TextUtils.isEmpty(pathos) || TextUtils.isEmpty(anesthos) || TextUtils.isEmpty(cardios) || TextUtils.isEmpty(ents) || TextUtils.isEmpty(MRI) || TextUtils.isEmpty(CT) || TextUtils.isEmpty(Xray)) {
                Toast.makeText(AddHospital.this, "Fill in all the fields!", Toast.LENGTH_SHORT).show();
                return;
            }
            else
            {
                Intent intent=new Intent(AddHospital.this,MitchMap.class);
                intent.putExtra("hosname",hosName);
                intent.putExtra("hosid",hosId);
                intent.putExtra("pathos",pathos);
                intent.putExtra("immunos",immunos);
                intent.putExtra("anesthos",anesthos);
                intent.putExtra("cardios",cardios);
                intent.putExtra("ents",ents);
                intent.putExtra("ervalue",erVal);
                intent.putExtra("mri",MRI);
                intent.putExtra("ct",CT);
                intent.putExtra("xray",Xray);
                startActivity(intent);
            }
        }
    }
}
