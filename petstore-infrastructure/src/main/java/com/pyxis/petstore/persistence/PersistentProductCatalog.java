package com.pyxis.petstore.persistence;

import com.pyxis.petstore.Maybe;
import com.pyxis.petstore.domain.product.Product;
import com.pyxis.petstore.domain.product.ProductCatalog;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.pyxis.petstore.Maybe.possibly;
import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.ilike;
import static org.hibernate.criterion.Restrictions.or;

@Repository
public class PersistentProductCatalog implements ProductCatalog {

    private final SessionFactory sessionFactory;

    @Autowired
    public PersistentProductCatalog(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    public void add(Product product) {
        currentSession().save(product);
    }

    @Transactional(readOnly = true)
    public Maybe<Product> findByNumber(String number) {
        return possibly((Product) currentSession().createCriteria(Product.class).
                add(eq("number", number)).
                uniqueResult());
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<Product> findByKeyword(String keyword) {
        return currentSession().createCriteria(Product.class)
                .add(or(fieldMatchesKeyword("name", keyword),
                        fieldMatchesKeyword("description", keyword)))
                .list();
    }

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    private Criterion fieldMatchesKeyword(String field, String keyword) {
        return ilike(field, keyword, MatchMode.ANYWHERE);
    }
}