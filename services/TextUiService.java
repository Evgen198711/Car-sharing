package carsharing.services;

import carsharing.model.Car;
import carsharing.model.Company;
import carsharing.model.Customer;

import java.util.*;

public class TextUiService {

    private final CarsharingDAO dao;
    Scanner sc = new Scanner(System.in);

    public TextUiService() {
        this.dao = new CarsharingDAO();
    }

    public void mainMenu() {
        Map<Integer, MenuCommand> mainMenu = new LinkedHashMap<>();
        mainMenu.put(1, new MenuCommand("Log in as a manager", this::managerMenu));
        mainMenu.put(2, new MenuCommand("Log in as a customer", this::customerList));
        mainMenu.put(3, new MenuCommand("Create a customer", this::createCustomer));
        mainMenu.put(0, new MenuCommand("Exit", null));
        runMenu(mainMenu);
    }

    public void managerMenu() {
        Map<Integer, MenuCommand> managerMenu = new LinkedHashMap<>();
        managerMenu.put(1, new MenuCommand("Company list", this::companyList));
        managerMenu.put(2, new MenuCommand("Create a company", this::createCompany));
        managerMenu.put(0, new MenuCommand("Back", this::mainMenu));
        runMenu(managerMenu);
    }

    public void companyMenu(Company company) {
        String header = "'%s' company".formatted(company.getName());
        Map<Integer, MenuCommand> companyMenu = new LinkedHashMap<>();
        companyMenu.put(1, new MenuCommand("Car list", () -> carList(company)));
        companyMenu.put(2, new MenuCommand("Create a car", () -> addCar(company)));
        companyMenu.put(0, new MenuCommand("Back", this::managerMenu));
        runMenu(header, companyMenu);
    }

    public void customerMenu(Customer customer) {
        Map<Integer, MenuCommand> customerMenu = new LinkedHashMap<>();
        customerMenu.put(1, new MenuCommand("Rent a car", () -> companyList(customer)));
        customerMenu.put(2, new MenuCommand("Return a rented car", () -> returnCar(customer)));
        customerMenu.put(3, new MenuCommand("My rented car", () -> myRentedCar(customer)));
        customerMenu.put(0, new MenuCommand("Back", this::mainMenu));
        runMenu(customerMenu);
    }
    public void runMenu(String menuHeader, Map<Integer, MenuCommand> menu) {
        System.out.printf("\n%s", menuHeader);
        runMenu(menu);
    }

    public void runMenu(Map<Integer, MenuCommand> menu) {

        while (true) {
            System.out.println();
            menu.entrySet()
                    .stream().map(s -> s.getKey() + ". " + s.getValue().getMenuOption())
                    .forEach(System.out::println);

            String input = sc.nextLine();

            try {
                int inputNumber = Integer.parseInt(input);

                if(inputNumber == 0 && menu.get(inputNumber).getCommand() == null) {
                    break;
                }

                if (menu.containsKey(inputNumber)) {
                    menu.get(inputNumber).getCommand().executeCommand();
                    break;
                }

            } catch (NumberFormatException ex) {
                System.out.println("Invalid option!");
            }

        }

    }

    public void createCompany() {
        System.out.println("\nEnter the company name:");
        String name = sc.nextLine();
        dao.addCompany(name);
        System.out.println("The company was created!\n");

        managerMenu();
    }

    public void companyList(Customer customer) {
        if(!(customer.getCarId() == 0)) {
            System.out.println("\nYou've already rented a car!");
            customerMenu(customer);
            return;
        }
        String header = "Choose a company:";
        Map<Integer, MenuCommand> chooseCompanyMenu = new LinkedHashMap<>();
        List<Company> companies = dao.findAllCompanies();

        if(companies.isEmpty()) {
            System.out.println("\nThe company list is empty!");
            managerMenu();
            return;
        }

        for(Company company : companies) {
            chooseCompanyMenu.put(company.getId(), new MenuCommand(company.getName(), () -> this.carList(company, customer)));
        }
        chooseCompanyMenu.put(0, new MenuCommand("Back", this::managerMenu));
        runMenu(header, chooseCompanyMenu);
    }

