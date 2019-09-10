package dao;

import models.Squad;
import java.util.List;

public interface SquadDao {



    List<Squad> getAll();


    void add(Squad task);


    Squad findById(int id);


    void update(int id, String content);


    void deleteById(int id);

    void clearAllSquads();
}
