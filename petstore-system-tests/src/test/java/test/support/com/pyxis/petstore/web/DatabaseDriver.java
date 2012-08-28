package test.support.com.pyxis.petstore.web;

import org.hibernate.SessionFactory;
import test.support.com.pyxis.petstore.builders.Builder;
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


    public void add(Builder<?>... entities) {
        for (final Builder<?> entity : entities) {
            add(entity.build());
        }
    }

    public void add(Object... entities) {
        database.persist(entities);
    }
}
