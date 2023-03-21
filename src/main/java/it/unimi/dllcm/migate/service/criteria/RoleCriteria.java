package it.unimi.dllcm.migate.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link it.unimi.dllcm.migate.domain.Role} entity. This class is used
 * in {@link it.unimi.dllcm.migate.web.rest.RoleResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /roles?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RoleCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private LocalDateFilter start;

    private LocalDateFilter end;

    private LongFilter institutionNameId;

    private LongFilter personId;

    private LongFilter workNameId;

    private Boolean distinct;

    public RoleCriteria() {}

    public RoleCriteria(RoleCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.start = other.start == null ? null : other.start.copy();
        this.end = other.end == null ? null : other.end.copy();
        this.institutionNameId = other.institutionNameId == null ? null : other.institutionNameId.copy();
        this.personId = other.personId == null ? null : other.personId.copy();
        this.workNameId = other.workNameId == null ? null : other.workNameId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public RoleCriteria copy() {
        return new RoleCriteria(this);
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

    public LocalDateFilter getStart() {
        return start;
    }

    public LocalDateFilter start() {
        if (start == null) {
            start = new LocalDateFilter();
        }
        return start;
    }

    public void setStart(LocalDateFilter start) {
        this.start = start;
    }

    public LocalDateFilter getEnd() {
        return end;
    }

    public LocalDateFilter end() {
        if (end == null) {
            end = new LocalDateFilter();
        }
        return end;
    }

    public void setEnd(LocalDateFilter end) {
        this.end = end;
    }

    public LongFilter getInstitutionNameId() {
        return institutionNameId;
    }

    public LongFilter institutionNameId() {
        if (institutionNameId == null) {
            institutionNameId = new LongFilter();
        }
        return institutionNameId;
    }

    public void setInstitutionNameId(LongFilter institutionNameId) {
        this.institutionNameId = institutionNameId;
    }

    public LongFilter getPersonId() {
        return personId;
    }

    public LongFilter personId() {
        if (personId == null) {
            personId = new LongFilter();
        }
        return personId;
    }

    public void setPersonId(LongFilter personId) {
        this.personId = personId;
    }

    public LongFilter getWorkNameId() {
        return workNameId;
    }

    public LongFilter workNameId() {
        if (workNameId == null) {
            workNameId = new LongFilter();
        }
        return workNameId;
    }

    public void setWorkNameId(LongFilter workNameId) {
        this.workNameId = workNameId;
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
        final RoleCriteria that = (RoleCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(start, that.start) &&
            Objects.equals(end, that.end) &&
            Objects.equals(institutionNameId, that.institutionNameId) &&
            Objects.equals(personId, that.personId) &&
            Objects.equals(workNameId, that.workNameId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, start, end, institutionNameId, personId, workNameId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RoleCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (start != null ? "start=" + start + ", " : "") +
            (end != null ? "end=" + end + ", " : "") +
            (institutionNameId != null ? "institutionNameId=" + institutionNameId + ", " : "") +
            (personId != null ? "personId=" + personId + ", " : "") +
            (workNameId != null ? "workNameId=" + workNameId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
