package models;

import java.util.Objects;

public class Hero {


    private int id;
    private String name;
    private int age;
    private String power;
    private String weakness;
    private int squadId;
    private byte[] image;
    private String encodedString;

    public String getName() {
        return name;
    }

    public void setHero_name(String name) {
        this.name = name;
    }

    public Hero(String name, int age, String power, String weakness, int squadId, byte[] image) {
        this.name = name;
        this.age = age;
        this.power = power;
        this.weakness = weakness;
        this.squadId = squadId;
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEncodedString() {
        return encodedString;
    }

    public void setEncodedString(String encodedString) {
        this.encodedString = encodedString;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getWeakness() {
        return weakness;
    }

    public void setWeakness(String weakness) {
        this.weakness = weakness;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hero hero = (Hero) o;
        return getAge() == hero.getAge() &&
                getName().equals(hero.getName()) &&
                getPower().equals(hero.getPower()) &&
                getWeakness().equals(hero.getWeakness());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getAge(), getPower(), getWeakness());
    }

    public int getSquadId() {
        return squadId;
    }

    public void setSquadId(int squadId) {
        this.squadId = squadId;
    }

   }



