package it.unimi.dllcm.migate.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.unimi.dllcm.migate.domain.enumeration.Country;
import it.unimi.dllcm.migate.domain.enumeration.Gender;
import it.unimi.dllcm.migate.domain.enumeration.Language;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Person.
 */
@Entity
@Table(name = "person")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "person")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Person implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "dod")
    private LocalDate dod;

    @Enumerated(EnumType.STRING)
    @Column(name = "country")
    private Country country;

    @Enumerated(EnumType.STRING)
    @Column(name = "language")
    private Language language;

    @ManyToMany
    @JoinTable(name = "rel_person__work", joinColumns = @JoinColumn(name = "person_id"), inverseJoinColumns = @JoinColumn(name = "work_id"))
    @JsonIgnoreProperties(value = { "sponsors", "workRoleNames", "collections", "responsibles" }, allowSetters = true)
    private Set<Work> works = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "rel_person__responsibility",
        joinColumns = @JoinColumn(name = "person_id"),
        inverseJoinColumns = @JoinColumn(name = "responsibility_id")
    )
    @JsonIgnoreProperties(value = { "institutionNames", "people", "workNames" }, allowSetters = true)
    private Set<Role> responsibilities = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Person id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Person name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gender getGender() {
        return this.gender;
    }

    public Person gender(Gender gender) {
        this.setGender(gender);
        return this;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getDob() {
        return this.dob;
    }

    public Person dob(LocalDate dob) {
        this.setDob(dob);
        return this;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public LocalDate getDod() {
        return this.dod;
    }

    public Person dod(LocalDate dod) {
        this.setDod(dod);
        return this;
    }

    public void setDod(LocalDate dod) {
        this.dod = dod;
    }

    public Country getCountry() {
        return this.country;
    }

    public Person country(Country country) {
        this.setCountry(country);
        return this;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Language getLanguage() {
        return this.language;
    }

    public Person language(Language language) {
        this.setLanguage(language);
        return this;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Set<Work> getWorks() {
        return this.works;
    }

    public void setWorks(Set<Work> works) {
        this.works = works;
    }

    public Person works(Set<Work> works) {
        this.setWorks(works);
        return this;
    }

    public Person addWork(Work work) {
        this.works.add(work);
        work.getResponsibles().add(this);
        return this;
    }

    public Person removeWork(Work work) {
        this.works.remove(work);
        work.getResponsibles().remove(this);
        return this;
    }

    public Set<Role> getResponsibilities() {
        return this.responsibilities;
    }

    public void setResponsibilities(Set<Role> roles) {
        this.responsibilities = roles;
    }

    public Person responsibilities(Set<Role> roles) {
        this.setResponsibilities(roles);
        return this;
    }

    public Person addResponsibility(Role role) {
        this.responsibilities.add(role);
        role.getPeople().add(this);
        return this;
    }

    public Person removeResponsibility(Role role) {
        this.responsibilities.remove(role);
        role.getPeople().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Person)) {
            return false;
        }
        return id != null && id.equals(((Person) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Person{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", gender='" + getGender() + "'" +
            ", dob='" + getDob() + "'" +
            ", dod='" + getDod() + "'" +
            ", country='" + getCountry() + "'" +
            ", language='" + getLanguage() + "'" +
            "}";
    }
}
