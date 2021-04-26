import java.awt.*;



public class Terrain {

    private int rows;
    private int columns;
    private int[][] grid;
    private double[][] dryGrid;
    private int[][] tempGrid;
    private double[][] tempDryGrid;
    private double windDirection;
    private double windSpeed;
    private double xSpeed;
    private double ySpeed;
    private int n;

    
    public Terrain(int c, int r, double wD, double wS) {
        this.rows = r;
        this.columns = c;
        this.grid = new int[c][r];
        this.tempGrid = new int[c][r];
        this.dryGrid = new double[c][r];
        this.tempDryGrid = new double[c][r];
        this.windDirection = wD*(Math.PI/180); //converting degrees to radians
        this.windSpeed = wS;
        this.xSpeed = windSpeed*Math.cos(windDirection);
        this.ySpeed = windSpeed*Math.sin(windDirection);
        this.n = 0;
        StdDraw.setCanvasSize(500,500);
        StdDraw.setXscale(0, this.columns);
        StdDraw.setYscale(0, this.rows);
    }

    /***
     * Updates cell colors based on temp status and updates real status
     * If cell is burt a margin is applied to prevent visible grid lines
     * If cell is still forest will set color to a shade of green based on dryness level
     */
    public void update(){
        this.n += 1;
        if(this.n == 1){
            //setting color green
            StdDraw.setPenColor(10,110,0);
            double[] xVals = {0,0,this.columns,this.columns}; double[] yVals = {0,this.rows,this.rows,0};
            StdDraw.filledPolygon(xVals,yVals);
        }

        double xMargin = 0; double yMargin = 0;
        for (int c = 0; c < this.columns; c++) {
            for (int r = 0; r < this.rows; r++) {
                if(StdDraw.mousePressed()) {
                    StdDraw.setPenColor(Color.red);
                    //while user is pressing mouse set cell under cursor on fire
                    while (StdDraw.mousePressed()) {
                        int mouseX = (int) StdDraw.mouseX();
                        int mouseY = (int) StdDraw.mouseY();
                        if (mouseX >= 0 && mouseX < this.columns && mouseY >= 0 && mouseY < this.rows) {
                            if(getStatus(mouseX, mouseY) == 0) {
                                setTempStatus(mouseX, mouseY, 1);

                                double[] xVals = {mouseX,mouseX,mouseX+.99,mouseX+.99};
                                double[] yVals = {mouseY,mouseY+1,mouseY+1,mouseY};
                                StdDraw.filledPolygon(xVals,yVals);
                            }
                        }
                    }
                }
                if((this.grid[c][r] != this.tempGrid[c][r] || this.dryGrid[c][r] != this.tempDryGrid[c][r])){
                    int tempStatus = this.tempGrid[c][r];
                    this.grid[c][r] = tempStatus;
                    if(tempStatus == 2){
                        //xMargin = -(1/(double)this.columns)*3;
                        //yMargin = -(1/(double)this.rows)*3;
                        //just trying something
                        xMargin = 0;
                        yMargin = 0;
                    }

                    double[] xVals = {(c+xMargin),(c+xMargin),(c+1-xMargin),(c+1-xMargin)};
                    double[] yVals = {(r+yMargin),(r+1-yMargin),(r+1-yMargin),(r+yMargin)};

                    xMargin = 0; yMargin = 0;

                    if(this.grid[c][r] == 0){
                        double tempDryness = this.tempDryGrid[c][r];
                        this.dryGrid[c][r] = tempDryness;
                        int newR = 10 + (int)(((double)tempDryness/10)*200);
                        int newG = 110 + (int)(((double)tempDryness/10)*120);
                        int newB = (int)(((double)tempDryness/10)*130);
                        StdDraw.setPenColor(newR,newG,newB);
                    }
                    else if(this.grid[c][r] == 1){
                        StdDraw.setPenColor(Color.RED);
                    }
                    else if(this.grid[c][r] == 3){
                        StdDraw.setPenColor(Color.BLUE);
                    }
                    else{
                        StdDraw.setPenColor(Color.BLACK);
                    }
                    StdDraw.filledPolygon(xVals,yVals);

                }


            }

        }
        return;
    }

