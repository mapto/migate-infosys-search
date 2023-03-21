package it.unimi.dllcm.migate.service.dto;

import it.unimi.dllcm.migate.domain.enumeration.Country;
import it.unimi.dllcm.migate.domain.enumeration.Gender;
import it.unimi.dllcm.migate.domain.enumeration.Language;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link it.unimi.dllcm.migate.domain.Person} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PersonDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private Gender gender;

    private LocalDate dob;

    private LocalDate dod;

    private Country country;

    private Language language;

    private Set<WorkDTO> works = new HashSet<>();

    private Set<RoleDTO> responsibilities = new HashSet<>();

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

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public LocalDate getDod() {
        return dod;
    }

    public void setDod(LocalDate dod) {
        this.dod = dod;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Set<WorkDTO> getWorks() {
        return works;
    }

    public void setWorks(Set<WorkDTO> works) {
        this.works = works;
    }

    public Set<RoleDTO> getResponsibilities() {
        return responsibilities;
    }

    public void setResponsibilities(Set<RoleDTO> responsibilities) {
        this.responsibilities = responsibilities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PersonDTO)) {
            return false;
        }

        PersonDTO personDTO = (PersonDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, personDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PersonDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", gender='" + getGender() + "'" +
            ", dob='" + getDob() + "'" +
            ", dod='" + getDod() + "'" +
            ", country='" + getCountry() + "'" +
            ", language='" + getLanguage() + "'" +
            ", works=" + getWorks() +
            ", responsibilities=" + getResponsibilities() +
            "}";
    }
}
