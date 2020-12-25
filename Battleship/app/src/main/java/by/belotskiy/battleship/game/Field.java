package by.belotskiy.battleship.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import javax.annotation.Nullable;

import by.belotskiy.battleship.game.enums.CellType;
import by.belotskiy.battleship.game.enums.FieldType;
import by.belotskiy.battleship.game.service.FleetService;
import by.belotskiy.battleship.game.util.FleetGenerator;

public class Field extends View {
    private final int size = 10;
    private float cellSize;
    private Paint blackPaint_20;
    private Paint blackPaint_10;
    private Fleet fleet;
    private FieldType fieldType;
    private CellType[][] cells;
    private Cell currentCell;

    private boolean FleetIsCreated = false;
    public boolean isAlreadyChecked;
    public boolean shipIsSunk;
    public Ship lastSunkShip;
    public Field(Context context, @Nullable AttributeSet attrs){
        super(context, attrs);
        cellSize = getWidth()/getSize();
        fieldType = FieldType.CREATE_FIELD;
        cells = new CellType[size][size];
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                cells[i][j] = CellType.EMPTY;
            }
        }
        blackPaint_20 = new Paint();
        blackPaint_20.setColor(Color.BLACK);
        blackPaint_20.setStrokeWidth(20);
        blackPaint_20.setStyle(Paint.Style.STROKE);
        blackPaint_10= new Paint();
        blackPaint_10.setColor(Color.BLACK);
        blackPaint_10.setStrokeWidth(10);
        blackPaint_10.setStyle(Paint.Style.STROKE);
    }
    public void generateFleet(){
        cells = new FleetGenerator(size).generateCells();
    }
    public int getSize() {
        return size;
    }

    public Fleet getFleet() {
        if(FleetIsCreated == false){
            FleetIsCreated = true;
            fleet = new FleetService().createFleet(this);
        }
        return fleet;
    }

    public void setFleet(Fleet fleet) {
        this.fleet = fleet;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
        invalidate();
    }

    public CellType[][] getCells() {
        return cells;
    }
    public void setCell(int i, int j, CellType cellType) {
        this.cells[i][j] = cellType;
        invalidate();
    }

    public void setCells(CellType[][] cells) {
        this.cells = cells;
        invalidate();
    }

    public Cell getCurrentCell() {
        return currentCell;
    }

    public void setCurrentCell(Cell currentCell) {
        this.currentCell = currentCell;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int weight, int height, int old_weight, int old_height) {
        super.onSizeChanged(weight, height, old_weight, old_height);
        cellSize = getHeight() / size;
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //border of field
        canvas.drawRect(0,getHeight(),getWidth(),0, blackPaint_20);

        for (int i = 0; i < size; i++) {
            //border of cells
            canvas.drawLine(i*cellSize, 0, i*cellSize, getHeight(), blackPaint_10);
            canvas.drawLine(0, i*cellSize, getHeight(), i*cellSize, blackPaint_10);
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                switch (cells[i][j]){
                    case SUNK:
                        DrawSunk(canvas, i, j);
                        break;
                    case MISS:
                        DrawMiss(canvas, i, j);
                        break;
                    case SHIP:
                        if(fieldType==FieldType.MY_FIELD || fieldType == FieldType.CREATE_FIELD){
                            DrawShip(canvas, i, j);
                        }
                        break;
                }
            }
        }
    }
    private void DrawSunk(Canvas canvas, int i, int j){
        canvas.drawLine(j * cellSize, i * cellSize,(j + 1) * cellSize,(i + 1) * cellSize, blackPaint_10);
        canvas.drawLine((j+1) * cellSize, i * cellSize,j * cellSize,(i + 1) * cellSize, blackPaint_10);
    }

    private void DrawShip(Canvas canvas, int i, int j){
        Paint blackPaint = new Paint();
        blackPaint.setColor(Color.BLACK);
        blackPaint.setStrokeWidth(10);
        canvas.drawRect(j * cellSize, i * cellSize, (j+1)* cellSize, (i+1)*cellSize, blackPaint);

    }

    private void DrawMiss(Canvas canvas, int i, int j){
        canvas.drawPoint(j * cellSize + cellSize/2, i * cellSize + cellSize/2, blackPaint_20);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            int j = (int) (event.getX() / cellSize);
            int i = (int) (event.getY() / cellSize);
            i = Math.max(i, 0);
            i = Math.min(i, 9);
            j = Math.max(j, 0);
            j = Math.min(j, 9);
            switch (fieldType) {
                case MY_FIELD:
                case BLOCKED:
                    break;
                case OPPONENT_FIELD:
                    isAlreadyChecked = (cells[i][j] == CellType.MISS) || (cells[i][j] == CellType.SUNK);
                    if(isAlreadyChecked)
                        break;
                    if (cells[i][j] == CellType.SHIP){
                        cells[i][j] = CellType.SUNK;
                        FleetService fleetService = new FleetService();
                        Ship ship = fleetService.findShip(i, j, this.getFleet());
                        ship.setCell(new Cell(i,j,CellType.SUNK));
                        if(fleetService.shipIsSunk(ship)){
                            drawMissAroundSunkShip(ship, this);
                            shipIsSunk = true;
                            lastSunkShip = ship;
                        }else{
                            shipIsSunk = false;
                        }
                    } else if (cells[i][j] == CellType.EMPTY){
                        cells[i][j] = CellType.MISS;
                        shipIsSunk = false;
                    }
                    break;
                case CREATE_FIELD: {
                    if (cells[i][j] == CellType.SHIP){
                        cells[i][j] = CellType.EMPTY;
                    } else if(cells[i][j] == CellType.EMPTY){
                        cells[i][j] = CellType.SHIP;
                    }
                    break;
                }
            }
            currentCell = new Cell(i, j, cells[i][j]);
            invalidate();
        }
        return true;
    }
    private void drawMissAroundSunkShip(Ship ship, Field field){
        FleetService fleetService = new FleetService();
        ArrayList<Cell> cells = fleetService.findBorderCells(ship, this);
        for(Cell cell : cells){
            cell.setCellType(CellType.MISS);
            field.setCell(cell.getX(), cell.getY(),cell.getCellType());
        }
    }
    private OnClickListener onClickListener;
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_UP &&
                (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            if(onClickListener != null) onClickListener.onClick(this);
        }
        return super.dispatchKeyEvent(event);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            setPressed(true);
        }
        else if(event.getAction() == MotionEvent.ACTION_UP) {
            if(onClickListener != null) onClickListener.onClick(this);
            setPressed(false);
        }
        else {
            setPressed(false);
        }
        return super.dispatchTouchEvent(event);
    }
    @Override
    public void setOnClickListener(OnClickListener l) {
        onClickListener = l;
    }
}
