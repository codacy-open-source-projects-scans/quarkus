package io.quarkus.spring.data.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.common.runtime.AbstractJpaOperations;

@SuppressWarnings("unused") // the methods of this class are invoked in the generated bytecode
public final class RepositorySupport {

    private RepositorySupport() {
    }

    public static List<?> findByIds(AbstractJpaOperations<PanacheQuery<?>> operations, Class<?> entityClass,
            Iterable<?> ids) {
        Objects.requireNonNull(ids);
        List<Object> result = new ArrayList<>();
        for (Object id : ids) {
            Object byId = operations.findById(entityClass, id);
            if (byId != null) {
                result.add(byId);
            }
        }
        return result;
    }

    public static List<?> findByIds(AbstractJpaOperations<PanacheQuery<?>> operations, Class<?> entityClass,
            String idField, Iterable<Long> ids) {
        Objects.requireNonNull(ids);
        return operations.find(entityClass, String.format("%s in ?1", idField), ids).list();
    }

    public static void deleteAll(AbstractJpaOperations<PanacheQuery<?>> operations, Iterable<?> entities) {
        for (Object entity : entities) {
            operations.delete(entity);
        }
    }

    /**
     * Add call to the Panache method implementing the actual retrieving of a reference to an entity with the given class and
     * identifier.
     *
     * @param operations an instance of {@code AbstractJpaOperations} used to perform JPA operations
     * @param entityClass the {@code Class} object of the entity type to be retrieved
     * @param id the identifier of the entity to be retrieved
     * @return a reference to the entity of the specified class with the given identifier
     * @deprecated use {@link RepositorySupport#getReferenceById)} instead.
     */
    @Deprecated
    public static Object getOne(AbstractJpaOperations<PanacheQuery<?>> operations, Class<?> entityClass, Object id) {
        return getReferenceById(operations, entityClass, id);
    }

    /**
     * Add call to the Panache method implementing the actual retrieving of a reference to an entity with the given class and
     * identifier.
     *
     * @param operations an instance of {@code AbstractJpaOperations} used to perform JPA operations
     * @param entityClass the {@code Class} object of the entity type to be retrieved
     * @param id the identifier of the entity to be retrieved
     * @return a reference to the entity of the specified class with the given identifier
     * @deprecated use {@link RepositorySupport#getReferenceById)} instead.
     */
    @Deprecated
    public static Object getById(AbstractJpaOperations<PanacheQuery<?>> operations, Class<?> entityClass, Object id) {
        return getReferenceById(operations, entityClass, id);
    }

    public static Object getReferenceById(AbstractJpaOperations<PanacheQuery<?>> operations, Class<?> entityClass, Object id) {
        return operations.getSession(entityClass).getReference(entityClass, id);
    }

    public static void clear(Class<?> clazz) {
        Panache.getSession(clazz).clear();
    }

    public static void flush(Class<?> clazz) {
        Panache.getSession(clazz).flush();
    }
}