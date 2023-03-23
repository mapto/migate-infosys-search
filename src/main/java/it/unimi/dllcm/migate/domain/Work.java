package it.unimi.dllcm.migate.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
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

    @ManyToOne
    @JsonIgnoreProperties(value = { "works" }, allowSetters = true)
    private WorkGroup collection;

    @ManyToOne
    @JsonIgnoreProperties(value = { "sponsors", "people", "products" }, allowSetters = true)
    private Role responsibility;

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

    public WorkGroup getCollection() {
        return this.collection;
    }

    public void setCollection(WorkGroup workGroup) {
        this.collection = workGroup;
    }

    public Work collection(WorkGroup workGroup) {
        this.setCollection(workGroup);
        return this;
    }

    public Role getResponsibility() {
        return this.responsibility;
    }

    public void setResponsibility(Role role) {
        this.responsibility = role;
    }

    public Work responsibility(Role role) {
        this.setResponsibility(role);
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
