package it.unimi.dllcm.migate.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link it.unimi.dllcm.migate.domain.Work} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WorkDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private LocalDate published;

    private Set<InstitutionDTO> sponsors = new HashSet<>();

    private Set<RoleDTO> workRoleNames = new HashSet<>();

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

    public LocalDate getPublished() {
        return published;
    }

    public void setPublished(LocalDate published) {
        this.published = published;
    }

    public Set<InstitutionDTO> getSponsors() {
        return sponsors;
    }

    public void setSponsors(Set<InstitutionDTO> sponsors) {
        this.sponsors = sponsors;
    }

    public Set<RoleDTO> getWorkRoleNames() {
        return workRoleNames;
    }

    public void setWorkRoleNames(Set<RoleDTO> workRoleNames) {
        this.workRoleNames = workRoleNames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkDTO)) {
            return false;
        }

        WorkDTO workDTO = (WorkDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, workDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WorkDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", published='" + getPublished() + "'" +
            ", sponsors=" + getSponsors() +
            ", workRoleNames=" + getWorkRoleNames() +
            "}";
    }
}
