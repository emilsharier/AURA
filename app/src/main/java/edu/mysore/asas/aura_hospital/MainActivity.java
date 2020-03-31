package edu.mysore.asas.aura_hospital;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private int LOCATION_PERMISSION_CODE = 1;

    private EditText username;
    private EditText password;
    private TextView register;
    private Button loginbutton;
    private FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    String txtuser;
    String txtpw;
    String txttype;

    String getUser;
    String getPw;
    String getType;

    private DatabaseReference mDatabase_user;
    private DatabaseReference mDatabase_ref;
    private DatabaseReference mDatabase_pass;
    private DatabaseReference mDatabase_type;

    private ValueEventListener postListener_user;
    private ValueEventListener postListener_pass;
    private ValueEventListener postListener_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        username=findViewById(R.id.txtusername);
        password=findViewById(R.id.txtpassword);
        register=findViewById(R.id.register);
        loginbutton=findViewById(R.id.btnlogin);
        loginbutton.setOnClickListener(this);
        register.setOnClickListener(this);
        firebaseAuth=FirebaseAuth.getInstance();

        firebaseAuth.signInWithEmailAndPassword("aura.asasmysore@gmail.com", "aura@1234").addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                    }
                    else {
                        requestLocationPermissions();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Error in connectivity", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void requestLocationPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(MainActivity.this, "Need permission to access location", Toast.LENGTH_SHORT);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        }
    }

    @Override
    public void onClick(View v)
    {
        if(v==loginbutton)
        {
            txtuser = username.getText().toString().trim();
            txtpw = password.getText().toString().trim();
            try {

                mDatabase_user = FirebaseDatabase.getInstance().getReferenceFromUrl("https://aura-9fb32.firebaseio.com/userslist/" + txtuser + "/username");
                postListener_user = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        getUser = dataSnapshot.getValue(String.class);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.d("OnCancelledLog", "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                };
                mDatabase_user.addValueEventListener(postListener_user);



                mDatabase_pass = FirebaseDatabase.getInstance().getReferenceFromUrl("https://aura-9fb32.firebaseio.com/userslist/" + txtuser + "/password");
                postListener_pass = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        getPw = dataSnapshot.getValue(String.class);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.d("OnCancelledLog", "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                };
                mDatabase_pass.addValueEventListener(postListener_pass);

                mDatabase_type = FirebaseDatabase.getInstance().getReferenceFromUrl("https://aura-9fb32.firebaseio.com/userslist/" + txtuser + "/type");
                postListener_type = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        getType = dataSnapshot.getValue(String.class);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.d("OnCancelledLog", "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                };
                mDatabase_type.addValueEventListener(postListener_type);

                if (getPw.equals(txtpw)) {
                    if (getType.equals("Hospital")) {
                        finish();
                        startActivity(new Intent(getApplicationContext(), Home.class));
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Invalid Credentials", Toast.LENGTH_LONG).show();
                }
            } catch (NullPointerException e) {
                Toast.makeText(MainActivity.this, "Checking Credentials", Toast.LENGTH_LONG).show();
            }

        }
        if(v==register)
        {
            finish();
            startActivity(new Intent(getApplicationContext(), Register.class));
        }
    }
public void onBackPressed()
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setMessage("Close the application?");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Close",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finishAndRemoveTask();
                        finishAffinity();
                        System.exit(0);
                    }
                });

        builder1.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        builder1.show();
        //alert11.show();
    }
}
