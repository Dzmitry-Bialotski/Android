package by.belotskiy.battleship.statistics;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import by.belotskiy.battleship.R;

public class StatisticsAdapter extends ArrayAdapter<StatisticsItem> {
    private final String WIN = "Win";
    private final String LOSE = "Lose";
    private List<StatisticsItem> stats;
    private int layout;
    private LayoutInflater inflater;
    public StatisticsAdapter(Context context, int resource, List<StatisticsItem> actions) {
        super(context, resource, actions);
        this.stats = actions;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }
    public View getView(int position, View convertView, ViewGroup parent) {

        View view=inflater.inflate(this.layout, parent, false);

        TextView result = (TextView) view.findViewById(R.id.game_result_text_view);
        TextView time = (TextView) view.findViewById(R.id.game_time_text_view);
        TextView opponentUserName = (TextView)view.findViewById(R.id.opponent_text_view);
        StatisticsItem statisticsItem = stats.get(position);

        result.setText(statisticsItem.getResult());
        if (statisticsItem.getResult().equals(WIN)){
            result.setTextColor(Color.GREEN);
        }
        else if(statisticsItem.getResult().equals(LOSE)){
            result.setTextColor(Color.RED);
        }
        time.setText("time : " + statisticsItem.getTime());

        opponentUserName.setText("opponent : " + statisticsItem.getOpponentUsername());

        return view;
    }
}

