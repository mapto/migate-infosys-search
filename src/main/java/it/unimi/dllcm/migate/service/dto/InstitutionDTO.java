package it.unimi.dllcm.migate.service.dto;

import it.unimi.dllcm.migate.domain.enumeration.Country;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link it.unimi.dllcm.migate.domain.Institution} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InstitutionDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private Country country;

    private Set<RoleDTO> institutionRoleNames = new HashSet<>();

    private LocationDTO site;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Set<RoleDTO> getInstitutionRoleNames() {
        return institutionRoleNames;
    }

    public void setInstitutionRoleNames(Set<RoleDTO> institutionRoleNames) {
        this.institutionRoleNames = institutionRoleNames;
    }

    public LocationDTO getSite() {
        return site;
    }

    public void setSite(LocationDTO site) {
        this.site = site;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InstitutionDTO)) {
            return false;
        }

        InstitutionDTO institutionDTO = (InstitutionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, institutionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InstitutionDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", country='" + getCountry() + "'" +
            ", institutionRoleNames=" + getInstitutionRoleNames() +
            ", site=" + getSite() +
            "}";
    }
}
