package models;

import java.util.Objects;

public class Hero {


    private int id;
    private String name;
    private int age;
    private String specialPower;
    private String weakness;
    private int squadId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSpecialPower() {
        return specialPower;
    }

    public void setSpecialPower(String specialPower) {
        this.specialPower = specialPower;
    }

    public String getWeakness() {
        return weakness;
    }

    public void setWeakness(String weakness) {
        this.weakness = weakness;
    }

    public int getSquadId() {
        return squadId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hero hero = (Hero) o;
        return getAge() == hero.getAge() &&
                getSquadId() == hero.getSquadId() &&
                getName().equals(hero.getName()) &&
                getSpecialPower().equals(hero.getSpecialPower()) &&
                getWeakness().equals(hero.getWeakness());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getAge(), getSpecialPower(), getWeakness(), getSquadId());
    }

    public void setSquadId(int squadId) {
        this.squadId = squadId;
    }

    public Hero(String name, int age, String specialPower, String weakness, int squadId) {
        this.name = name;
        this.age = age;
        this.specialPower = specialPower;
        this.weakness = weakness;
        this.squadId = squadId;
    }
}



