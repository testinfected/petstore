package test.support.com.pyxis.petstore.db;

import com.carbonfive.db.migration.DataSourceMigrationManager;
import com.carbonfive.db.migration.ResourceMigrationResolver;

import javax.sql.DataSource;
import java.util.Properties;

public class DatabaseMigrator {

    private final DataSource dataSource;

    public DatabaseMigrator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void migrate() {
        DataSourceMigrationManager migrationManager = new DataSourceMigrationManager(dataSource);
        migrationManager.setMigrationResolver(new ResourceMigrationResolver());
        migrationManager.migrate();
    }
}
