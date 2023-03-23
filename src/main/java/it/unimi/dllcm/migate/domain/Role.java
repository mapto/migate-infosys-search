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

    @OneToMany(mappedBy = "position")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "sites", "position" }, allowSetters = true)
    private Set<Institution> sponsors = new HashSet<>();

    @OneToMany(mappedBy = "job")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "job" }, allowSetters = true)
    private Set<Person> people = new HashSet<>();

    @OneToMany(mappedBy = "responsibility")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "collection", "responsibility" }, allowSetters = true)
    private Set<Work> products = new HashSet<>();

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

    public Set<Institution> getSponsors() {
        return this.sponsors;
    }

    public void setSponsors(Set<Institution> institutions) {
        if (this.sponsors != null) {
            this.sponsors.forEach(i -> i.setPosition(null));
        }
        if (institutions != null) {
            institutions.forEach(i -> i.setPosition(this));
        }
        this.sponsors = institutions;
    }

    public Role sponsors(Set<Institution> institutions) {
        this.setSponsors(institutions);
        return this;
    }

    public Role addSponsor(Institution institution) {
        this.sponsors.add(institution);
        institution.setPosition(this);
        return this;
    }

    public Role removeSponsor(Institution institution) {
        this.sponsors.remove(institution);
        institution.setPosition(null);
        return this;
    }

    public Set<Person> getPeople() {
        return this.people;
    }

    public void setPeople(Set<Person> people) {
        if (this.people != null) {
            this.people.forEach(i -> i.setJob(null));
        }
        if (people != null) {
            people.forEach(i -> i.setJob(this));
        }
        this.people = people;
    }

    public Role people(Set<Person> people) {
        this.setPeople(people);
        return this;
    }

    public Role addPerson(Person person) {
        this.people.add(person);
        person.setJob(this);
        return this;
    }

    public Role removePerson(Person person) {
        this.people.remove(person);
        person.setJob(null);
        return this;
    }

    public Set<Work> getProducts() {
        return this.products;
    }

    public void setProducts(Set<Work> works) {
        if (this.products != null) {
            this.products.forEach(i -> i.setResponsibility(null));
        }
        if (works != null) {
            works.forEach(i -> i.setResponsibility(this));
        }
        this.products = works;
    }

    public Role products(Set<Work> works) {
        this.setProducts(works);
        return this;
    }

    public Role addProduct(Work work) {
        this.products.add(work);
        work.setResponsibility(this);
        return this;
    }

    public Role removeProduct(Work work) {
        this.products.remove(work);
        work.setResponsibility(null);
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
