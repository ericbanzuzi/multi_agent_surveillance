package controller;

import agent.Guard;
import agent.Intruder;
import map.scenario.Scenario;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.*;

public class Simulation {
    private static final boolean DEBUG = true;

    //General variables
    public static final int ITERATIONS = 100;

    //Exploration simulation variables
    //File for exploration
    private static final String pathExplore = GameRunner.path1;
    public static final int TIME_IN_SEC = 30;
    //The next few variables determine the increase of x after each iteration
    public static final int INCREASE_SPEED = 0;
    public static final int INCREASE_NUM = 0;

    //Versus simulation variables
    //File for versus
    private static final String pathVS = GameRunner.path5;

    public static void main(String[] args) {
        exploreSim();
        //GvISim();
        System.exit(0);
    }
    private static void exploreSim(){
        GameRunner.PRINT_INFORMATION_AFTER = false;
        GameRunner.simulationDelay = 100;
        String mapD= pathExplore;
        Scenario scene  = null;
        ArrayList<Double> percents = new ArrayList<>();
        //each iteration
        for(int i = 0; i < ITERATIONS; i++){
            int n = i+1;
            if(DEBUG) System.out.println("Iteration " + n);
            // initial scene
            GameRunner game = null;
            if(scene == null){
                game = new GameRunner(mapD);
                scene = GameRunner.runner.getScene();
                GameRunner.PAUSE_AFTER_END = false;
            }
            //iterated scene with changes
            else{
                //changes
                int numGuards = scene.getNumGuards() + (i * INCREASE_NUM);
                double speedGuards = scene.getBaseSpeedGuard() + (i * INCREASE_SPEED);

                //creation
                game = new GameRunner(scene);
                GameRunner.PAUSE_AFTER_END = false;
            }

            //time constraint
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future future = executor.submit(game.getController());
            double progress = 0;
            try{
                future.get(TIME_IN_SEC, TimeUnit.SECONDS);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                game.getController().pause();
                progress = game.getController().getGameState().getProgressGuards();
                if(DEBUG) System.out.println("Explored " + progress + "%");
                percents.add(progress);
                future.cancel(true);
            } finally {
                executor.shutdown();
            }
        }
        System.out.println(percents);
        double avg = 0;
        for(double d:percents){
            avg += d;
        }
        avg /= percents.size();
        System.out.println("Average exploration percent: " + avg + "%");
        return;
    }

