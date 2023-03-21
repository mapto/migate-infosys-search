package it.unimi.dllcm.migate.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link it.unimi.dllcm.migate.domain.Work} entity. This class is used
 * in {@link it.unimi.dllcm.migate.web.rest.WorkResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /works?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WorkCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private LocalDateFilter published;

    private LongFilter sponsorId;

    private LongFilter workRoleNameId;

    private LongFilter collectionId;

    private LongFilter responsibleId;

    private Boolean distinct;

    public WorkCriteria() {}

    public WorkCriteria(WorkCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.published = other.published == null ? null : other.published.copy();
        this.sponsorId = other.sponsorId == null ? null : other.sponsorId.copy();
        this.workRoleNameId = other.workRoleNameId == null ? null : other.workRoleNameId.copy();
        this.collectionId = other.collectionId == null ? null : other.collectionId.copy();
        this.responsibleId = other.responsibleId == null ? null : other.responsibleId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public WorkCriteria copy() {
        return new WorkCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public LocalDateFilter getPublished() {
        return published;
    }

    public LocalDateFilter published() {
        if (published == null) {
            published = new LocalDateFilter();
        }
        return published;
    }

    public void setPublished(LocalDateFilter published) {
        this.published = published;
    }

    public LongFilter getSponsorId() {
        return sponsorId;
    }

    public LongFilter sponsorId() {
        if (sponsorId == null) {
            sponsorId = new LongFilter();
        }
        return sponsorId;
    }

    public void setSponsorId(LongFilter sponsorId) {
        this.sponsorId = sponsorId;
    }

    public LongFilter getWorkRoleNameId() {
        return workRoleNameId;
    }

    public LongFilter workRoleNameId() {
        if (workRoleNameId == null) {
            workRoleNameId = new LongFilter();
        }
        return workRoleNameId;
    }

    public void setWorkRoleNameId(LongFilter workRoleNameId) {
        this.workRoleNameId = workRoleNameId;
    }

    public LongFilter getCollectionId() {
        return collectionId;
    }

    public LongFilter collectionId() {
        if (collectionId == null) {
            collectionId = new LongFilter();
        }
        return collectionId;
    }

    public void setCollectionId(LongFilter collectionId) {
        this.collectionId = collectionId;
    }

    public LongFilter getResponsibleId() {
        return responsibleId;
    }

    public LongFilter responsibleId() {
        if (responsibleId == null) {
            responsibleId = new LongFilter();
        }
        return responsibleId;
    }

    public void setResponsibleId(LongFilter responsibleId) {
        this.responsibleId = responsibleId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final WorkCriteria that = (WorkCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(published, that.published) &&
            Objects.equals(sponsorId, that.sponsorId) &&
            Objects.equals(workRoleNameId, that.workRoleNameId) &&
            Objects.equals(collectionId, that.collectionId) &&
            Objects.equals(responsibleId, that.responsibleId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, published, sponsorId, workRoleNameId, collectionId, responsibleId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WorkCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (published != null ? "published=" + published + ", " : "") +
            (sponsorId != null ? "sponsorId=" + sponsorId + ", " : "") +
            (workRoleNameId != null ? "workRoleNameId=" + workRoleNameId + ", " : "") +
            (collectionId != null ? "collectionId=" + collectionId + ", " : "") +
            (responsibleId != null ? "responsibleId=" + responsibleId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
