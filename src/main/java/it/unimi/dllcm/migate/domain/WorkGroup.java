package it.unimi.dllcm.migate.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A WorkGroup.
 */
@Entity
@Table(name = "work_group")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "workgroup")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WorkGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JsonIgnoreProperties(value = { "sponsors", "workRoleNames", "collections", "responsibles" }, allowSetters = true)
    private Work work;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public WorkGroup id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public WorkGroup name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Work getWork() {
        return this.work;
    }

    public void setWork(Work work) {
        this.work = work;
    }

    public WorkGroup work(Work work) {
        this.setWork(work);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkGroup)) {
            return false;
        }
        return id != null && id.equals(((WorkGroup) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WorkGroup{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