    /**
     * Simulation of guards vs intruders
     */
    private static void GvISim() {
        //we dont want delay in an non time based simulation
        GameRunner.simulationDelay = 0;
        GameRunner.PAUSE_AFTER_END = true;
        GameRunner.PRINT_INFORMATION_AFTER = false;
        String mapD= pathVS;

        int intruderWins = 0;
        int guardsWins = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            System.out.println("Iteration " + (i+1));
            GameRunner game = new GameRunner(mapD);
            while(!GameRunner.FINISHED){
                game.getController().run();
            }
            if(GameRunner.winner.equals("Intruder")){
                intruderWins++;
            }
            else{
                if(DEBUG) System.out.println(GameRunner.winner);
                guardsWins++;
            }
        }
        System.out.println("The guards won " + guardsWins + " rounds");
        System.out.println("The intruders won " + intruderWins + " rounds");
        GameRunner.PRINT_INFORMATION_AFTER = true;
    }


    /**
     * Welch's t-test
     * @return true or false
     */
    private static void tTest(){
        double[] data1 = Lee;
        double[] data2 = BnM;
        //calculate mean
        double mean1 = 0;
        double mean2 = 0;
        for (int i = 0; i < data1.length; i++) {
            mean1 += data1[i];
            mean2 += data2[i];
        }
        mean1 /= data1.length;
        mean2 /= data2.length;
        System.out.println("Mean1 = " + mean1);
        System.out.println("Mean2 = " + mean2);

        //calculate std
        double std1 = 0;
        double std2 = 0;
        for (int i = 0; i < data1.length; i++) {
            std1 += Math.abs(data1[i] - mean1);
            std2 += Math.abs(data2[i] - mean2);
        }
        std1 = Math.sqrt(std1/data1.length);
        std2 = Math.sqrt(std2/data2.length);

        System.out.println("Std1 = " + std1);
        System.out.println("Std2 = " + std2);

        //calc standard error squared
        double sErrSq1 = std1/Math.sqrt(data1.length);
        double sErrSq2 = std2/Math.sqrt(data2.length);

        System.out.println("sErrSq1 = " + sErrSq1);
        System.out.println("sErrSq2 = " + sErrSq2);

        //t - value
        double t  = (mean1 - mean2)/Math.sqrt(sErrSq1 + sErrSq2);
        System.out.println("T-value = " + t);

        // dof
        double v = (Math.pow(Math.sqrt(sErrSq1 + sErrSq2),4)) / ((data1.length-1)* Math.pow(sErrSq1,4) + (data2.length-1)* Math.pow(sErrSq2,4));
        System.out.println("Dof = " + v);
    }

    private static double[] HLU = new double[]{88.55499999999999, 90.705, 90.77499999999999, 90.655, 90.705, 90.945, 90.73, 90.05499999999999, 90.46499999999999, 90.79, 90.05, 88.875, 90.645, 89.86, 90.81, 90.96499999999999, 90.55, 88.985, 89.68, 89.915, 90.115, 90.495, 89.88000000000001, 90.865, 89.97, 91.175, 89.73, 89.755, 90.5, 90.01, 90.42999999999999, 90.725, 90.885, 90.31, 89.85499999999999, 91.295, 91.27499999999999, 90.145, 90.11, 90.32, 90.45, 90.96499999999999, 90.5, 89.895, 90.16499999999999, 88.99000000000001, 89.31, 89.99000000000001, 88.85499999999999, 90.995, 90.68, 89.94, 91.10000000000001, 89.45, 90.545, 90.435, 90.835, 90.55499999999999, 90.625, 90.91, 90.69500000000001, 90.12, 90.64999999999999, 90.265, 90.25999999999999, 88.595, 89.96499999999999, 90.53, 89.44, 90.445, 90.415, 90.78, 89.88000000000001, 90.495, 90.41, 90.135, 90.81, 90.985, 91.435, 89.5, 90.005, 89.385, 90.88000000000001, 90.525, 90.16499999999999, 90.4, 90.82000000000001, 90.97, 91.01, 89.57000000000001, 90.535, 90.16999999999999, 90.52, 89.77000000000001, 88.465, 90.85, 90.19, 89.425, 89.785, 90.98};
    private static double[] BnM = new double[]{6.63, 8.450000000000001, 8.05, 4.645, 5.055, 6.05, 8.325000000000001, 7.015000000000001, 9.685, 7.84, 6.67, 5.12, 10.725, 6.2700000000000005, 7.75, 7.359999999999999, 5.8999999999999995, 8.455, 8.649999999999999, 7.925, 5.665, 5.2299999999999995, 6.16, 5.605, 5.76, 7.095, 9.955, 5.9799999999999995, 8.045, 5.050000000000001, 5.79, 9.53, 7.920000000000001, 6.819999999999999, 6.6850000000000005, 9.675, 9.235, 7.315, 5.975, 10.290000000000001, 11.17, 5.935, 8.885, 4.42, 6.425, 6.325, 5.140000000000001, 7.9750000000000005, 5.425, 5.955, 6.165, 5.915, 6.550000000000001, 9.139999999999999, 8.260000000000002, 4.5249999999999995, 7.245, 6.3549999999999995, 6.995, 9.675, 11.86, 7.435, 8.36, 8.37, 6.4399999999999995, 4.390000000000001, 5.025, 8.35, 8.735, 6.734999999999999, 11.5, 6.944999999999999, 11.3, 11.110000000000001, 4.62, 4.695, 5.945, 6.935, 4.67, 4.955, 6.87, 6.645, 8.649999999999999, 4.4799999999999995, 8.695, 6.925000000000001, 5.465, 8.959999999999999, 3.855, 7.88, 4.89, 6.260000000000001, 6.915, 7.805, 8.12, 6.419999999999999, 10.16, 7.585, 9.264999999999999, 4.625};
    private static double[] Lee = new double[]{13.18, 17.08, 13.75, 15.45, 16.84, 15.49, 9.97, 12.01, 12.75, 14.98, 15.00, 14.77, 10.48, 11.87, 13.42, 14.69, 12.17, 11.72, 13.00, 14.52, 10.26, 16.21, 18.17, 16.50, 15.91, 17.34, 17.83, 16.15, 15.36, 17.58, 18.25, 18.48, 18.21, 16.30, 16.98, 16.74, 18.72, 15.78, 18.72, 17.57, 16.91, 16.46, 18.58, 16.16, 17.16, 16.33, 16.88, 15.77, 17.35, 17.37, 17.19, 18.13, 17.45, 16.92, 17.86, 18.33, 16.62, 15.87, 18.22, 16.04, 16.14, 17.69, 18.63, 16.47, 16.62, 13.83, 16.52, 13.40, 17.61, 17.72, 16.52, 17.62, 17.61, 17.64, 18.09, 16.70, 12.98, 16.48, 17.29, 18.20, 18.61, 18.07, 18.58, 16.25, 17.31, 16.57, 17.31, 16.65, 17.05, 17.69, 15.66, 18.84, 14.87, 15.75, 17.01, 16.04, 18.33, 16.24, 16.98, 17.51};

}
