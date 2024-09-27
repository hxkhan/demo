package agile18.demo.model;

import java.util.List;
import java.util.Map;

//import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import agile18.demo.model.Exceptions.CitizenDoesNotExistException;
import agile18.demo.model.Exceptions.CitizenExistsException;
import agile18.demo.model.Exceptions.ReferendumExistsException;
import agile18.demo.model.Exceptions.ReferendumNotFoundException;

@Service
public class Database {
    private final JdbcTemplate jdbc;

    public Database(JdbcTemplate jdbcTemplate) {
        this.jdbc = jdbcTemplate;
    }

// --------------------- Citizen ---------------------
    public Citizen getCitizenWithPersonNumber(String id) throws CitizenDoesNotExistException {
        String sql = "SELECT * FROM Citizens WHERE id = '" + id + "';";
        List<Map<String, Object>> result = jdbc.queryForList(sql);
        if (result.isEmpty()) {
            throw new CitizenDoesNotExistException();
        }
        Citizen user = new Citizen(
                result.get(0).get("id").toString(),
                result.get(0).get("name").toString(),
                result.get(0).get("pass").toString());
        return user;
    }
    public boolean ifCitizenExists(String id) {
        String sql = "SELECT 1 FROM Citizens WHERE id = '" + id + "';";
        return !jdbc.queryForList(sql).isEmpty();
    }

    public List<Citizen> getAllCitizens() {
        String sql = "SELECT * FROM Citizens;";

        // Query for a list of rows
        List<Citizen> entities = jdbc.query(sql, (rs, rowNum) -> {
            return new Citizen(rs.getString("id"), rs.getString("name"), rs.getString("pass"));
        });

        return entities;
    }
    public void createCitizen(String name, String id, String pass) throws CitizenExistsException {
        if (ifCitizenExists(id)) {
            throw new CitizenExistsException();
        }
        String sql = "INSERT INTO Citizens VALUES ('" + id + "', '" + name + "', '" + pass + "');";
        jdbc.execute(sql);
    }

    // --------------------- Referendum ---------------------

    /** 
     * Creates an unique referendum id by incrementing the highest existing id.
     * @return an unique referendum id that can be used for new referendums.
     */
    public String getUniqueRefId(){
        String sql = 
            """
            SELECT id 
            FROM Referendum 
            ORDER BY id DESC 
            LIMIT 1;
            """;
        List<Map<String, Object>> result = jdbc.queryForList(sql);
        if (result.isEmpty()){
            return "0";
        }
        String res_str = result.get(0).get("id").toString();
        System.out.println(result.get(0).get("id").toString());
        return ""+(Integer.parseInt(res_str)+1);
    }

    /** 
     * Checks if given referendum id exists.
     * @param id - Id for citizen.
     * @return true if referendum exists, false otherwise.
     */
    public boolean referendumExists(String id){
        String sql = "SELECT 1 FROM Referendum WHERE id = " + id + ";";
        return !jdbc.queryForList(sql).isEmpty();
    }
    /** 
     * Tries to create a referendum in the database using input parameters.
     * @param id - Id for citizen.
     * @param area - Area affected by referendum.
     * @param title - Title of referendum.
     * @param body - Text explanation for referendum.
     * @param startDate - Start date of referendum.
     * @param endDate - End date of referendum.
     */
    public void createReferendum(String id, String area, String title, String body, String startDate, String endDate) throws ReferendumExistsException {
        if (referendumExists(id)) {
            throw new ReferendumExistsException();
        }
        String sql = "INSERT INTO Referendum VALUES ("
            + id + ", '"
            + area + "', '" 
            + title + "', '" 
            + body + "', '" 
            + startDate + "', '"
            + endDate + "');";

        jdbc.execute(sql);
    }

    /**
     * Returns true if the given referendum is currently open.
     * @param id id for referendum.
     */
    public boolean referendumOpen(String id){
        String sql = "SELECT 1 FROM Referendum " +
                "WHERE id = " + id +
                " AND CURRENT_DATE >= startDate\n" +
                " AND CURRENT_DATE <= endDate;";
        return !jdbc.queryForList(sql).isEmpty();
    }

