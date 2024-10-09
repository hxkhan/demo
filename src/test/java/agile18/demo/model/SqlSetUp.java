package agile18.demo.model;

import agile18.demo.model.Database.Database;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class SqlSetUp {
    private final String resourcePath = "C:\\Users\\corbe\\agiletry\\src\\main\\resources\\";


    public SqlSetUp(){}
    static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
    public void setUpDB(Database db, String insertFile){
        executeSqlFile(db, "schema.sql");
        executeSqlFile(db, insertFile);
    }
    public void setUpDB(Database db){
        executeSqlFile(db, "schema.sql");
    }
    public void executeSqlFile(Database db, String File){
        String sql = resourcePath + File;
        try {
            sql = readFile(sql, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        db.executeHelper(sql);
    }
    public void clearDB(Database db){
        executeSqlFile(db, "clear.sql");
    }
}
