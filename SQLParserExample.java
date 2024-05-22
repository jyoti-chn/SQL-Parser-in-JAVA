import java.nio.file.Files;
import java.util.*;
import java.nio.file.Paths;

class SQLParser {
    private String input;
    private int position;
    public ArrayList<String> tables = new ArrayList<> ();
    public HashMap<String, ArrayList<List<String>>> columns = new HashMap<>();
    public HashMap<String, List<String>> pks = new HashMap<>();
    public HashMap<String, List<String>> sks = new HashMap<>();
    public HashMap<String, List<String>> ColumnNames = new HashMap<>();
    
    public SQLParser(String input) {
        this.input = input;
        this.position = 0;
    }
    public boolean parseAll(){
        String statements[] = input.split(";");
        boolean isValid= true;
        for(String x: statements){
            input = x;
            position=0;
            isValid = isValid && parse();
        }
        return isValid;
    }
    public boolean parse(){ 
        if(createDatabase()) return true;
        position=0;
        if(parseSelectStatement()){
            position=0;
            match("SELECT");
            if(!match("*")) parseSelectList();
            match("FROM");
            int start = position;
            parseTableName();
            String name = input.substring(start, position).trim();
            if(!tables.contains(name)){
                System.out.println("There is no such table");
                return false;
            }
            
            return true;
        }
        position=0;
        if(parseCreateTable()){
            position=0;
            match("CREATE");
            match("TABLE");
            int startInd = position;
            parseTableName();
            int endInd = position;
            tables.add(input.substring(startInd,endInd).trim());
            match("(");
            StoreColumns(input.substring(startInd,endInd).trim());
            return true;
        }
        position=0;
        if(parseInsertInto()){
            position=0;
            match("INSERT");
            match("INTO");
            int startInd = position;
            parseTableName();
            String tableName = input.substring(startInd,position).trim();
            if(!tables.contains(tableName)){
                System.out.println("There is no such table");
                return false;
            }
            List<String> pk = pks.get(tableName);
            match("(");
            startInd = position;
            while(position<input.length() && input.charAt(position) != ')') position++;
            String list[] = input.substring(startInd, position).split(",");
            for(int i=0;i< list.length;i++) list[i]= list[i].trim();
            List<String> colList = Arrays.asList(list);
            // System.out.println(ColumnNames);
            for(String x: colList){
                if(!ColumnNames.get(tableName).contains(x)){
                    System.out.println("The column "+ x + " doesn't exist in " + tableName);
                    return false;
                }
            }
            // System.out.println(colList);
            for(String x: pk){
                if(!colList.contains(x)){
                    System.out.println("Primary key " +x + " cannot be null");
                    return false;
                }
            }
            return true;
        }
        position=0;
        if(parseDropTable()){
            position=0;
            match("DROP");
            match("TABLE");
            String table = input.substring(position).trim();
            if(!tables.contains(table)){
                System.out.print("No table named '" + table + "' in the db");
                return false;
            }
            tables.remove(table);
            columns.remove(table);
            pks.remove(table);
            sks.remove(table);
            ColumnNames.remove(table);
            return true;
        }
        return false;
    }
    
    public boolean parseSelectStatement() {
        if (match("SELECT") && (parseSelectList() || match("*")) && match("FROM") && parseTableList() && parseJoin() &&
            parseWhereClause() && parseGroupByClause() && parseHavingClause()) {
            return true;
        }
        return false;
    }
    private boolean parseCreateTable() {
        return match("CREATE") && match("TABLE") && parseTableName() && match("(") && parseColumnDefList() && match(")");
    }

    private boolean parseInsertInto() {
        return match("INSERT") && match("INTO") && parseTableName() && match("(") && parseSelectList() && match(")") && match("VALUES") && match("(") && parseValueList() && match(")");
    }
    private boolean parseDropTable() {
        return match("DROP") && match("TABLE") && parseTableName();
    }
    private boolean createDatabase() {
        return match("CREATE") && match("DATABASE") && matchIdentifier();
    }

    private void StoreColumns(String tableName){
        ArrayList<List<String>> cols = new ArrayList<>();
        List<String> pk= new ArrayList<>();
        List<String> fk= new ArrayList<>();
        List<String> colNames= new ArrayList<>();
        cols.add(findColumn(pk,fk, colNames));
        while (match(",")){
            cols.add(findColumn(pk,fk, colNames));
        }
        columns.put(tableName,cols);
        pks.put(tableName, pk);
        sks.put(tableName, fk);
        ColumnNames.put(tableName, colNames);
    }