    /**
     * Tries to let given citizen vote in given referendum.
     * @param citizenId id for citizen.
     * @param referendumId id for referendum.
     * @param vote What kind of vote.
     * @return true if vote is successful, false otherwise.
     */
    public boolean citizenVote(String citizenId, String referendumId, VoteEnum vote) {
        if (!referendumOpen(referendumId) || !canCitizenVote(citizenId, referendumId)){
            return false;
        }
        String sql;

        if (vote == VoteEnum.FAVOR) {
            sql = "UPDATE RefResults \n" +
                    "SET favor = favor + 1 \n" +
                    "WHERE referendum = " + referendumId +";";
            jdbc.update(sql);
        }
        if (vote == VoteEnum.AGAINST) {
            sql = "UPDATE RefResults \n" +
                    "SET against = against + 1 \n" +
                    "WHERE referendum = " + referendumId +";";
            jdbc.update(sql);

        }
        if (vote == VoteEnum.BLANK) {
            sql = "UPDATE RefResults \n" +
                    "SET blank = blank + 1 \n" +
                    "WHERE referendum = " + referendumId +";";
            jdbc.update(sql);
        }

        return true;
    }

    /**
     * Returns true if citizen is eligible to vote in given referendum.
     * @param citizenId id for citizen.
     * @param referendumId id for referendum.
     */
    public boolean canCitizenVote(String citizenId, String referendumId){
        String sql = "\n" +
                "SELECT * FROM Citizens JOIN\n" +
                "(SELECT name FROM Area JOIN Referendum ON Area.name = Referendum.area\n" +
                "WHERE Referendum.id = " + referendumId +"\n" +
                "UNION\n" +
                "SELECT name FROM Area JOIN Referendum ON Area.partof = Referendum.area\n" +
                "WHERE Referendum.id = " + referendumId + ")\n" +
                "AS Valid\n" +
                "WHERE Citizens.home = Valid.name\n" +
                "AND Citizens.id NOT IN\n" +
                "(Select citizen FROM RefRoll\n" +
                "WHERE referendum = " + referendumId + ") \n" +
                "AND Citizens.id = '" + citizenId + "';";
        return !jdbc.queryForList(sql).isEmpty();
    }

    /**
     * Returns true if the citizen has voted in the given referendum.
     * @param citizenId id for citizen.
     * @param referendumId id for referendum.
     */
    public boolean hasCitizenVoted(String citizenId, String referendumId){
        String sql = "SELECT 1 FROM RefRoll \n" +
                "WHERE citizen = '" + citizenId + "' \n" +
                "AND referendum = " + referendumId + ";";
        return !jdbc.queryForList(sql).isEmpty();
    }
    
    /**
     * Retrieve the voting results of a referendum including the number of blank votes,
     * votes in favor, and votes against, from the RefResults table.
     */
    public int[] getReferendumResult(String referendumId) throws ReferendumNotFoundException {
        String sql = "SELECT * FROM RefResults WHERE referendum = " + referendumId + ";";
        List<Map<String, Object>> results = jdbc.queryForList(sql);
        if (results.isEmpty()) {
            throw new ReferendumNotFoundException();
        }

        int[] Arr =  new int[3];
        Arr[0] = (int)results.get(0).get("blank");
        Arr[1] = (int)results.get(0).get("favor");
        Arr[2] = (int)results.get(0).get("against");

        return Arr;
    }

    // --------------------- TEST ---------------------
    public void testReferendum(){
        String id = getUniqueRefId();
        System.out.println("id: "+ id);
        String now = "2024-09-23";
        String endDate = "2024-09-30";
        try {
            createReferendum(id, "Göteborg", "Ny skola", "Vi behöver..", now, endDate);
            int[] res = getReferendumResult("1");
            System.out.println(res[0] + " " + res[1] + " " + res[2]);
        } catch (Exception e) {
            System.out.println(e);
        }

    }
}