package it.unimi.dllcm.migate.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Role.
 */
@Entity
@Table(name = "role")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "role")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "start")
    private LocalDate start;

    @Column(name = "jhi_end")
    private LocalDate end;

    @ManyToMany(mappedBy = "institutionRoleNames")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "institutionRoleNames", "site", "products" }, allowSetters = true)
    private Set<Institution> institutionNames = new HashSet<>();

    @ManyToMany(mappedBy = "responsibilities")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "works", "responsibilities" }, allowSetters = true)
    private Set<Person> people = new HashSet<>();

    @ManyToMany(mappedBy = "workRoleNames")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "sponsors", "workRoleNames", "collections", "responsibles" }, allowSetters = true)
    private Set<Work> workNames = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Role id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Role name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStart() {
        return this.start;
    }

    public Role start(LocalDate start) {
        this.setStart(start);
        return this;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return this.end;
    }

    public Role end(LocalDate end) {
        this.setEnd(end);
        return this;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public Set<Institution> getInstitutionNames() {
        return this.institutionNames;
    }

    public void setInstitutionNames(Set<Institution> institutions) {
        if (this.institutionNames != null) {
            this.institutionNames.forEach(i -> i.removeInstitutionRoleName(this));
        }
        if (institutions != null) {
            institutions.forEach(i -> i.addInstitutionRoleName(this));
        }
        this.institutionNames = institutions;
    }

    public Role institutionNames(Set<Institution> institutions) {
        this.setInstitutionNames(institutions);
        return this;
    }

    public Role addInstitutionName(Institution institution) {
        this.institutionNames.add(institution);
        institution.getInstitutionRoleNames().add(this);
        return this;
    }

    public Role removeInstitutionName(Institution institution) {
        this.institutionNames.remove(institution);
        institution.getInstitutionRoleNames().remove(this);
        return this;
    }

    public Set<Person> getPeople() {
        return this.people;
    }

    public void setPeople(Set<Person> people) {
        if (this.people != null) {
            this.people.forEach(i -> i.removeResponsibility(this));
        }
        if (people != null) {
            people.forEach(i -> i.addResponsibility(this));
        }
        this.people = people;
    }

    public Role people(Set<Person> people) {
        this.setPeople(people);
        return this;
    }

    public Role addPerson(Person person) {
        this.people.add(person);
        person.getResponsibilities().add(this);
        return this;
    }

    public Role removePerson(Person person) {
        this.people.remove(person);
        person.getResponsibilities().remove(this);
        return this;
    }

    public Set<Work> getWorkNames() {
        return this.workNames;
    }

    public void setWorkNames(Set<Work> works) {
        if (this.workNames != null) {
            this.workNames.forEach(i -> i.removeWorkRoleName(this));
        }
        if (works != null) {
            works.forEach(i -> i.addWorkRoleName(this));
        }
        this.workNames = works;
    }

    public Role workNames(Set<Work> works) {
        this.setWorkNames(works);
        return this;
    }

    public Role addWorkName(Work work) {
        this.workNames.add(work);
        work.getWorkRoleNames().add(this);
        return this;
    }

    public Role removeWorkName(Work work) {
        this.workNames.remove(work);
        work.getWorkRoleNames().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Role)) {
            return false;
        }
        return id != null && id.equals(((Role) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Role{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", start='" + getStart() + "'" +
            ", end='" + getEnd() + "'" +
            "}";
    }
}
