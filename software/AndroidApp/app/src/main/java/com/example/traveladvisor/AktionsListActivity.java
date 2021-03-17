package com.example.traveladvisor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.ListFragment;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.traveladvisor.adapter.AktionListAdapter;
import com.example.traveladvisor.bll.Aktion;
import com.example.traveladvisor.bll.Location;
import com.example.traveladvisor.dal.DatabaseManager;

import java.util.ArrayList;

public class AktionsListActivity extends AppCompatActivity {

    private ListView list_view;

    private Location location;
    private ArrayList<Aktion> aktionen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aktions_list);
        Toast.makeText(getApplicationContext(),"AktionsListe",Toast.LENGTH_SHORT).show();

        location = (Location) getIntent().getExtras().get("selectedLocation");

        list_view = findViewById(R.id.list_view);
        list_view.setEmptyView(findViewById(R.id.empty));

        try {
            aktionen = DatabaseManager.newInstance().getAktionenFromLocation(location);
            aktionen = getActiveAktionen(aktionen);
            AktionListAdapter adapter = new AktionListAdapter(this, aktionen);
            list_view.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private ArrayList<Aktion> getActiveAktionen(ArrayList<Aktion> aktionen) {
        ArrayList<Aktion> result = new ArrayList<>();
        for (Aktion a :aktionen ) {
            if(a.isAktiv())
                result.add(a);
        }
        return result;
    }
}
