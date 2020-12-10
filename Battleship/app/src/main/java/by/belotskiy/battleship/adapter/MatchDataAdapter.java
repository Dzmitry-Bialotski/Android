package by.belotskiy.battleship.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import by.belotskiy.battleship.R;

import java.util.ArrayList;

import by.belotskiy.battleship.data.MatchData;

public class MatchDataAdapter extends ArrayAdapter<MatchData> {
    private Context context;
    private ArrayList<MatchData> list;
    public MatchDataAdapter(Context context, ArrayList<MatchData> list)
    {
        super(context, 0, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.history_item, parent, false);

        MatchData currentNote = list.get(position);

        TextView titleText = (TextView)listItem.findViewById(R.id.dateText);
        titleText.setText(currentNote.getDate());

        TextView bodyText = (TextView)listItem.findViewById(R.id.resultText);
        bodyText.setText(currentNote.getResult());

        TextView tagsText = (TextView)listItem.findViewById(R.id.shipsLeft);
        tagsText.setText("Ships remain: " + currentNote.getShipRemains().toString());

        return listItem;
    }
}