    private List<String> findColumn(List<String> pk, List<String> fk, List<String> colNames){
        List<String> singleCol = new ArrayList<>();
        skipSpaces();
        int startInd = position;
        parseColumnName();
        int endInd = position;
        singleCol.add(input.substring(startInd,endInd).trim());
        colNames.add(input.substring(startInd,endInd).trim());
        parseDataType();
        singleCol.add(input.substring(endInd,position));
        endInd= position;
        skipSpaces();
        parseColumnConstraints();
        String temp= input.substring(endInd,position).trim();
        if(temp.length()<1) singleCol.add("no constraint provided");
        else if(temp.length() <12){
            singleCol.add(temp);
            if(temp.equals("PRIMARY KEY")) pk.add(singleCol.get(0));
        }
        else {
            fk.add(singleCol.get(0));
            singleCol.add("FOREIGN KEY");
        }
        return singleCol;
    }


    private boolean parseSelectList() {
        int currPos = position;
        if (parseColumn() && match(",") && parseSelectList()) {
            return true;
        } else{
            position = currPos;
            if (parseColumn()) {
                return true;
            }
        }
        return false;
    }
    
    private boolean parseColumn() {
        int currPos = position;
        if (parseTableName() && match(".") && parseColumnName()) {
            return true;
        } else{
            position = currPos;
            if (parseColumnName()) {
                return true;
            }
        }
        return false;
    }
    
    private boolean parseTableList() {
        int currPos = position;
        if (parseTableName() && match(",") && parseTableList()) {
            return true;
        } else{
            position = currPos;
            if (parseTableName()) {
                return true;
            }
        }
        return false;
    }
    
    private boolean parseWhereClause() {
        if (match("WHERE") && parseCondition()) {
            return true;
        } else {
           return false;
        }
    }

    private boolean parseCondition() {
        if (parseComparison()) {
            return true;
        } else if (match("(") && parseCondition() && match(")")) {
            return true;
        } else if (parseCondition() && match("AND") && parseCondition()) {
            return true;
        } else if (parseCondition() && match("OR") && parseCondition()) {
            return true;
        }
        return false;
    }
    
    private boolean parseComparison() {
        if (parseColumn() && parseOperator() && parseValue()) {
            return true;
        }
        return false;
    }
    
    private boolean parseOperator() {
        if (match("=") || match("<>") || match(">") || match("<") || match(">=") || match("<=")) {
            return true;
        }
        return false;
    }
    
    private boolean parseValue() {
        if (parseColumn() || parseLiteral()) {
            return true;
        }
        return false;
    }
    
    private boolean parseLiteral() {
        if (match("'") && matchString() && match("'")) {
            return true;
        } else if (matchNumber()) {
            return true;
        }
        return false;
    }
    
    private boolean matchString(){
        if(position < input.length() && input.charAt(position) >=32 && input.charAt(position) <127){
            while(position < input.length() && input.charAt(position) !=39 && input.charAt(position) >=32 && input.charAt(position) <127){
                position++;
            }
            return true;
        }
        return false;
        
    }
    private boolean parseGroupByClause() {
        if (match("GROUP BY") && parseSelectList()) {
            return true;
        }
        return false;
    }
    
    private boolean parseHavingClause() {
        if (match("HAVING") && parseAggregateFunction() && parseOperator() && parseLiteral()) {
            return true;
        }
        return false;
    }
    private boolean parseAggregateFunction() {
        if (match("SUM") || match("AVG") || match("MIN") || match("MAX") || match("COUNT")) {
            if (match("(") && parseColumn() && match(")")) {
                return true;
            }
        }
        return false;
    }

    private boolean parseJoin() {
        int currPos = position;
        if(!(parseInnerJoin() || parseOuterJoin() || parseCrossJoin())) {
            position= currPos;
            return true;
        }
        parseJoin();
        return true;
    }
    
    private boolean parseInnerJoin() {
        return  match("INNER JOIN") && parseTableName() && match("ON") && parseCondition();
    }
    
    private boolean parseOuterJoin() {
        return parseOuterJoinType() && parseTableName() && match("ON") && parseCondition();
    }
    