    /***
     * Draws a river
     * Chooses random starting position on the x axis
     * Will find random points and connect with water to give appearance of random river
     */
    public void drawRiver() {
        int xStart = (int)((Math.random()*(double)columns/2)+(double)columns/4);
        int numPoints = 20;
        int[][] points = new int[numPoints][2];
        points[0] = new int[]{xStart, 0};

        //generating array of random points
        for (int i = 1; i < points.length; i++) {
            int dX = 3-(int)(Math.random()*8);
            //System.out.println(dX);
            int dY = 1+(int)Math.ceil((double)rows/ (double)points.length);
            points[i][0] = points[i-1][0] + dX;
            points[i][1] = points[i-1][1] + dY;
            int x = points[i][0]; int y = points[i][1];
            if(x >= 0 && x < columns && y>=0 && y < rows) setTempStatus(x,y,3);
        }
        //connecting the dots
        int thickness = 2;
        for (int i = 1; i < points.length; i++) {
            int x1 = points[i-1][0]; int y1 = points[i-1][1]; int x2 = points[i][0]; int y2 = points[i][1];
            double slope = (double)(y2-y1)/(double)(x2-x1);
            double b = y1-(slope*(double)x1);
            int xMin;
            int xRange = Math.abs(x2-x1); int yRange = Math.abs(y2-y1);
            if(x1 < x2) xMin = x1;
            else xMin = x2;
            if(x1 == x2){
                for (int j = 0; j < yRange; j++) {
                    for (int k = 0; k < thickness; k++) {
                        int y = j+y1;
                        setTempStatus(x1+k,y,3);
                    }
                }
            }
            for (int j = 0; j < xRange*10; j++) {
                double tempX = ((double)j/10)+xMin;
                int y = (int)Math.round(slope*tempX+b);
                int x = (int)Math.round(tempX);
                for (int k = 0; k < thickness; k++) {
                    if(x >= 0 && x < columns && y >= 0 && y < rows) setTempStatus(x+k,y,3);
                }

            }

        }

        update();
    }

    /***
     * initialized dryness based on given dryness level
     * Will randomly select point on graph and increase the dryness of cells in a randomly chosen radius
     * The higher the dryness level the more dry
     * @param drynessLevel - between 0 and 1
     */
    public void initalizeDryness(double drynessLevel) {
        drynessLevel = drynessLevel/5;
        int radius;
        for (int c = 0; c < this.columns; c++) {
            for (int r = 0; r < this.rows; r++) {
                if (Math.random() < drynessLevel) {
                    radius = (int) (Math.random() * ((double)this.rows/5))+7;
                    for (int dX = -(radius); dX <= radius; dX++) {
                        for (int dY = -(radius - Math.abs(dX)); dY <= (radius - Math.abs(dX)); dY++) {
                            int newC = c + dX;
                            int newR = r + dY;
                            if (newC >= 0 && newC < this.columns && newR >= 0 && newR < this.rows) {
                                double currentDry = getTempDry(newC, newR);
                                int distance = Math.abs(dX)+Math.abs(dY);
                                double tempDry = (Math.random()*drynessLevel*10*((double)(radius-distance)/(double)radius));
                                tempDry += currentDry;
                                if (tempDry > 10) {
                                    tempDry = 10;
                                }
                                if (tempDry < 0 ){
                                    tempDry = 0;
                                }
                                setTempDryness((c + dX), (r + dY), tempDry);
                            }
                        }

                    }

                }
            }
        }
    }

    /***
     * Changes temporary cell status
     * @param x - column value
     * @param y - row value
     * @param status - status you want to set given cell
     */
    public void setTempStatus(int x, int y, int status){
        //checking to avoid out of bounds
        if(x >= 0 && x < columns && y >= 0 && y < rows) this.tempGrid[x][y] = status;
        return;
    }

    /***
     * Changes temp cell dryness level
     * @param x - column value
     * @param y - row value
     * @param dryness - dryness value
     */
    public void setTempDryness(int x, int y, double dryness){
        this.tempDryGrid[x][y] = dryness;
        return;
    }

    /***
     * returns x speed between 0 and 1
     * @return
     */

    public double getXSpeed(){
        return this.xSpeed;
    }

    /***
     * returns y speed between 0 and 1
     * @return
     */
    public double getYSpeed(){
        return this.ySpeed;
    }

    /***
     * returns cell status
     * @param x - column value
     * @param y - row value
     * @return
     * 0 - Not burning
     * 1 - Burning
     * 2 - empty/burnt
     * 3-Water
     */
    public int getStatus(int x, int y){
        return this.grid[x][y];
    }

    /***
     * returns cell dryness level
     * @param x - column value
     * @param y - row value
     * @return
     */
    public double getTempDry(int x, int y){
        return this.tempDryGrid[x][y];
    }
}
