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


public class Register extends AppCompatActivity implements View.OnClickListener {

    private EditText reguser;
    private EditText regpw;
    private EditText regconpw;
    private EditText regHosid;
    private Button btnreg;
    private ValueEventListener postListener_user;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabase_user;
    private String txtuser;
    private String txtpw;
    private String txtconpw;
    private String type;
    private String hosid;
    private String getUsername;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);

        reguser = findViewById(R.id.regUsername);
        regpw = findViewById(R.id.regpw);
        regconpw = findViewById(R.id.regconpw);
        regHosid = findViewById(R.id.regID);
        btnreg=findViewById(R.id.btnreg);
        btnreg.setOnClickListener(this);
        mDatabase=FirebaseDatabase.getInstance().getReferenceFromUrl("https://aura-9fb32.firebaseio.com");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword("aura.asasmysore@gmail.com", "aura@1234").addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Register.this, "Connected", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Register.this, "Error in connectivity", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        if (v==btnreg) {
            txtuser = reguser.getText().toString().trim();
            txtpw = regpw.getText().toString().trim();
            txtconpw = regconpw.getText().toString().trim();
            hosid = regHosid.getText().toString().trim();
            type = "Hospital";
            Toast.makeText(Register.this, type, Toast.LENGTH_SHORT).show();
            if (TextUtils.isEmpty(txtuser) || TextUtils.isEmpty(txtpw) || TextUtils.isEmpty(txtconpw)) {
                Toast.makeText(Register.this, "Fill in all the fields!", Toast.LENGTH_SHORT).show();
                return;
            } else if (type == null) {
                Toast.makeText(Register.this, "Please select a field", Toast.LENGTH_SHORT).show();
                return;
            } else if (!txtpw.equals(txtconpw)) {
                Toast.makeText(Register.this, "Passwords don't match!", Toast.LENGTH_SHORT).show();
                return;
            } else {
                try {
                    mDatabase_user = FirebaseDatabase.getInstance().getReferenceFromUrl("https://aura-9fb32.firebaseio.com/userslist/" + txtuser + "/username");
                    postListener_user = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            getUsername = dataSnapshot.getValue(String.class);
                            try {
                                if (getUsername.equals(txtuser)) {
                                    Toast.makeText(Register.this, "Username Exists", Toast.LENGTH_SHORT).show();
                                }
                            } catch (NullPointerException e) {
                                try {
                                    mDatabase.child("userslist").child(txtuser).child("username").setValue(txtuser, new DatabaseReference.CompletionListener() {
                                        public void onComplete(DatabaseError error, DatabaseReference ref) {
                                            mDatabase.child("userslist").child(txtuser).child("password").setValue(txtpw, new DatabaseReference.CompletionListener() {
                                                public void onComplete(DatabaseError error, DatabaseReference ref) {
                                                    mDatabase.child("userslist").child(txtuser).child("type").setValue(type, new DatabaseReference.CompletionListener() {
                                                        public void onComplete(DatabaseError error, DatabaseReference ref) {
                                                            mDatabase.child("userslist").child(txtuser).child("hosid").setValue(regHosid, new DatabaseReference.CompletionListener() {
                                                                public void onComplete(DatabaseError error, DatabaseReference ref) {
                                                                    Toast.makeText(Register.this, "Registered", Toast.LENGTH_SHORT).show();
                                                                    finish();
                                                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                } catch (DatabaseException ex) {
                                    Toast.makeText(Register.this, "Use Alpha-Numericals only", Toast.LENGTH_SHORT).show();
                                    Log.d("Database error",ex.getMessage());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            Log.d("OnCancelledLog", "loadPost:onCancelled", databaseError.toException());
                            // ...
                        }
                    };
                    mDatabase_user.addValueEventListener(postListener_user);
                } catch (DatabaseException e) {
                    Toast.makeText(this, "Use Alpha-Numericals only", Toast.LENGTH_SHORT).show();
                    Log.d("Database error",e.getMessage());
                } catch (NullPointerException e) {
                    try {
                        mDatabase.child("userslist").child(txtuser).child("username").setValue(txtuser, new DatabaseReference.CompletionListener() {
                            public void onComplete(DatabaseError error, DatabaseReference ref) {
                                mDatabase.child("userslist").child(txtuser).child("password").setValue(txtpw, new DatabaseReference.CompletionListener() {
                                    public void onComplete(DatabaseError error, DatabaseReference ref) {
                                        mDatabase.child("userslist").child(txtuser).child("type").setValue(type, new DatabaseReference.CompletionListener() {
                                            public void onComplete(DatabaseError error, DatabaseReference ref) {
                                                mDatabase.child("userslist").child(txtuser).child("hosid").setValue(hosid, new DatabaseReference.CompletionListener() {
                                                    public void onComplete(DatabaseError error, DatabaseReference ref) {
                                                        Toast.makeText(Register.this, "Registered", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    } catch (DatabaseException ex) {
                        Toast.makeText(Register.this, "Use Alpha-Numericals only", Toast.LENGTH_SHORT).show();
                        Log.d("Database error",ex.getMessage());
                    }
                }
            }
        }
    }
    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(Register.this);
        builder1.setMessage("Cancel Registration?");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Register.this, MainActivity.class));
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder1.show();
    }
}
