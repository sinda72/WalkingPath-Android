package dayoung.walkingpath;

public class PathData {
    //private String r_path;//추천 경로 번호
    private String r_time;//걸리는 시간
    private String r_cal;
    private String r_walk;
    private String r_path;
    private String r_road;


    public PathData(String r_time, String r_cal, String r_walk, String r_path, String r_road){
        this.r_time = r_time;
        this.r_cal = r_cal;
        this.r_walk = r_walk;
        this.r_path = r_path;
        this.r_road = r_road;
    }
    public String getCal()
    {
        return this.r_cal;
    }

    public String getWalk() { return this.r_walk; }
    public String getPath() { return this.r_path; }
    public String getTime()
    {
        return this.r_time;
    }
    public String getRoad()
    {
        return this.r_road;
    }


}