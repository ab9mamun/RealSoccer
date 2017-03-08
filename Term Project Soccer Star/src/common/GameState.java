package common;

public enum GameState {
    MyTurn, YourTurn, IWon, ILost, Steady;
    private static int serverGoal=0, clientGoal=0;
    private static boolean connectionEstablished = false;
    static final double collisionFactor = 0.9;
    private static int errorCount = 0;
    private static final int ERROR_MAX = 100;
    private static int duration = 3;

    public static int getDuration() {
        return duration;
    }

    public static void setDuration(int duration) {
        GameState.duration = duration;
        System.out.println(duration);
    }

    public static int getServerGoal() {
        return serverGoal;
    }

    public static void addServerGoal() {
        serverGoal++;
    }

    public static int getClientGoal() {
        return clientGoal;
    }

    public static void setConnectionEstablished(boolean connectionEstablished) {
        GameState.connectionEstablished = connectionEstablished;
    }

    public static boolean isConnectionEstablished() {
        return connectionEstablished;
    }

    public static void addClientGoal() {
        clientGoal++;
    }
    public static void addErrorCount(){
        errorCount++;
    }
    public static void addErrorCount(int n){
        errorCount+= n;
    }

    public static void valuateInt(String s){
        if(s==null) return;

        setDuration(Integer.parseInt(s.substring(0, s.indexOf(" ",-1))));
    }

    public static boolean errorTooMuch() {
        return errorCount>=ERROR_MAX;
    }

    public static void reset(){
        serverGoal = 0;
        clientGoal = 0;
    }
}