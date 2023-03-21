package it.unimi.dllcm.migate.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link it.unimi.dllcm.migate.domain.WorkGroup} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WorkGroupDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private WorkDTO work;

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

    public WorkDTO getWork() {
        return work;
    }

    public void setWork(WorkDTO work) {
        this.work = work;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkGroupDTO)) {
            return false;
        }

        WorkGroupDTO workGroupDTO = (WorkGroupDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, workGroupDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WorkGroupDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", work=" + getWork() +
            "}";
    }
}
