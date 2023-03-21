package it.unimi.dllcm.migate.domain;

import static org.assertj.core.api.Assertions.assertThat;

import it.unimi.dllcm.migate.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WorkGroupTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WorkGroup.class);
        WorkGroup workGroup1 = new WorkGroup();
        workGroup1.setId(1L);
        WorkGroup workGroup2 = new WorkGroup();
        workGroup2.setId(workGroup1.getId());
        assertThat(workGroup1).isEqualTo(workGroup2);
        workGroup2.setId(2L);
        assertThat(workGroup1).isNotEqualTo(workGroup2);
        workGroup1.setId(null);
        assertThat(workGroup1).isNotEqualTo(workGroup2);
    }
}