    public void companyList() {
        String header = "Choose a company:";
        Map<Integer, MenuCommand> chooseCompanyMenu = new LinkedHashMap<>();
        List<Company> companies = dao.findAllCompanies();

        if(companies.isEmpty()) {
            System.out.println("\nThe company list is empty!");
            managerMenu();
            return;
        }

        for(Company company : companies) {
            chooseCompanyMenu.put(company.getId(), new MenuCommand(company.getName(), () -> this.companyMenu(company)));
        }
        chooseCompanyMenu.put(0, new MenuCommand("Back", this::managerMenu));
        runMenu(header, chooseCompanyMenu);
    }

    public void carList(Company company, Customer customer) {
        String header = "Choose a car:";
        List<Car> cars = dao.findCompanyCars(company);
        List<Customer> customers = dao.findAllCustomers();
        Map<Integer, MenuCommand> carsCommand = new LinkedHashMap<>();

      for(int i = 0; i < cars.size(); i++) {
          for(int j = 0; j < customers.size(); j++) {
              if(cars.get(i).getId() == customers.get(j).getCarId()) {
                  cars.remove(i);
                  --i;
                  break;
              }
          }
      }


        if(cars.isEmpty()) {
            System.out.println("\nThe car list is empty!");
            customerMenu(customer);
            return;
        }

        for (Car car : cars) {
            carsCommand.put((cars.indexOf(car) + 1), new MenuCommand(car.getName(), () -> rentCar(car, customer)));
        }
        carsCommand.put(0, new MenuCommand("Back", () -> customerMenu(customer)));

        runMenu(header, carsCommand);
    }

    public void carList(Company company) {
        List<Car> cars = dao.findCompanyCars(company);
        System.out.println();

        if(cars.isEmpty()) {
            System.out.println("The car list is empty!");
            companyMenu(company);
            return;
        }

        System.out.println("Car list:");
        cars.stream()
                .map(s -> (cars.indexOf(s) + 1) + ". " + s.getName())
                .forEach(System.out::println);

        companyMenu(company);
    }

    public void addCar(Company company) {
        System.out.println("\nEnter the car name:");
        String carName = sc.nextLine();
        dao.addCar(carName, company);
        System.out.println("The car was added!");

        companyMenu(company);
    }

    public void createCustomer() {
        System.out.println("\nEnter the customer name:");
        String customerName = sc.nextLine();
        dao.addCustomer(customerName);
        System.out.println("The customer was added!");

        mainMenu();
    }

    public void customerList() {
        String header = "Customer list:";
        List<Customer> customers = dao.findAllCustomers();
        Map<Integer, MenuCommand> chooseCustomerMenu = new LinkedHashMap<>();

        if(customers.isEmpty()) {
            System.out.println("The customer list is empty!");
            mainMenu();
            return;
        }

        for(Customer customer : customers) {
            chooseCustomerMenu.put(customer.getId(), new MenuCommand(customer.getName(),() -> customerMenu(customer)));
        }
        chooseCustomerMenu.put(0, new MenuCommand("Back", this::mainMenu));

        runMenu(header, chooseCustomerMenu);
    }

    public void rentCar(Car car , Customer customer) {

        if (customer.getCarId() == 0) {
            dao.rentCar(car, customer);
            System.out.printf("\nYou rented '%s'\n", car.getName());
            customer = dao.findCustomerById(customer);
        }

        customerMenu(customer);
    }

    public void myRentedCar(Customer customer) {
        List<Car> cars = dao.findAllCars();
        List<Company> companies = dao.findAllCompanies();

        for (Car car : cars) {
            if(car.getId() == customer.getCarId()) {
                System.out.println("\nYour rented car:" +
                        "\n%s".formatted(car.getName()));
                for(Company company : companies) {
                    if(car.getCompany_id() == company.getId()) {
                        System.out.println("Company:" +
                                "\n%s".formatted(company.getName()));
                        customerMenu(customer);
                        return;
                    }
                }
            }
        }

        System.out.println("\nYou didn't rent a car!");
        customerMenu(customer);
    }

    public void returnCar(Customer customer) {
        if(customer.getCarId() == 0) {
            System.out.println("\nYou didn't rent a car!");
        } else {
            dao.returnCar(customer);
            customer = dao.findCustomerById(customer);
            System.out.println("\nYou've returned a rented car!");
        }
        customerMenu(customer);
    }
}
