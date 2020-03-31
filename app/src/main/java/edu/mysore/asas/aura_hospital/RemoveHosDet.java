package edu.mysore.asas.aura_hospital;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseError;

public class RemoveHosDet extends AppCompatActivity implements View.OnClickListener{

    private Button btndelete;
    private EditText txthosid;
    private String hosId="";
    private String verifyHosId;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_hos_det);
        btndelete=findViewById(R.id.btndelete);
        btndelete.setOnClickListener(this);
        txthosid=findViewById(R.id.txtVerifyHosID);
        Intent intent=getIntent();
        try {
            Bundle data = getIntent().getExtras();
            hosId = data.getString("hosid");
        }
        catch (Exception e)
        {
            Toast.makeText(RemoveHosDet.this, "Please select a hospital", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(getApplicationContext(), Home.class));
        }
        mDatabase=FirebaseDatabase.getInstance().getReferenceFromUrl("https://aura-9fb32.firebaseio.com/hospitalslist");
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            finish();
            startActivity(new Intent(getApplicationContext(), RemoveHospital.class));
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v)
    {
        if(v==btndelete)
        {
            verifyHosId=txthosid.getText().toString().trim();
            if (hosId.equals(verifyHosId))
            {
                mDatabase.child(hosId).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        mDatabase.child("AllHospitals").child(hosId).removeValue();
                        Toast.makeText(RemoveHosDet.this, "Hospital Removed Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(getApplicationContext(), Home.class));
                    }
                });
            }
        }
    }
}
