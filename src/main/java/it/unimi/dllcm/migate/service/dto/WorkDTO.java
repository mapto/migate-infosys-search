package it.unimi.dllcm.migate.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
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

    private WorkGroupDTO collection;

    private RoleDTO responsibility;

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

    public WorkGroupDTO getCollection() {
        return collection;
    }

    public void setCollection(WorkGroupDTO collection) {
        this.collection = collection;
    }

    public RoleDTO getResponsibility() {
        return responsibility;
    }

    public void setResponsibility(RoleDTO responsibility) {
        this.responsibility = responsibility;
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
            ", collection=" + getCollection() +
            ", responsibility=" + getResponsibility() +
            "}";
    }
}
