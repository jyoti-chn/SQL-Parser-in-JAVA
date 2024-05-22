import java.util.Map;

public class SQLParserTest extends SQLParserExample{

    public static void main(String[] args) {
        String TC1 ="SELECT customers.name, orders.order_date, order_items.quantity " +
        "FROM customers " +
        "INNER JOIN orders ON customers.id = orders.customer_id " +
        "INNER JOIN order_items ON orders.id = order_items.order_id " +
        "WHERE customers.country = 'USA' " +
        "GROUP BY customers.name, orders.order_date " +
        "HAVING SUM(order_items.quantity) > 10;";

        String TC2 = "SELECT customers.name, orders.order_date, order_items.quantity " +
                         "WHERE customers " +
                         "FROM customers.country = 'USA' " +
                         "GROUP BY customers.name, orders.order_date " +
                         "HAVING SUM(order_items.quantity) > 10;";

        String TC3 ="SELECT customers.name,, orders.order_date, order_items.quantity " +
        "FROM customers " +
        "WHERE customers.country = 'USA' " +
        "GROUP BY customers.name, orders.order_date " +
        "HAVING SUM(order_items.quantity) > 10;";

        String TC4 = "CREATE TABLE customers (" +
                "id INT PRIMARY KEY," +
                "name VARCHAR(50) NOT NULL," +
                "email VARCHAR(100) UNIQUE," +
                "personID INT FOREIGN KEY REFERENCES Persons(PersonID)," +
                "address TEXT);"; 

        String insert = "INSERT INTO customers (name, email, address)" + 
        " VALUES ('John Doe', 'john@example.com', '123 Main St');";

        String TC5 = TC4 + insert;

        String TC6 = "CREATE TABLE customers (" +
                "id INT PRIMARY KEY," +
                "name VARCHAR(50) NOT NULL," +
                "email VARCHAR(100) UNIQUE," +
                "personID INT FOREIGN KEY REFERENCES Persons(PersonID)," +
                "address TEXT);" +
                "INSERT INTO customerss (name, email, id, address)" + 
                " VALUES ('John Doe', 'john@example.com', 1,  '123 Main St');";

        String TC7 = "CREATE TABLE customers (" +
                "id INT PRIMARY KEY," +
                "name VARCHAR(50) NOT NULL," +
                "email VARCHAR(100) UNIQUE," +
                "personID INT FOREIGN KEY REFERENCES Persons(PersonID)," +
                "address TEXT);" +
                "INSERT INTO customers (name, email, id, address)" + 
                " VALUES ('John Doe', 'john@example.com', 1,  '123 Main St');";

        String TC8 = TC7 + "DROP TABLE customers;";

        String TC9 = "CREATE TABLE customers (" +
        "id INT PRIMARY KEY," +
        "name VARCHAR(50) NOT NULL," +
        "email VARCHAR(100) UNIQUE," +
        "personID INT FOREIGN KEY REFERENCES Persons(PersonID)," +
        "address TEXT);" +

        "CREATE TABLE orders (" +
        "id INT PRIMARY KEY," +
        "amount INT NOT NULL," +
        "customerID INT FOREIGN KEY REFERENCES customers(customerID))" ;

        String TC10 = "CREATE TABLE customers (" +
        "id INT PRIMARY KEY," +
        "name VARCHAR(50) NOT NULL," +
        "email VARCHAR(100) UNIQUE," +
        "personID INT FOREIGN KEY REFERENCES Persons(PersonID)," +
        "address TEXT);" +
        "INSERT INTO customers (xyz, email,id ,address)" + 
        " VALUES ('John Doe', 'john@example.com',1, '123 Main St');";

        String TC11 = "CREATE DATABASE db;";

        String TC12 = "CREATE TABLE 1customers (" +
                "id INT PRIMARY KEY," +
                "name VARCHAR(50) NOT NULL," +
                "email VARCHAR(100) UNIQUE," +
                "personID INT FOREIGN KEY REFERENCES Persons(PersonID)," +
                "address TEXT);"; 
                    
        SQLParser parser = new SQLParser(TC9);

        boolean success = parser.parseAll();

        if (success) {
            System.out.println("No syntax/lexical errors found");
            System.out.println("Tables: " + parser.tables);
            System.out.println("Columns: " + parser.columns);
            System.out.println("Primary keys: " + parser.pks);
            System.out.println("Foreign keys: " + parser.sks);
        } else {
            System.out.println("Parsing failed. Please check for sytax errors.");
        }

    }

}
