package it.unimi.dllcm.migate.repository;

import it.unimi.dllcm.migate.domain.Person;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class PersonRepositoryWithBagRelationshipsImpl implements PersonRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Person> fetchBagRelationships(Optional<Person> person) {
        return person.map(this::fetchWorks).map(this::fetchResponsibilities);
    }

    @Override
    public Page<Person> fetchBagRelationships(Page<Person> people) {
        return new PageImpl<>(fetchBagRelationships(people.getContent()), people.getPageable(), people.getTotalElements());
    }

    @Override
    public List<Person> fetchBagRelationships(List<Person> people) {
        return Optional.of(people).map(this::fetchWorks).map(this::fetchResponsibilities).orElse(Collections.emptyList());
    }

    Person fetchWorks(Person result) {
        return entityManager
            .createQuery("select person from Person person left join fetch person.works where person is :person", Person.class)
            .setParameter("person", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Person> fetchWorks(List<Person> people) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, people.size()).forEach(index -> order.put(people.get(index).getId(), index));
        List<Person> result = entityManager
            .createQuery("select distinct person from Person person left join fetch person.works where person in :people", Person.class)
            .setParameter("people", people)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }

    Person fetchResponsibilities(Person result) {
        return entityManager
            .createQuery("select person from Person person left join fetch person.responsibilities where person is :person", Person.class)
            .setParameter("person", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Person> fetchResponsibilities(List<Person> people) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, people.size()).forEach(index -> order.put(people.get(index).getId(), index));
        List<Person> result = entityManager
            .createQuery(
                "select distinct person from Person person left join fetch person.responsibilities where person in :people",
                Person.class
            )
            .setParameter("people", people)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
