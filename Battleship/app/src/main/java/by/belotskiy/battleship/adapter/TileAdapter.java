package by.belotskiy.battleship.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import by.belotskiy.battleship.R;

import by.belotskiy.battleship.game_logic.Board;
import by.belotskiy.battleship.game_logic.Tile;

public class TileAdapter extends BaseAdapter {
    private Context context;
    private Board board;
    private int tileSize;

    private Integer[] thumbIds = {
            R.drawable.none, R.drawable.ship, R.drawable.hit, R.drawable.sunk, R.drawable.miss
    };


    public TileAdapter(Context context, Board board, int tileSize){
        this.context = context;
        this.board = board;
        this.tileSize = tileSize;
    }


    @Override
    public int getCount() {
        return board.getTotalBoardSize();
    }

    @Override
    public Object getItem(int position) {
        int row = position / board.getBoardSize();
        int col = position % board.getBoardSize();

        return board.getTile(row, col);
    }

    @Override
    public long getItemId(int position) {
        return position / board.getBoardSize();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int row = position / board.getBoardSize();
        int col = position % board.getBoardSize();

        final ImageView imageView;

        if (convertView == null || position ==0) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(tileSize, tileSize));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 0, 0, 0);

        } else {
            imageView = (ImageView) convertView;
        }

        Tile.Status status = board.getTile(row, col).getTileStatus();

        switch (status) {
            case NONE:
            case NONE_X:
                imageView.setImageResource(thumbIds[0]);
                break;
            case HIT:
                imageView.setBackgroundResource(thumbIds[2]);
                board.getTile(row,col).setFired(true);
                break;
            case MISS:
                imageView.setBackgroundResource(thumbIds[4]);
                board.getTile(row,col).setFired(true);
                break;
            case SHIP:
                if (parent.getId() == R.id.userGrid)
                    imageView.setImageResource(thumbIds[1]);
                if (parent.getId() == R.id.enemyGrid)
                    imageView.setImageResource(thumbIds[0]);
                break;
            case SUNK:
                imageView.setBackgroundResource(thumbIds[3]);
                board.getTile(row,col).setIsSunk(true);
                break;
        }

        return imageView;
    }
}
