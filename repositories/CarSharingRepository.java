package carsharing.repositories;

import carsharing.model.Car;
import carsharing.model.Company;
import carsharing.model.Customer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CarSharingRepository {
   
    Connection con;
    
    public CarSharingRepository(Connection con) {
       this.con = con;
    }

    public Customer selectForCustomer(String query) {
        List<Customer> customers = selectForCustomerList(query);

        if(customers.size() == 1) {
            return customers.get(0);
        } else if(customers.isEmpty()) {
            return null;
        } else {
            throw new IllegalStateException("More than one object!");
        }
    }
    public List<Company> selectForCompanyList(String query) {
        List<Company> companies = new ArrayList<>();
        try(Statement statement = con.createStatement()) {
           ResultSet result = statement.executeQuery(query);

           while(result.next()) {
               int id = result.getInt("ID");
               String name = result.getString("NAME");

               companies.add(new Company(id, name));
           }
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
        return companies;
    }

    public void run(String update) {
        try (Statement statement = con.createStatement()){
            statement.executeUpdate(update);
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }



    public List<Car> selectForCarList(String query) {
        List<Car> cars = new ArrayList<>();
        try(Statement statement = con.createStatement()) {
            ResultSet rs = statement.executeQuery(query);

            while(rs.next()) {
                int id = rs.getInt("ID");
                String name = rs.getString("NAME");
                int company_id = rs.getInt("COMPANY_ID");

                cars.add(new Car(id, name, company_id));
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
        return cars;
    }

    public List<Customer> selectForCustomerList(String query) {
        List<Customer> customers = new ArrayList<>();
        try(Statement st = con.createStatement()) {
            ResultSet rs = st.executeQuery(query);

            while(rs.next()) {
                int id = rs.getInt("ID");
                String name = rs.getString("NAME");
                Integer car_id = rs.getInt("RENTED_CAR_ID");

                customers.add(new Customer(id, name, car_id));
            }

        } catch(SQLException ex) {
            ex.printStackTrace();
        }
        return customers;
    }
}
