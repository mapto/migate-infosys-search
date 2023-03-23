package it.unimi.dllcm.migate.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import it.unimi.dllcm.migate.domain.Institution;
import it.unimi.dllcm.migate.repository.InstitutionRepository;
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
 * Spring Data Elasticsearch repository for the {@link Institution} entity.
 */
public interface InstitutionSearchRepository extends ElasticsearchRepository<Institution, Long>, InstitutionSearchRepositoryInternal {}

interface InstitutionSearchRepositoryInternal {
    Page<Institution> search(String query, Pageable pageable);

    Page<Institution> search(Query query);

    void index(Institution entity);
}

class InstitutionSearchRepositoryInternalImpl implements InstitutionSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final InstitutionRepository repository;

    InstitutionSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, InstitutionRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Institution> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<Institution> search(Query query) {
        SearchHits<Institution> searchHits = elasticsearchTemplate.search(query, Institution.class);
        List<Institution> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Institution entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
