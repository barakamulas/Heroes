package models;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Squad(String name, String cause, int maxSize) {
        this.name = name;
        this.cause = cause;
        this.maxSize = maxSize;
    }


}
