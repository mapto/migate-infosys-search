package it.unimi.dllcm.migate.service.criteria;

import it.unimi.dllcm.migate.domain.enumeration.Country;
import it.unimi.dllcm.migate.domain.enumeration.Gender;
import it.unimi.dllcm.migate.domain.enumeration.Language;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link it.unimi.dllcm.migate.domain.Person} entity. This class is used
 * in {@link it.unimi.dllcm.migate.web.rest.PersonResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /people?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PersonCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Gender
     */
    public static class GenderFilter extends Filter<Gender> {

        public GenderFilter() {}

        public GenderFilter(GenderFilter filter) {
            super(filter);
        }

        @Override
        public GenderFilter copy() {
            return new GenderFilter(this);
        }
    }

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

    /**
     * Class for filtering Language
     */
    public static class LanguageFilter extends Filter<Language> {

        public LanguageFilter() {}

        public LanguageFilter(LanguageFilter filter) {
            super(filter);
        }

        @Override
        public LanguageFilter copy() {
            return new LanguageFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private GenderFilter gender;

    private LocalDateFilter dob;

    private LocalDateFilter dod;

    private CountryFilter country;

    private LanguageFilter language;

    private LongFilter jobId;

    private Boolean distinct;

    public PersonCriteria() {}

    public PersonCriteria(PersonCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.gender = other.gender == null ? null : other.gender.copy();
        this.dob = other.dob == null ? null : other.dob.copy();
        this.dod = other.dod == null ? null : other.dod.copy();
        this.country = other.country == null ? null : other.country.copy();
        this.language = other.language == null ? null : other.language.copy();
        this.jobId = other.jobId == null ? null : other.jobId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public PersonCriteria copy() {
        return new PersonCriteria(this);
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

    public GenderFilter getGender() {
        return gender;
    }

    public GenderFilter gender() {
        if (gender == null) {
            gender = new GenderFilter();
        }
        return gender;
    }

    public void setGender(GenderFilter gender) {
        this.gender = gender;
    }

    public LocalDateFilter getDob() {
        return dob;
    }

    public LocalDateFilter dob() {
        if (dob == null) {
            dob = new LocalDateFilter();
        }
        return dob;
    }

    public void setDob(LocalDateFilter dob) {
        this.dob = dob;
    }

    public LocalDateFilter getDod() {
        return dod;
    }

    public LocalDateFilter dod() {
        if (dod == null) {
            dod = new LocalDateFilter();
        }
        return dod;
    }

    public void setDod(LocalDateFilter dod) {
        this.dod = dod;
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

    public LanguageFilter getLanguage() {
        return language;
    }

    public LanguageFilter language() {
        if (language == null) {
            language = new LanguageFilter();
        }
        return language;
    }

    public void setLanguage(LanguageFilter language) {
        this.language = language;
    }

    public LongFilter getJobId() {
        return jobId;
    }

    public LongFilter jobId() {
        if (jobId == null) {
            jobId = new LongFilter();
        }
        return jobId;
    }

    public void setJobId(LongFilter jobId) {
        this.jobId = jobId;
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
        final PersonCriteria that = (PersonCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(gender, that.gender) &&
            Objects.equals(dob, that.dob) &&
            Objects.equals(dod, that.dod) &&
            Objects.equals(country, that.country) &&
            Objects.equals(language, that.language) &&
            Objects.equals(jobId, that.jobId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, gender, dob, dod, country, language, jobId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PersonCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (gender != null ? "gender=" + gender + ", " : "") +
            (dob != null ? "dob=" + dob + ", " : "") +
            (dod != null ? "dod=" + dod + ", " : "") +
            (country != null ? "country=" + country + ", " : "") +
            (language != null ? "language=" + language + ", " : "") +
            (jobId != null ? "jobId=" + jobId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
