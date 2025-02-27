# Democracy Now
*Democracy now* is a direct democracy platform that allows individuals to propose and vote on new ideas for legislation on both the local and national level. In Sweden this would be the municipal, county and national levels. 

Our website will allow users to log in and browse various referendums and if eligible, also vote. A news feed detailing things like which referendums have passed will also be available.

Our main UN sustainability goal is nr 16. Direct democracy can promote transparency and helps fight corruption. It also empowers local communities to protect their interests!

## Technical Info

### Steps to run
1. Have vs code
3. Have the Java SDK (preferably v21)
3. Get `Extension Pack for Java`
4. Get `Spring Boot Extension Pack`
5. Click run button at the top right in vs code


### Directories
1. Model code (business logic) goes into `src/main/java/agile18/demo/model`
2. REST API controllers go into `src/main/java/agile18/demo/web`
    - Remember, there should be no business logic in here
    - It should be as if we can replace the web interface with a console one without muche effort and without any changes in the model whatsoever
3. Frontend (HTML/CSS/JS) go into `src/main/resources/static`

### Specific files
1. Login/Registration REST API code goes into `src/main/java/agile18/demo/web/OnboardingController.java`
2. Complete DB Schema is available at `src/main/resources/schema.sql`
    - This schema does not automatically run by itself on startup (for obvious reasons including the fact that the DB is persistent between different runs)
    - That means it has to be manually run in the Admin panel of the database, but only when the schema has *changed* - more on that below
3. Login/Registration model code goes into `src/main/java/agile18/demo/model/Onboarder.java`

### Access the DB Admin panel
1. Run the application
2. Go to `localhost:8080/h2-console`
3. Paste `jdbc:h2:file:./store` into the JDBC url (only needed the first time)
4. Username is `sa` & there is no password
5. Click connect
6. You're free to execute any SQL you want
7. Also leave the `Users` table alone, thats for the DB itself and not ours

## Rest API Testing

Register a citizen
```
curl --request POST \
  --url http://localhost:8080/register \
  --header 'Content-Type: application/json' \
  --data '{
  "firstName": "John",
  "lastName": "Doe",
  "id": "0305250000",
  "password": "123",
  "municipality": "Göteborg"
}'
```

Login a citizen
```
curl --request POST \
  --url http://localhost:8080/login \
  --header 'Content-Type: application/json' \
  --data '{
  "id": "0305250000",
  "password": "123"
}'
```

Get all citizens
```
curl --request GET \
  --url http://localhost:8080/citizens
```

Create a poll, make sure to run login first and paste the uuid down below in the url
```
curl --request POST \
  --url 'http://localhost:8080/create-poll?uuid=b25e7996-b9a5-4cf4-9c86-65e0b2363750' \
  --header 'Content-Type: application/json' \
  --data '{
  "title": "Legalize Marijuana!",
  "body": "plz plzz",
  "level": "Municipal",
  "startDate": "2024-11-01",
  "endDate": "2024-12-01"
}'
```

Cast a vote, make sure to run login first and paste the uuid down below in the url
```
curl --request POST \
  --url 'http://localhost:8080/cast-vote?uuid=852ad138-41bb-4519-919e-8de4a9e257d5' \
  --header 'Content-Type: application/json' \
  --data '{
  "id": 0,
  "vote": "Favor"
}'
```

## Response
Responses are supposed to return a json with a key `success` that is either `true` or `false`. If it is `false` then there is also a `message` key present that provides context on why. If it is `true` then we know what should be present, for login and register `success: true` means a key `uuid` will be present. This is the access token for further requests.