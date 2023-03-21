package it.unimi.dllcm.migate.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.unimi.dllcm.migate.domain.enumeration.Country;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Institution.
 */
@Entity
@Table(name = "institution")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "institution")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Institution implements Serializable {

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
    @Column(name = "country")
    private Country country;

    @ManyToMany
    @JoinTable(
        name = "rel_institution__institution_role_name",
        joinColumns = @JoinColumn(name = "institution_id"),
        inverseJoinColumns = @JoinColumn(name = "institution_role_name_id")
    )
    @JsonIgnoreProperties(value = { "institutionNames", "people", "workNames" }, allowSetters = true)
    private Set<Role> institutionRoleNames = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "owners" }, allowSetters = true)
    private Location site;

    @ManyToMany(mappedBy = "sponsors")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "sponsors", "workRoleNames", "collections", "responsibles" }, allowSetters = true)
    private Set<Work> products = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Institution id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Institution name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Country getCountry() {
        return this.country;
    }

    public Institution country(Country country) {
        this.setCountry(country);
        return this;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Set<Role> getInstitutionRoleNames() {
        return this.institutionRoleNames;
    }

    public void setInstitutionRoleNames(Set<Role> roles) {
        this.institutionRoleNames = roles;
    }

    public Institution institutionRoleNames(Set<Role> roles) {
        this.setInstitutionRoleNames(roles);
        return this;
    }

    public Institution addInstitutionRoleName(Role role) {
        this.institutionRoleNames.add(role);
        role.getInstitutionNames().add(this);
        return this;
    }

    public Institution removeInstitutionRoleName(Role role) {
        this.institutionRoleNames.remove(role);
        role.getInstitutionNames().remove(this);
        return this;
    }

    public Location getSite() {
        return this.site;
    }

    public void setSite(Location location) {
        this.site = location;
    }

    public Institution site(Location location) {
        this.setSite(location);
        return this;
    }

    public Set<Work> getProducts() {
        return this.products;
    }

    public void setProducts(Set<Work> works) {
        if (this.products != null) {
            this.products.forEach(i -> i.removeSponsor(this));
        }
        if (works != null) {
            works.forEach(i -> i.addSponsor(this));
        }
        this.products = works;
    }

    public Institution products(Set<Work> works) {
        this.setProducts(works);
        return this;
    }

    public Institution addProduct(Work work) {
        this.products.add(work);
        work.getSponsors().add(this);
        return this;
    }

    public Institution removeProduct(Work work) {
        this.products.remove(work);
        work.getSponsors().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Institution)) {
            return false;
        }
        return id != null && id.equals(((Institution) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Institution{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", country='" + getCountry() + "'" +
            "}";
    }
}
