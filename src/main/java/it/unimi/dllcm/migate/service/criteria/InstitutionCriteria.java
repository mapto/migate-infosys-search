package it.unimi.dllcm.migate.service.criteria;

import it.unimi.dllcm.migate.domain.enumeration.Country;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link it.unimi.dllcm.migate.domain.Institution} entity. This class is used
 * in {@link it.unimi.dllcm.migate.web.rest.InstitutionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /institutions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InstitutionCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Country
     */
    public static class CountryFilter extends Filter<Country> {

        public CountryFilter() {}

        public CountryFilter(CountryFilter filter) {
            super(filter);
        }

        @Override
        public CountryFilter copy() {
            return new CountryFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private CountryFilter country;

    private LongFilter siteId;

    private LongFilter positionId;

    private Boolean distinct;

    public InstitutionCriteria() {}

    public InstitutionCriteria(InstitutionCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.country = other.country == null ? null : other.country.copy();
        this.siteId = other.siteId == null ? null : other.siteId.copy();
        this.positionId = other.positionId == null ? null : other.positionId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public InstitutionCriteria copy() {
        return new InstitutionCriteria(this);
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

    public CountryFilter getCountry() {
        return country;
    }

    public CountryFilter country() {
        if (country == null) {
            country = new CountryFilter();
        }
        return country;
    }

    public void setCountry(CountryFilter country) {
        this.country = country;
    }

    public LongFilter getSiteId() {
        return siteId;
    }

    public LongFilter siteId() {
        if (siteId == null) {
            siteId = new LongFilter();
        }
        return siteId;
    }

    public void setSiteId(LongFilter siteId) {
        this.siteId = siteId;
    }

    public LongFilter getPositionId() {
        return positionId;
    }

    public LongFilter positionId() {
        if (positionId == null) {
            positionId = new LongFilter();
        }
        return positionId;
    }

    public void setPositionId(LongFilter positionId) {
        this.positionId = positionId;
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
        final InstitutionCriteria that = (InstitutionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(country, that.country) &&
            Objects.equals(siteId, that.siteId) &&
            Objects.equals(positionId, that.positionId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, country, siteId, positionId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InstitutionCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (country != null ? "country=" + country + ", " : "") +
            (siteId != null ? "siteId=" + siteId + ", " : "") +
            (positionId != null ? "positionId=" + positionId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
