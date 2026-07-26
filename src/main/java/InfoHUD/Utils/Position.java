package InfoHUD.Utils;

public class Position {

    private int x;
    private int y;
    private final int defaultX;
    private final int defaultY;
    private boolean isEdit = false;

    public Position(int defaultX, int defaultY) {
        this.defaultX = defaultX;
        this.defaultY = defaultY;
        this.x = defaultX;
        this.y = defaultY;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setEdit() {
        this.isEdit = true;
    }

    public void resetToDefault() {
        this.x = defaultX;
        this.y = defaultY;
        this.isEdit = false;
    }

    public boolean isEdit() {
        return isEdit;
    }
}
