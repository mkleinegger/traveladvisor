package com.example.traveladvisor.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.traveladvisor.R;
import com.example.traveladvisor.bll.Aktion;
import com.example.traveladvisor.dal.DatabaseManager;

import java.util.ArrayList;

public class AktionListAdapter extends ArrayAdapter<Aktion> {


    public AktionListAdapter(Context context, ArrayList<Aktion> aktionen) {
        super(context, 0, aktionen);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Aktion aktion = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.aktion_list_items, parent, false);
        }

        TextView tvBezeichnung = (TextView) convertView.findViewById(R.id.tvBezeichnung);
        TextView tvPunkte = (TextView) convertView.findViewById(R.id.tvPunkte);
        Button btnEinloesen = convertView.findViewById((R.id.button_Aktion_Einsloesen));

        tvBezeichnung.setText(aktion.getBezeichnung());
        tvPunkte.setText("Punkte: " + aktion.getPunkte());

        btnEinloesen.setTag(aktion);
        btnEinloesen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Aktion aktion = (Aktion) view.getTag();
                int aktionsPunkte = aktion.getPunkte();
                Toast.makeText(getContext(), "Einlösen", Toast.LENGTH_SHORT).show();
                try {
                    int currentPoints = DatabaseManager.newInstance().getCurrentAmountOfPoints();
                    if (aktionsPunkte > currentPoints) {
                        Toast.makeText(getContext(), "Ihnen fehlen " + (aktionsPunkte - currentPoints) + " Punkte um die Prämie einzulösen.", Toast.LENGTH_LONG).show();
                    } else {
                        String result = DatabaseManager.newInstance().userRedeemAktion(aktion);
                        if (result.split(",")[0].equals("Successful")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                            builder.setMessage("Sie haben die Aktion " + aktion.getBezeichnung() + " eingelöst. Zeigen Sie die Nachricht an der Kassa." +
                                    "\n\nSobald sie das Fenster schließen können Sie nicht wieder auf die eingelöste Aktion zugreifen." +
                                    "\n\nNeuer Punktestand: " + (currentPoints-aktionsPunkte))
                                    .setTitle(Html.fromHtml("<font color='"+getContext().getResources().getColor(R.color.colorPrimary)+"'>Aktion eingelöst</font>"));

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
                            Toast.makeText(getContext(), "Ein Fehler ist aufgetreten: " + result, Toast.LENGTH_LONG).show();
                            Log.e("AktionListAdapter",result);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //einlösen Route noch nicht vorhanden
            }
        });

        return convertView;
    }
}
