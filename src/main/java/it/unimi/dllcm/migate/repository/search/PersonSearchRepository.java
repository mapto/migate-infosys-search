package it.unimi.dllcm.migate.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import it.unimi.dllcm.migate.domain.Person;
import it.unimi.dllcm.migate.repository.PersonRepository;
import java.util.List;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data Elasticsearch repository for the {@link Person} entity.
 */
public interface PersonSearchRepository extends ElasticsearchRepository<Person, Long>, PersonSearchRepositoryInternal {}

interface PersonSearchRepositoryInternal {
    Page<Person> search(String query, Pageable pageable);

    Page<Person> search(Query query);

    void index(Person entity);
}

class PersonSearchRepositoryInternalImpl implements PersonSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final PersonRepository repository;

    PersonSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, PersonRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Person> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<Person> search(Query query) {
        SearchHits<Person> searchHits = elasticsearchTemplate.search(query, Person.class);
        List<Person> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Person entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
