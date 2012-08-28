package test.support.com.pyxis.petstore.db;

import com.carbonfive.db.migration.DataSourceMigrationManager;
import com.carbonfive.db.migration.ResourceMigrationResolver;

import javax.sql.DataSource;
import java.util.Properties;

public class DatabaseMigrator {

    private final MigrationProperties properties;

    public DatabaseMigrator(Properties properties) {
        this.properties = new MigrationProperties(properties);
    }

    public void migrate(DataSource dataSource) {
        DataSourceMigrationManager migrationManager = new DataSourceMigrationManager(dataSource);
        migrationManager.setMigrationResolver(new ResourceMigrationResolver(properties.migrationsPath()));
        migrationManager.migrate();
    }
}
