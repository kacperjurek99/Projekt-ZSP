import java.awt.*;

/**
 
 * 0 - nie płąnące
 * 1 - płonące
 * 2 - puste/wypalone
 * 3-  woda
 */

public class FireSimulator {
    private int rows;
    private int columns;
    private Terrain terrain;

    /**
     * Inicjuje wartości klas i tworzy teren z określonymi wartościami, a następnie inicjuje suchość i rysuje rzekę
     *
     * @param c  - kolumny
     * @param r  - wiersze
     * @param wS - predkoscwiatru
     * @param wD - kierunek wiatru
     * @param dL - poziom wysuszenia gleby
     */
    public FireSimulator(int c, int r, double wS, double wD, double dL) {
        this.rows = r;
        this.columns = c;
        this.terrain = new Terrain(c, r, wD, wS);
        this.terrain.initalizeDryness(dL);
        this.terrain.drawRiver();


    }

    /**
     * randomowo zaczyna ogien, ale jest opcja rysowania wiec nie uzywam tego
     */
    public void startFire() {
        int randCol = (int) (Math.random() * this.columns);
        int randRow = (int) (Math.random() * this.rows);
        this.terrain.setTempStatus(randCol, randRow, 1);
        terrain.update();
    }

    /***
     *rozprzestrzenia ogień, biorąc pod uwagę aktualną suchość komórek, prędkość wiatru i stan otaczających komórek
     */
    public void spread() {

        double xSpeed = terrain.getXSpeed();
        double ySpeed = terrain.getYSpeed();
        for (int c = 0; c < this.columns; c++) {
            for (int r = 0; r < this.rows; r++) {
                int status = terrain.getStatus(c, r);
                int nStatus;
                int neStatus;
                int eStatus;
                int seStatus;
                int sStatus;
                int wStatus;
                int swStatus;
                int nwStatus;

                if (r > this.rows - 2) nStatus = 0;
                else nStatus = terrain.getStatus(c, r + 1);
                if (r < 1) sStatus = 0;
                else sStatus = terrain.getStatus(c, r - 1);
                if (c > this.columns - 2) eStatus = 0;
                else eStatus = terrain.getStatus(c + 1, r);
                if (c < 1) wStatus = 0;
                else wStatus = terrain.getStatus(c - 1, r);

                if (r > this.rows - 2 || c < 1) nwStatus = 0;
                else nwStatus = terrain.getStatus(c - 1, r + 1);
                if (r > this.rows - 2 || c > this.columns - 2) neStatus = 0;
                else neStatus = terrain.getStatus(c + 1, r + 1);
                if (r < 1 || c > this.columns - 2) seStatus = 0;
                else seStatus = terrain.getStatus(c + 1, r - 1);
                if (r < 1 || c < 1) swStatus = 0;
                else swStatus = terrain.getStatus(c - 1, r - 1);

                if (status == 2) terrain.setTempStatus(c, r, 2);
                if (status == 1) terrain.setTempStatus(c, r, 2);
                //sprawdzenie ze przynajmniej jedna komorka jest w ogniu
                if (status == 0 && (nStatus == 1 || eStatus == 1 || sStatus == 1 || wStatus == 1 ||
                        nwStatus == 1 || neStatus == 1 || seStatus == 1 || swStatus == 1)) {

                    double[] wStats = {nStatus, neStatus, eStatus, seStatus, sStatus, swStatus, wStatus, nwStatus};
                    //zapewnia, że ​​wartości, które się nie palą, nie wpływają na sumę

                    for (int i = 0; i < wStats.length; i++) {
                        if (wStats[i] != 1) {
                            wStats[i] = 0;
                        }

                    }

                    int speedFactor = 1;
                    //dodaje prędkość wiatru do jednej strony i odejmuje od drugiej
                    if (xSpeed > 0) {
                        int[] toAdd = {5, 6, 7};
                        int[] toSubtract = {1, 2, 3};
                        for (int i = 0; i < toAdd.length; i++) {
                            int a = toAdd[i];
                            int s = toSubtract[i];

                            if (wStats[a] == 1) {
                                wStats[a] += speedFactor * xSpeed;
                            }
                            if (wStats[s] == 1) {
                                wStats[s] -= speedFactor * xSpeed;
                            }

                        }
                    }

                    if (xSpeed < 0) {
                        int[] toAdd = {1, 2, 3};
                        int[] toSubtract = {5, 6, 7};
                        for (int i = 0; i < toAdd.length; i++) {
                            int a = toAdd[i];
                            int s = toSubtract[i];

                            if (wStats[a] == 1) {
                                wStats[a] += speedFactor * xSpeed;
                            }
                            if (wStats[s] == 1) {
                                wStats[s] -= speedFactor * xSpeed;
                            }

                        }
                    }

                    if (ySpeed > 0) {
                        int[] toAdd = {5, 4, 3};
                        int[] toSubtract = {7, 0, 1};
                        for (int i = 0; i < toAdd.length; i++) {
                            int a = toAdd[i];
                            int s = toSubtract[i];

                            if (wStats[a] == 1) {
                                wStats[a] += speedFactor * ySpeed;
                            }
                            if (wStats[s] == 1) {
                                wStats[s] -= speedFactor * ySpeed;
                            }

                        }
                    }

                    if (ySpeed < 0) {
                        int[] toAdd = {7, 0, 1};
                        int[] toSubtract = {5, 4, 3};
                        for (int i = 0; i < toAdd.length; i++) {
                            int a = toAdd[i];
                            int s = toSubtract[i];

                            if (wStats[a] == 1) {
                                wStats[a] += speedFactor * ySpeed;
                            }
                            if (wStats[s] == 1) {
                                wStats[s] -= speedFactor * ySpeed;
                            }

                        }
                    }

                    double sum = 0;

                    //sprawia, że ​​komórki narożne liczą się mniej
                    for (int i = 1; i < wStats.length; i += 2) {
                        wStats[i] = wStats[i] / 2.0;
                    }

                    for (int i = 0; i < wStats.length; i++) {
                        sum += wStats[i];
                    }

                    int tempStatus = 0;
                    double tempDryness;
                    double currentDry = terrain.getTempDry(c, r);
                    tempDryness = currentDry * (1 + (sum));

                    if (tempDryness > 10) {
                        tempDryness = 10;
                    }
                    double prob = (sum / 4);
                    prob += (prob) * (tempDryness / 12);


                    if (Math.random() < prob) {
                        tempStatus = 1;
                    }


                    terrain.setTempStatus(c, r, tempStatus);
                    if (tempStatus == 0) {
                        terrain.setTempDryness(c, r, tempDryness);
                    }

                }


            }


        }
        //ustawianie wszystkich na siatki temp

        terrain.update();

    }

    /***
     * coblicza liczbę spalonych komórek i dzieli przez całkowitą liczbę komórek bez wody, a następnie mnoży przez 100, aby przekształcić w procent
     
     */
    public double getPercentBurned() {
        int numBurned = 0;
        int numWater = 0;
        for (int c = 0; c < this.columns; c++) {
            for (int r = 0; r < this.rows; r++) {
                if (terrain.getStatus(c, r) == 2) {
                    numBurned += 1;
                }
                if (terrain.getStatus(c, r) == 3) {
                    numWater += 1;
                }
            }
        }
        double percent = 100 * ((double) numBurned / ((double) (this.columns * this.rows) - (double) numWater));
        return percent;
    }


}

