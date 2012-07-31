package test.support.com.pyxis.petstore.db;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.testinfected.hamcrest.jpa.Reflection;
import test.support.com.pyxis.petstore.builders.Builder;

import javax.persistence.Id;
import java.lang.reflect.Field;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.testinfected.hamcrest.jpa.SamePersistentFieldsAs.samePersistentFieldsAs;

public class Database {

    private Session session;

    public Database(Session session) {
        this.session = session;
    }

    public void close() {
        session.close();
    }

    public void given(final Builder<?>... builders) {
        persist(builders);
    }

    public void persist(final Builder<?>... builders) {
        for (final Builder<?> builder : builders) {
            persist(builder.build());
        }
    }

    public void given(final Object... entities) {
        persist(entities);
    }

    public void persist(final Object... entities) {
        for (final Object entity : entities) {
            perform(new UnitOfWork() {
                public void work(Session session) {
                    session.save(entity);
                }
            });
        }
        clearCache();
    }

    private void clearCache() {
        session.clear();
    }

    public void perform(UnitOfWork work) {
        Transaction transaction = session.beginTransaction();
        try {
            work.work(session);
            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
            throw e;
        }
    }

    public void assertCanBeReloadedWithSameState(final Object original) {
        assertCanBeReloadedWithSameState("entity", original);
    }

    public void assertCanBeReloadedWithSameState(final String entityName, final Object original) {
        perform(new UnitOfWork() {
            public void work(Session session) {
                Object loaded = session.get(original.getClass(), idOf(original));
                assertThat(entityName, loaded, samePersistentFieldsAs(original));
            }
        });
    }

    public static long idOf(Object entity) {
        Class<?> type = entity.getClass();
        while (type != Object.class) {
            Field id = getId(type);
            if (id != null) return (Long) Reflection.readField(entity, id);
            type = type.getSuperclass();
        }
        throw new IllegalArgumentException("Entity has no id : " + entity);
    }

    private static Field getId(Class<?> type) {
        Field[] fields = type.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(Id.class) != null) return field;
        }
        return null;
    }
}
