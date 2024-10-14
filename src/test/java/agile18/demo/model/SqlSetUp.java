package agile18.demo.model;

import agile18.demo.model.Database.Database;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class SqlSetUp {
    private final Path resourcePath = Paths.get(System.getProperty("user.dir"),
                                                    "src","main","resources");

    static String readFile(Path path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(path);
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
        Path path = Paths.get(resourcePath.toString(), File);
        String sql;
        try {
            sql = readFile(path, StandardCharsets.UTF_8);
            db.executeHelper(sql);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void clearDB(Database db){
        executeSqlFile(db, "clear.sql");
    }
}
