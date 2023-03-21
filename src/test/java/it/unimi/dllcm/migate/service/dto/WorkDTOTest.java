package it.unimi.dllcm.migate.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import it.unimi.dllcm.migate.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WorkDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(WorkDTO.class);
        WorkDTO workDTO1 = new WorkDTO();
        workDTO1.setId(1L);
        WorkDTO workDTO2 = new WorkDTO();
        assertThat(workDTO1).isNotEqualTo(workDTO2);
        workDTO2.setId(workDTO1.getId());
        assertThat(workDTO1).isEqualTo(workDTO2);
        workDTO2.setId(2L);
        assertThat(workDTO1).isNotEqualTo(workDTO2);
        workDTO1.setId(null);
        assertThat(workDTO1).isNotEqualTo(workDTO2);
    }
}
