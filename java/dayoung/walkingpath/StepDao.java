package dayoung.walkingpath;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface StepDao {

        @Query("SELECT * FROM Step")
        List<Step> getAll();

        @Insert
        void insert(Step step);

        @Update
        void update(Step step);

        @Delete
        void delete(Step step);

        @Query("DELETE FROM Step")
        void deleteall();

        @Query("SELECT final_step FROM step")
        String getdata();

}
