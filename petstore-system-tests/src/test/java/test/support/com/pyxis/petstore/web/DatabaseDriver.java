package test.support.com.pyxis.petstore.web;

import org.hibernate.SessionFactory;
import test.support.com.pyxis.petstore.db.Database;

public class DatabaseDriver {

    private final SessionFactory sessionFactory;

    private Database database;

    public DatabaseDriver(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void start() {
        database = Database.connect(sessionFactory);
        database.clean();
    }

    public void stop() {
        database.close();
    }

}
