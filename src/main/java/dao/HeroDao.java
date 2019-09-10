package dao;

import models.Hero;
import java.util.List;

public interface HeroDao {



    List<Hero> getAll();


    void add(Hero task);


    Hero findById(int id);


    void update(int id, String content);


    void deleteById(int id);
    void clearAllHeros();
}
