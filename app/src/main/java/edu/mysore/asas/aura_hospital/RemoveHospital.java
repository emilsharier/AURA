package edu.mysore.asas.aura_hospital;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RemoveHospital extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_hospital);

        listView=findViewById(R.id.listHospitalsR);
        mDatabase=FirebaseDatabase.getInstance().getReferenceFromUrl("https://aura-9fb32.firebaseio.com/hospitalslist/AllHospitals");
        FirebaseListAdapter<String> firebaseListAdapter=new FirebaseListAdapter<String>(this,String.class,android.R.layout.simple_list_item_1,mDatabase) {
            @Override
            protected void populateView(View v, String value, int position)
            {
                TextView textView1=v.findViewById(android.R.id.text1);
                textView1.setText(getItem(position).toString());
            }
        };
        listView.setAdapter(firebaseListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(RemoveHospital.this, RemoveHosDet.class);
                intent.putExtra("hosid", listView.getItemAtPosition(position).toString());
                startActivity(intent);
            }
        });
    }
}
