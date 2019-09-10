package models;


import java.util.Objects;

public class Squad {

    private String name;
    private String cause;
    private int maxSize;
    private int id;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public int getMaxSize() {
        return maxSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Squad squad = (Squad) o;
        return getMaxSize() == squad.getMaxSize() &&
                getName().equals(squad.getName()) &&
                getCause().equals(squad.getCause());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getCause(), getMaxSize());
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Squad(String name, int maxSize, String cause) {
        this.name = name;
        this.cause = cause;
        this.maxSize = maxSize;
    }


}
