---
layout: article
title: "Engine Tutorial: Postgres"
category: tut
---

**It is required that the article on the [Database Abstraction Layer](dba.html) has been read thoroughly in order to be able to follow this tutorial.**

This tutorial will show how Postgres support is implemented via the Database Abstraction Layer.

### Preconditions

- Postgres is relational
- A JDBC driver exists for Postgres
- We want to add repository and IQM4HD support

### Workload
- No [Fetcher](../javaDocs/de/hshannover/dqgui/execution/database/api/DatabaseFetcher.html) needs to be written as we can use the [JdbcFetcher](../javaDocs/de/hshannover/dqgui/execution/database/fetchers/JdbcFetcher.html) implementation.
- We will use the [ofJDBCommon() GuiConfiguration with two icons](../javaDocs/de/hshannover/dqgui/execution/database/gui/GuiConfiguration.html)
- We will add repository support via [JdbcRepository](../javaDocs/de/hshannover/dqgui/dbsupport/JdbcRepository.html) and [YamlConfigurableRelationalRepositorySupport](../javaDocs/de/hshannover/dqgui/execution/database/api/YamlConfigurableRelationalRepositorySupport.html)
- We will have to implement a [createDataSourceUrl()](../javaDocs/de/hshannover/dqgui/execution/database/api/DatabaseEngine.html#createDataSourceUrl-de.hshannover.dqgui.execution.database.api.DatabaseConnection-) since it is a JDBC backed engine

### Integrating with Build Script

We start by unzipping the sub-project skeleton, registering it with the build script and changing all placeholder identifiers to `postgres` and adding the Postgres JDBC driver to our sub-project.

```gradle
// https://mvnrepository.com/artifact/org.postgresql/postgresql
implementation group: 'org.postgresql', name: 'postgresql', version: '42.2.8'
```

### Setting up resources

We copy the two yaml configuration files from the support files zip into `engine/postgres` within the resource folder. 

We also search the internet for icons that shall be displayed in the wizard and menu and place them in the `engine/postgres` folder as well with the following names:
- wizard icon: `POSTGRES.png`
- list icon: `postgres_menu.png`

Our resource folder looks like this now:

```
└── src
    ├── main                    
    │   │                       
    │   └── resources
    │       └── engine
    │           └── postgres
    │               ├── POSTGRES.png
    │               ├── postgres_menu.png
    │               ├── postgres_queries.yml
    │               └── postgres_schema.yml
```

### Extending DatabaseEngine

Now we can implement our [DatabaseEngine](../javaDocs/de/hshannover/dqgui/execution/database/api/DatabaseEngine.html) subclass in the `de.hshannover.dqgui.engine` package.

```java
package de.hshannover.dqgui.engine;

public class PostgresEngine extends DatabaseEngine {
    // Define resource location to resolve resources
    private final static String RESOURCES = "/engine/postgres/";

    protected PostgresEngine() throws Exception {
        super();
    // set identifer after convention
        registerIdentifier("postgres");
    // default port for the gui wizard
        registerPort(5432);
    // register language for iqm4hd 
        registerLanguage("sql");
        
    // load schema and queries yaml files
        String schema = CharStreams.toString(
            new InputStreamReader(
                PostgresEngine.class.getResourceAsStream(RESOURCES + "postgres_schema.yml"), 
                    StandardCharsets.UTF_8));
        String queries = CharStreams.toString(
            new InputStreamReader(
                PostgresEngine.class.getResourceAsStream(RESOURCES + "postgres_queries.yml"), 
                    StandardCharsets.UTF_8));
                
    // load our repository support, more regarding this class later on
        registerRepositorySupport(new PostgresRepositorySupport(schema, queries));
        
    // using JDBCCommon and linking our icons
        registerGuiSupport(
                GuiConfiguration.ofJDBCCommon(
                        Icon.of(RESOURCES + "POSTGRES.png"), 
                        Icon.of(RESOURCES + "postgres_menu.png")
                        )
                );
    }

    @Override
    protected void loadDatabaseDriver() throws ClassNotFoundException {
    // we must trigger the static initializer within the postgres driver
    // otherwise we will get an exception from the JDBC DriverManager
        Class.forName("org.postgresql.Driver");
    }

    @Override
    public DatabaseFetcher createFetcher(DatabaseConnection connection) {
    // redirect to the already existing JdbcFetcher
        return new JdbcFetcher(connection);
    }

    @Override
    public String name() {
        return "Postgres";
    }

    @Override
    public boolean isRelational() {
        return true;
    }

    @Override
    public boolean allowUseForRepository() {
        return true;
    }

    @Override
    public boolean allowUseForIqm4hd() {
        return true;
    }

    @Override
    public boolean supportsJdbc() {
        return true;
    }

    @Override
    public String createDataSourceUrl(DatabaseConnection connection) {
    // converting our connection to a datasource url
    // postgres specifies their datasource schema here
    // https://jdbc.postgresql.org/documentation/80/connect.html

        StringBuilder sb = new StringBuilder(200);
        String host = connection.getSocket().getFormat() == HostFormat.IPv6 
                    ? String.format("[%s]", connection.getSocket().getHost()) 
                    : connection.getSocket().getHost();
        sb.append(String.format("jdbc:postgresql://%s:%d/%s?user=%s&password=%s",
                host,
                connection.getSocket().getPort(),
                connection.getCredential().getDatabase(),
                connection.getCredential().getUsername(),
                connection.getCredential().getPassword()
        ));
        connection.getCustomParameters().forEach((k, v) -> sb.append(String.format("&%s=%s", k, v)));
        return sb.toString();
    }

    @Override
    public DatabaseTestResult test(DatabaseConnection connection) {
    // redirect to the already existing JDBC test implementation
        return DatabaseTests.ofJDBC(connection);
    }

    @Override
    protected Repository<?> createRepositoryForConnection(DatabaseConnection connection) {
    // we shall use the JdbcRepository implementation that works because we registered
    // our repository support in the constructor
        return new JdbcRepository(connection);
    }
}

```
### Extending YamlConfigurableRelationalRepositorySupport

We implement our own repository support by extending [YamlConfigurableRelationalRepositorySupport](../javaDocs/de/hshannover/dqgui/execution/database/api/YamlConfigurableRelationalRepositorySupport.html) and overriding the `validateRepository` method.

Since we want to validate the repository schema, we hard code our known valid data into the concrete class.

```java
package de.hshannover.dqgui.engine.postgres;

public class PostgresRepositorySupport extends YamlConfigurableRelationalRepositorySupport {
    
    // Tests work by comparing type metadata (int flag) with already recorded correct type identifiers.
    // JDBCRepository provides a <String, Integer> 
    // with table name and column data type that is compared with a should result.
    
    /* 1111 = JSON
     * 1 = char(n)
     * 4 = int
     * 12 = text
     */
    
    public PostgresRepositorySupport(String yamlRawdataSchema, String yamlRawdataQueries) {
        super(yamlRawdataSchema, yamlRawdataQueries);
    }

    // Hard code test data

    @SuppressWarnings("serial")
    private static Map<String, Integer> INDEX = new HashMap<String, Integer>() {{ "{{ " }}
        put("project", 4);
        put("guid", 1);
        put("name", 12);
        put("environments", 1111);
    }};
    
    @SuppressWarnings("serial")
    private static Map<String, Integer> RESULTS = new HashMap<String, Integer>() {{ "{{ " }}
        put("dqgui_meta", 1111);
        put("iqm4hd_report", 1111);
        put("iqm4hd_feedback", 1111);
        put("hash", 1);
        put("project", 4);
    }};
    
    @SuppressWarnings("serial")
    private static Map<String, Integer> REPO = new HashMap<String, Integer>() {{ "{{ " }}
        put("type", 4);
        put("content", 12);
        put("name", 12);
        put("extra_data", 1111);
        put("project", 4);
    }};
    
    @Override
    public ValidationReport validateRepository(Map<String, Integer> index, 
                                               Map<String, Integer> repo, 
                                               Map<String, Integer> results) {

        List<String> errors = new ArrayList<>();
        validateInternal(index, INDEX, "dqgui_index", "index", errors);
        validateInternal(repo, REPO, "dqgui_repo", "repository", errors);
        validateInternal(results, RESULTS, "dqgui_results", "results", errors);
        if(errors.isEmpty())
            return ValidationReport.success();
        return ValidationReport.error(String.join("\n\n", errors));
    }

    private void validateInternal(Map<String, Integer> is, 
                                  Map<String, Integer> should, 
                                  String schemaName, 
                                  String schemaId, 
                                  List<String> errors) {

        if(!Objects.equal(is, should)) {
            errors.add(String.format(
                "Invalid %s schema!%nIs: %s | Should: %s | Should query: %s", 
                schemaName, 
                is, 
                should, 
                getSchema(schemaId)));
        }
    }
}
```

### YAML Configuration

In order to use a YamlConfigurableRelationalRepositorySupport two correct YAML configuration files have to be passed to its constructor.

The constructor validates the configuration files and will throw an exception if a key:value pair is missing or a query does not contain the required named prepared statement parameter.

These postgres sample files should be portable to other relational database systems with minimal changes (listed below).

#### Schema

- json type might not be portable with other engines and could be replaced with a TEXT type
- SERIAL keyword might not be portable with other engines

<hr/>

With the actual schema being:

```yaml
# Postgres Repository Schema
#
# When porting this schema:
# - JSON can also be stored as raw text if the engine does not support a JSON type
# - Column names are hard coded and must not change

# Schema for projects and environments
index: >
  CREATE TABLE dqgui_index(
  project SERIAL,
  name TEXT NOT NULL,
  guid CHAR(36) NOT NULL,
  environments JSON NOT NULL,
  PRIMARY KEY(project)
  );

# Schema for results
results: >
  CREATE TABLE dqgui_results(
  project INTEGER NOT NULL,
  hash CHAR(32) NOT NULL,
  dqgui_meta JSON NOT NULL,
  iqm4hd_report JSON NOT NULL,
  iqm4hd_feedback JSON NOT NULL,
  PRIMARY KEY(project, hash),
  FOREIGN KEY (project) REFERENCES dqgui_index(project)
  );

# Schema for repository
repository: >
  CREATE TABLE dqgui_repo(
  project INTEGER NOT NULL,
  type INTEGER NOT NULL,
  name TEXT NOT NULL,
  content TEXT NOT NULL,
  extra_data JSON NOT NULL,
  PRIMARY KEY(project, type, name),
  FOREIGN KEY (project) REFERENCES dqgui_index(project)
  );
```

#### Queries

- the ::json casts are definitely not portable and have to be removed

Since no other crazy postgres features are used the queries should be portable.
  
<hr/>

With the actual queries being:

```yaml
# This is a configuration file for the YamlConfigurableRelationalRepositorySupport class implementing queries that work with the postgres_schema.yml
#
# YamlConfigurableRelationalRepositorySupport uses prepared statements internally.
#
# The schema and column names are hard coded into the JdbcRepository implementation and must be adhered.
#
# < UNSAFE OPERATIONS >
# These operations do not rely on prepared statements and are thus plainly replaced.
#
# __TABLE__  : Table to query for
# __TYPE_TUPEL__    : Tupel of types for multiple selection (for IN queries used by search)
#
# < PREPARED PARAMETERS>
# :project       : Project identifier associated with query

# :type          : Type of component (Magic Integer defined in DSLComponent enum)
# :content       : text of components source code
# :name          : Name of component
# :new_name      : Name in move (to)
# :old_name      : Name in move(from)
# :extra_data    : Extra data json field
#
# :hash            : Hash identifier of result
# :dqgui_meta      : JSON Serialized DQGUI metadata
# :iqm4hd_report   : JSON Serialized IQM4HD metadata
# :iqm4hd_feedback : JSON Serialized IQM4HD user report feedback
#
# :project_name    : Name of project
# :guid            : guid of project
# :environments    : database environments associated with project
#
# The return aliased as are only required if the common JDBC repository is used. 
# You are free to choose your own identifiers with your own implementation.
#
# As a standard a global check will get the project id 0. So to query for all components it must be searched in the tupel (0, :project).
#
# --------------------------------------------------------------------------------------

# This query should deliver no results.
# It is required to fetch the JDBC meta data set to check if the correct table schema is in place.
#
# Unsafe operation __TABLE__ is required because prepared statements don't allow you to SQL meta program queries.
Q_ALL_COLUMNS_NO_RESULTS: >
  SELECT * FROM __TABLE__ WHERE 1 != 1;

# Check if a component exists by returning the count of matching rows.
# Must query global and project related entries as duplicate name identifiers should not exist
#
# Returns:
# - matches: amount of matches (INTEGER) aliased as 'matches'
Q_COMPONENT_EXISTS: >
  SELECT COUNT(type) AS matches FROM dqgui_repo WHERE type = :type AND name = :name AND project IN (0, :project);

# Create an empty component within the repository schema.
Q_COMPONENT_CREATE_EMPTY: >
  INSERT INTO dqgui_repo (project, type, name, content, extra_data) VALUES (:project, :type, :name, '', '{}');

# Read a component's source from the repository
#
# Returns:
# - content: string content of source aliased as 'content'
Q_COMPONENT_READ: >
  SELECT content FROM dqgui_repo WHERE type = :type AND name = :name AND project = :project;

# Update a components source code and extra data
Q_COMPONENT_UPDATE: >
  UPDATE dqgui_repo SET content = :content, extra_data = :extra_data::json WHERE type = :type AND name = :name AND project = :project;
  
# Update a components extra data only
Q_COMPONENT_UPDATE_EXTRA_DATA_ONLY: >
  UPDATE dqgui_repo SET extra_data = :extra_data::json WHERE type = :type AND name = :name AND project = :project;

# Deletes a component from the repository
Q_COMPONENT_DELETE: >
  DELETE FROM dqgui_repo WHERE type = :type AND name = :name AND project = :project;

# Moves a component from one name to another name
Q_COMPONENT_MOVE: >
  UPDATE dqgui_repo SET name = :new_name WHERE type = :type AND name = :old_name AND project = :project;

# List all components in the repository.
# Only lists type and name not the content.
#
# Returns:
# - project: project id aliased as 'project'
# - type: type identifier aliased as 'type'
# - name: name aliased as 'name'
# - extra_data: extra_data aliased as 'extra_data'
Q_COMPONENT_LIST_ALL: >
  SELECT project, type, name, extra_data FROM dqgui_repo WHERE project IN (0, :project);

# List all hashes within the result table.
#
# Returns:
# - hash: hash identifier string aliased as 'hash'
Q_RESULT_LIST_ALL: >
  SELECT hash FROM dqgui_results WHERE project = :project;

# Delete a result form the result table by using the hash primary key.
Q_RESULT_DELETE: >
  DELETE FROM dqgui_results WHERE hash = :hash AND project = :project;

# Read a result by using the hash primary key.
#
# Returns:
# - iqm4hd_report: Serialized IQM4HD JSON metadata as string aliased as 'iqm4hd_report'
# - dqgui_meta: Serialized DQGUI JSON metadata as string aliased as 'dqgui_meta'
Q_RESULT_READ: >
  SELECT iqm4hd_report, dqgui_meta FROM dqgui_results WHERE hash = :hash AND project = :project;

# Create a result by inserting it into the results table.
Q_RESULT_CREATE: >
  INSERT INTO dqgui_results (project, hash, dqgui_meta, iqm4hd_report, iqm4hd_feedback) VALUES (:project, :hash, :dqgui_meta::json, :iqm4hd_report::json, :iqm4hd_feedback::json);
  
# Request all components and their source that match a given type.
# Multiple types could be queried here.
#
# Unsafe operation __TYPE_TUPEL__ is required to check which types are searched in.
#
# Returns:
# - type:    type identifier aliased as 'type'
# - name:    name aliased as 'name'
# - content: source code of identifier aliased as 'content'
Q_REQUEST_COMPONENTS_FOR_SEARCH: >
  SELECT name, type, content FROM dqgui_repo WHERE type IN __TYPE_TUPEL__ AND project IN (0, :project);

# Create a dummy project with the ID 0 to store global checks with.
Q_PROJECT_CREATE_GLOBAL_DUMMY_ENTRY: >
  INSERT INTO dqgui_index(project, name, guid, environments) VALUES (0, 'GLOBAL_DUMMY', '', '{}');
  
# List all projects excluding the dummy project with the id 0
#
# Returns:
# - project: identifier of the project
# - name   : name of project
# - guid   : guid of project
Q_PROJECT_LIST_ALL: >
  SELECT project, name, guid FROM dqgui_index WHERE project > 0;

# Create a new project.
# It is possible to use :project here if the database does not have an auto increment feature.
# In this postgres sample DEFAULT is used since our schema is backed by a sequence
Q_PROJECT_CREATE: >
  INSERT INTO dqgui_index(project, name, guid, environments) VALUES (DEFAULT, :project_name, :guid, :environments::json);

# Delete all repo entries for a given project id
Q_PROJECT_DELETE_REPO: >
  DELETE FROM dqgui_repo WHERE project = :project;

# Delete all results for a given project id
Q_PROJECT_DELETE_RESULTS: >
  DELETE FROM dqgui_results WHERE project = :project;

# Delete the project entry itself
Q_PROJECT_DELETE_PROJECT_ENTRY: >
  DELETE FROM dqgui_index WHERE project = :project;

#Fetch database environments for project
#
# Returns:
# - environments : serialized database environment as json
Q_PROJECT_FETCH_DATABASE_ENVIRONMENTS: >
  SELECT environments FROM dqgui_index WHERE project = :project;

# Updated database environments for a project
Q_PROJECT_UPDATE_DATABASE_ENVIRONMENTS: >
  UPDATE dqgui_index SET environments = :environments::json WHERE project = :project;
  
# Check if a project exists by returning the count of matching rows.
#
# Returns:
# - matches: amount of matches (INTEGER) aliased as 'matches'
Q_PROJECT_EXISTS: >
  SELECT COUNT(project) AS matches FROM dqgui_index WHERE project = :project;
  
# Read feedback for hash
#
# Returns:
# iqm4hd_feedback : serialized feedback as json
Q_FEEDBACK_READ: >
  SELECT iqm4hd_feedback FROM dqgui_results WHERE hash = :hash AND project = :project;
  
# Write feedback for hash
Q_FEEDBACK_WRITE: >
  UPDATE dqgui_results SET iqm4hd_feedback = :iqm4hd_feedback::json WHERE hash = :hash AND project = :project;
  
Q_PROMOTION_TO_GLOBAL: >
  UPDATE dqgui_repo SET project = 0 WHERE project = :project AND type = :type AND name = :name;
  
Q_DEMOTION_TO_LOCAL: >
  UPDATE dqgui_repo SET project = :project WHERE project = 0 AND type = :type AND name = :name;
```

### Result

After implementing all of this we should have a working Postgres engine implementation with repository support.

In case you want to output a repository related query you can set the log level in `dqgui-core/src/main/resources/tinylog.properties` to debug.