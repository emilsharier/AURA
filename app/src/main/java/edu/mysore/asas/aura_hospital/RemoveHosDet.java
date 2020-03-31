package edu.mysore.asas.aura_hospital;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;

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
