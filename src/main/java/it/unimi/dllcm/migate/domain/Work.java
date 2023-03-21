package it.unimi.dllcm.migate.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Work.
 */
@Entity
@Table(name = "work")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "work")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Work implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "published")
    private LocalDate published;

    @ManyToMany
    @JoinTable(
        name = "rel_work__sponsor",
        joinColumns = @JoinColumn(name = "work_id"),
        inverseJoinColumns = @JoinColumn(name = "sponsor_id")
    )
    @JsonIgnoreProperties(value = { "institutionRoleNames", "site", "products" }, allowSetters = true)
    private Set<Institution> sponsors = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "rel_work__work_role_name",
        joinColumns = @JoinColumn(name = "work_id"),
        inverseJoinColumns = @JoinColumn(name = "work_role_name_id")
    )
    @JsonIgnoreProperties(value = { "institutionNames", "people", "workNames" }, allowSetters = true)
    private Set<Role> workRoleNames = new HashSet<>();

    @OneToMany(mappedBy = "work")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "work" }, allowSetters = true)
    private Set<WorkGroup> collections = new HashSet<>();

    @ManyToMany(mappedBy = "works")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "works", "responsibilities" }, allowSetters = true)
    private Set<Person> responsibles = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Work id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Work name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getPublished() {
        return this.published;
    }

    public Work published(LocalDate published) {
        this.setPublished(published);
        return this;
    }

    public void setPublished(LocalDate published) {
        this.published = published;
    }

    public Set<Institution> getSponsors() {
        return this.sponsors;
    }

    public void setSponsors(Set<Institution> institutions) {
        this.sponsors = institutions;
    }

    public Work sponsors(Set<Institution> institutions) {
        this.setSponsors(institutions);
        return this;
    }

    public Work addSponsor(Institution institution) {
        this.sponsors.add(institution);
        institution.getProducts().add(this);
        return this;
    }

    public Work removeSponsor(Institution institution) {
        this.sponsors.remove(institution);
        institution.getProducts().remove(this);
        return this;
    }

    public Set<Role> getWorkRoleNames() {
        return this.workRoleNames;
    }

    public void setWorkRoleNames(Set<Role> roles) {
        this.workRoleNames = roles;
    }

    public Work workRoleNames(Set<Role> roles) {
        this.setWorkRoleNames(roles);
        return this;
    }

    public Work addWorkRoleName(Role role) {
        this.workRoleNames.add(role);
        role.getWorkNames().add(this);
        return this;
    }

    public Work removeWorkRoleName(Role role) {
        this.workRoleNames.remove(role);
        role.getWorkNames().remove(this);
        return this;
    }

    public Set<WorkGroup> getCollections() {
        return this.collections;
    }

    public void setCollections(Set<WorkGroup> workGroups) {
        if (this.collections != null) {
            this.collections.forEach(i -> i.setWork(null));
        }
        if (workGroups != null) {
            workGroups.forEach(i -> i.setWork(this));
        }
        this.collections = workGroups;
    }

    public Work collections(Set<WorkGroup> workGroups) {
        this.setCollections(workGroups);
        return this;
    }

    public Work addCollection(WorkGroup workGroup) {
        this.collections.add(workGroup);
        workGroup.setWork(this);
        return this;
    }

    public Work removeCollection(WorkGroup workGroup) {
        this.collections.remove(workGroup);
        workGroup.setWork(null);
        return this;
    }

    public Set<Person> getResponsibles() {
        return this.responsibles;
    }

    public void setResponsibles(Set<Person> people) {
        if (this.responsibles != null) {
            this.responsibles.forEach(i -> i.removeWork(this));
        }
        if (people != null) {
            people.forEach(i -> i.addWork(this));
        }
        this.responsibles = people;
    }

    public Work responsibles(Set<Person> people) {
        this.setResponsibles(people);
        return this;
    }

    public Work addResponsible(Person person) {
        this.responsibles.add(person);
        person.getWorks().add(this);
        return this;
    }

    public Work removeResponsible(Person person) {
        this.responsibles.remove(person);
        person.getWorks().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Work)) {
            return false;
        }
        return id != null && id.equals(((Work) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Work{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", published='" + getPublished() + "'" +
            "}";
    }
}
