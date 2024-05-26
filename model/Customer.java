package carsharing.model;

public class Customer {
    private int id;
    private String name;
    private Integer carId;

    public Customer(int id, String name, Integer carId) {
        this.id = id;
        this.name = name;
        this.carId = carId;
    }

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

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }
}
