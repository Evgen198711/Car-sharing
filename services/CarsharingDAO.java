package carsharing.services;

import carsharing.model.Car;
import carsharing.model.Company;
import carsharing.model.Customer;
import carsharing.repositories.CarSharingRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

public class CarsharingDAO {
    private CarSharingRepository repository;
    static final String DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:./src/carsharing/db/carsharing";
    private static final String CREATE_TABLE_COMPANY =
            "CREATE TABLE IF NOT EXISTS company (" +
                    "ID INT AUTO_INCREMENT NOT NULL PRIMARY KEY," +
                    "NAME VARCHAR(50) UNIQUE NOT NULL);";

    private static final String CREATE_TABLE_CAR =
            "CREATE TABLE IF NOT EXISTS car (" +
                    "ID INT AUTO_INCREMENT NOT NULL PRIMARY KEY," +
                    "NAME VARCHAR(50) UNIQUE NOT NULL," +
                    "COMPANY_ID INT NOT NULL," +
                    "CONSTRAINT COMPANY_ID FOREIGN KEY (COMPANY_ID)" +
                    "REFERENCES company(ID)" +
                    ");";
    private static final String CREATE_TABLE_CUSTOMER =
            "CREATE TABLE IF NOT EXISTS customer (" +
                    "ID INT AUTO_INCREMENT NOT NULL PRIMARY KEY," +
                    "NAME VARCHAR(50) UNIQUE NOT NULL," +
                    "RENTED_CAR_ID INT," +
                    "CONSTRAINT RENTED_CAR_ID FOREIGN KEY (RENTED_CAR_ID)" +
                    "REFERENCES car(ID)" +
                    ");";
    private static final String SHOW_ALL_COMPANIES = "SELECT * FROM company";
    private static final String SHOW_COMPANY_CARS = "SELECT * FROM car WHERE COMPANY_ID = %d";
    private static final String SHOW_ALL_CARS = "SELECT * FROM car";
    private static final String SHOW_ALL_CUSTOMERS = "SELECT * FROM customer";
    private static final String FIND_CUSTOMER_BY_ID = "SELECT * FROM customer WHERE ID = '%s'";
    private static final String ADD_COMPANY = "INSERT INTO company (NAME) VALUES ('%s')";
    private static final String ADD_CAR = "INSERT INTO car (NAME, COMPANY_ID) VALUES ('%s', %d)";
    private static final String ADD_CUSTOMER = "INSERT INTO customer (NAME, RENTED_CAR_ID) VALUES ('%s', NULL)";
    private static final String RENT_CAR = "UPDATE customer SET RENTED_CAR_ID = %d WHERE NAME = '%s'";
    private static final String RETURN_CAR = "UPDATE customer SET RENTED_CAR_ID = NULL WHERE NAME = '%s'";


    public CarsharingDAO() {
        try {
            Class.forName(DRIVER);
            Connection con = DriverManager.getConnection(DB_URL);
            con.setAutoCommit(true);
            this.repository = new CarSharingRepository(con);
            repository.run(CREATE_TABLE_COMPANY);
            repository.run(CREATE_TABLE_CAR);
            repository.run(CREATE_TABLE_CUSTOMER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<Company> findAllCompanies() {
        return repository.selectForCompanyList(SHOW_ALL_COMPANIES);
    }

    public List<Car> findCompanyCars(Company company) {
        return repository.selectForCarList(SHOW_COMPANY_CARS.formatted(company.getId()));
    }
    public List<Car> findAllCars() {
        return repository.selectForCarList(SHOW_ALL_CARS);
    }
    public List<Customer> findAllCustomers() {
        return repository.selectForCustomerList(SHOW_ALL_CUSTOMERS);
    }
    public Customer findCustomerById(Customer customer) {
        return repository.selectForCustomer(FIND_CUSTOMER_BY_ID.formatted(customer.getId()));
    }

    public void addCompany(String company) {
        repository.run(ADD_COMPANY.formatted(company));
    }

    public void addCar(String name, Company company) {
        repository.run(ADD_CAR.formatted(name, company.getId()));
    }

    public void addCustomer(String name) {
        repository.run(ADD_CUSTOMER.formatted(name));
    }

    public void rentCar(Car car, Customer customer) {
        repository.run(RENT_CAR.formatted(car.getId(), customer.getName()));
    }

    public void returnCar(Customer customer) {
        repository.run(RETURN_CAR.formatted(customer.getName()));
    }
}
