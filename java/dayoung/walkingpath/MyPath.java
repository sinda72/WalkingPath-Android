package dayoung.walkingpath;

public class MyPath {
    private int trafficType;

    public MyPath(int r_path){
        this.trafficType = r_path;
        //this.r_time = r_time;
    }

    public int getTrafficType(){
        return trafficType;
    }
    public void setTrafficType(int trafficType){
        this.trafficType = trafficType;
    }
}
