
public class Init {
    /***
     * Inicjuje ogień z określoną prędkością wiatru, kierunkiem wiatru i poziomem suchości
     * Wyświetla procent spalonego lasu, gdy ogień już się nie pali
     * @param args -ws windSpeed(0 - 1) -wd windDirection(degrees) -dl drynessLevel(0 - 1)
     */
    public static void main(String[] args){
        int columns = 100; int rows = 100; double windSpeed = .3; double windDirection = 72; double drynessLevel = .3;
        for (int i = 0; i<args.length; i++) {
            String tempArg = args[i];
            if(tempArg.equals("-h")){
                    System.err.println("Help:\n" +
                            "\t-ws windSpeed(0 - 1)\tdefault: .9\n" +
                            "\t-wd windDirection(degrees)\tdefault: 45\n" +
                            "\t-dl drynessLevel(0 - 1)\tdefault: .5\n" +
                            "\t-h help");
                    System.exit(1);
            }
            else if(tempArg.equals("-ws")){
                try {
                    double tempWS = Double.parseDouble(args[i+1]);
                    if(tempWS < 0 || tempWS > 1) throw new NumberFormatException();
                    windSpeed = tempWS;
                    i++;
                } catch (NumberFormatException e) {
                    System.err.println("Prędkość wiatru od 0 do 1");
                    System.exit(1);
                }
            }
            else if(tempArg.equals("-wd")){
                try {
                    double tempWD = Double.parseDouble(args[i+1]);
                    //if(tempWD < 0 || tempWS > 1) throw new NumberFormatException();
                    windDirection = tempWD % 360;
                    i++;
                } catch (NumberFormatException e) {
                    System.err.println("Kierunek wiatru musi być podany w stopniach");
                    System.exit(1);
                }
            }

            else if(tempArg.equals("-dl")){
                try {
                    double tempDL = Double.parseDouble(args[i+1]);
                    if(tempDL < 0 || tempDL > 1) throw new NumberFormatException();
                    drynessLevel = tempDL;
                    i++;
                } catch (NumberFormatException e) {
                    System.err.println("Poziom wysuszenia od 0 do 1");
                    System.exit(1);
                }
            }
        }
        if(args.length < 2){
            System.out.println("Argument -h dla dodatowych opcji");
        }

        FireSimulator fs = new FireSimulator(columns,rows,windSpeed,windDirection,drynessLevel);

        double lastPercent = 0;
        boolean fireStopped = false;
            while(!fireStopped){
                fs.spread();
                if(lastPercent == fs.getPercentBurned() && lastPercent > 0) fireStopped = true;
                lastPercent = fs.getPercentBurned();
                try{
                    Thread.sleep(200);
                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        System.out.println(fs.getPercentBurned()+"% spalonej powierzchni");

    }
}
