package dayoung.walkingpath;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Step {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String final_step;

    public Step(String final_step) {
        this.final_step = final_step;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFinal_step() {
        return final_step;
    }

    public void setFinal_step(String final_step) {
        this.final_step = final_step;
    }

    @Override
    public String toString() {
        return final_step;
    }
}
