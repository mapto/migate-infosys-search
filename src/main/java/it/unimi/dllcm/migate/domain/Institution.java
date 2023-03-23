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

    @OneToMany(mappedBy = "owner")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "owner" }, allowSetters = true)
    private Set<Location> sites = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "sponsors", "people", "products" }, allowSetters = true)
    private Role position;

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

    public Set<Location> getSites() {
        return this.sites;
    }

    public void setSites(Set<Location> locations) {
        if (this.sites != null) {
            this.sites.forEach(i -> i.setOwner(null));
        }
        if (locations != null) {
            locations.forEach(i -> i.setOwner(this));
        }
        this.sites = locations;
    }

    public Institution sites(Set<Location> locations) {
        this.setSites(locations);
        return this;
    }

    public Institution addSite(Location location) {
        this.sites.add(location);
        location.setOwner(this);
        return this;
    }

    public Institution removeSite(Location location) {
        this.sites.remove(location);
        location.setOwner(null);
        return this;
    }

    public Role getPosition() {
        return this.position;
    }

    public void setPosition(Role role) {
        this.position = role;
    }

    public Institution position(Role role) {
        this.setPosition(role);
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
