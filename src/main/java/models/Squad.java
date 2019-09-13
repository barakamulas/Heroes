package models;


import java.util.Objects;

public class Squad {

    private String name;
    private String cause;
    private int size;
    private int id;

    public Squad(String name, int size, String cause) {
        this.name = name;
        this.size = size;
        this.cause = cause;
    }

    public String getName() {
        return name;
    }

    public void setSquad_name(String name) {
        this.name = name;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Squad squad = (Squad) o;
        return getSize() == squad.getSize() &&
                getName().equals(squad.getName()) &&
                getCause().equals(squad.getCause());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getCause(), getSize());
    }


}
