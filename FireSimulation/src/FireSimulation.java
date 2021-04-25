public class FireSimulation {
    private static int x;
    private static int y;
    private static int time;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Height is not specified. Using default");
            x = 1000;
            System.out.println("Width is not specified. Using default");
            y = 700;
            System.out.println("Time is not specified. Using default");
            time = 250;
        } else {

            try {
                x = Integer.parseInt(args[0]);
            } catch (Exception e) {
                System.out.println("Invalid input.");
                x = 1000;
            }
            if (args.length < 2) {
                System.out.println("Width is not specified. Using default");
                y = 700;
                System.out.println("Time is not specified. Using default");
                time = 250;
            } else {

                try {
                    y = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    System.out.println("Invalid input.");
                    y = 500;

                }
                if (args.length == 2) {
                    System.out.println("Time is not specified. Using default");
                    time = 100;
                } else {

                    try {
                        time = Integer.parseInt(args[2]);
                    } catch (Exception e) {
                        System.out.println("Invalid input.");
                        time = 100;
                    }
                }
            }
        }
        GUI gui = new GUI (x,y);
        gui.GameTimer(time);
    }
}



