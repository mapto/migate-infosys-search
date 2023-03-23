package it.unimi.dllcm.migate.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import it.unimi.dllcm.migate.domain.WorkGroup;
import it.unimi.dllcm.migate.repository.WorkGroupRepository;
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
 * Spring Data Elasticsearch repository for the {@link WorkGroup} entity.
 */
public interface WorkGroupSearchRepository extends ElasticsearchRepository<WorkGroup, Long>, WorkGroupSearchRepositoryInternal {}

interface WorkGroupSearchRepositoryInternal {
    Page<WorkGroup> search(String query, Pageable pageable);

    Page<WorkGroup> search(Query query);

    void index(WorkGroup entity);
}

class WorkGroupSearchRepositoryInternalImpl implements WorkGroupSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final WorkGroupRepository repository;

    WorkGroupSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, WorkGroupRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<WorkGroup> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<WorkGroup> search(Query query) {
        SearchHits<WorkGroup> searchHits = elasticsearchTemplate.search(query, WorkGroup.class);
        List<WorkGroup> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(WorkGroup entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