    private boolean parseOuterJoinType() {
        return match("LEFT OUTER JOIN") || match("RIGHT OUTER JOIN") || match("FULL OUTER JOIN");
    }
    
    private boolean parseCrossJoin() {
        return match("CROSS JOIN") && parseTableName();
    }

    private boolean parseOrderBy() {
        if (match("ORDER BY")) {
            if (parseColumn()) {
                if (match("ASC") || match("DESC")) {
                    return parseOrderByRest();
                }
                return true;
            }
            return false;
        }
        return true;
    }
    
    private boolean parseOrderByRest() {
        if (match(",")) {
            if (parseColumn()) {
                if (match("ASC") || match("DESC")) {
                    return parseOrderByRest();
                }
                return true;
            }
            return false;
        }
        return true;
    }

    
    private boolean parseTableName() {
        if (matchIdentifier()) {
            return true;
        }
        return false;
    }
    
    private boolean parseColumnName() {
        if (matchIdentifier()) {
            return true;
        }
        return false;
    }

    private boolean parseColumnDefList() {
        if (parseColumnDef()) {
            while (match(",")) {
                if (!parseColumnDef()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private boolean parseColumnDef() {
        skipSpaces();
        return matchIdentifier() && parseDataType() && parseColumnConstraints();
    }

    private boolean parseDataType() {
        skipSpaces();
        return match("INT") || (match("VARCHAR(") && matchNumber() && match(")")) || match("TEXT");
    }
    
    private boolean parseColumnConstraints() {
        skipSpaces();
        if (match("PRIMARY KEY")) {
            return true;
        }
        if (match("NOT NULL")) {
            return true;
        }
        if (match("UNIQUE")) {
            return true;
        }
        if (match("FOREIGN KEY")) {
            boolean once = false;
            while(input.charAt(position)!= ','){
                if(input.charAt(position) == ')'){
                    if(once) {
                        break;
                    }
                    once= true;
                }
                position++;
            }
            return true;
        }
        return true;
    }

    private boolean parseValueList() {
        if (parseLiteral()) {
            skipSpaces();
            while (match(",")) {
                if (!parseLiteral()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private boolean match(String expected) {
        skipSpaces();
        if (position + expected.length() <= input.length() &&
            input.substring(position, position + expected.length()).equals(expected)) {
            position += expected.length();
            // System.out.println("success" + expected);
            return true;
        }
        // System.out.println("match func failed " + expected + input.substring(position, position+expected.length() ));
        return false;
    }
    
    private boolean matchIdentifier() {
        skipSpaces();
        if (position < input.length() && Character.isLetter(input.charAt(position))) {
            position++;
            while (position < input.length() && (Character.isLetterOrDigit(input.charAt(position)) || input.charAt(position) == '_')) {
                position++;
            }
            return true;
        }
        return false;
    }
    
    private boolean matchNumber() {
        if (position < input.length() && Character.isDigit(input.charAt(position))) {
            position++;
            while (position < input.length() && (Character.isDigit(input.charAt(position)) || input.charAt(position) == '.')) {
                position++;
            }
            return true;
        }
        return false;
    }
    private void skipSpaces(){
        while(position < input.length() && input.charAt(position) == ' ') position++ ;
    }
    }
    
    public class SQLParserExample {
        public static void main(String[] args) {
            
            String select = "SELECT customers.name, orders.order_date, order_items.quantity " +
                         "FROM customers " +
                         "INNER JOIN orders ON customers.id = orders.customer_id " +
                         "INNER JOIN order_items ON orders.id = order_items.order_id " +
                         "WHERE customers.country = 'USA' " +
                         "GROUP BY customers.name, orders.order_date " +
                         "HAVING SUM(order_items.quantity) > 10;";

            String create = "CREATE TABLE customers (" +
                "id INT PRIMARY KEY," +
                "name VARCHAR(50) NOT NULL," +
                "email VARCHAR(100) UNIQUE," +
                "personID INT FOREIGN KEY REFERENCES Persons(PersonID)," +
                "address TEXT);"; 
            
            String insert = "INSERT INTO customers (name, email, id, address)" + 
            " VALUES ('John Doe', 'john@example.com', 1, '123 Main St');";

            String drop = "DROP TABLE customers;";
        }
    }

    
    
    
    
    
    
    
    
