package edu.mysore.asas.aura_hospital;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Button;
import android.view.View;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
//import android.support.anno
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.WindowManager;
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

public class Home extends AppCompatActivity implements View.OnClickListener {

    private Button addbtn;
    private Button editbtn;
    private Button deletebtn;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);
        addbtn=findViewById(R.id.btnadd);
        addbtn.setOnClickListener(Home.this);
        editbtn=findViewById(R.id.btnedit);
        editbtn.setOnClickListener(Home.this);
        deletebtn=findViewById(R.id.btnremove);
        deletebtn.setOnClickListener(Home.this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword("aura.asasmysore@gmail.com", "aura@1234").addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Home.this, "Connected", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Home.this, "Error in connectivity", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        if(v==addbtn)
        {
            finish();
            startActivity(new Intent(getApplicationContext(), AddHospital.class));
        }
        if(v==editbtn)
        {
            finish();
            startActivity(new Intent(getApplicationContext(), EditHospital.class));
        }
        if(v==deletebtn)
        {
            finish();
            startActivity(new Intent(getApplicationContext(), RemoveHospital.class));
        }
    }
    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(Home.this);
        builder1.setMessage("You are about to Logout");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Logout",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(Home.this,"Logging out",Toast.LENGTH_SHORT).show();
                        finishAndRemoveTask();
                        startActivity(new Intent(Home.this, MainActivity.class));
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
    }
}
