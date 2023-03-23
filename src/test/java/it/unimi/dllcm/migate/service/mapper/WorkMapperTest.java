package it.unimi.dllcm.migate.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WorkMapperTest {

    private WorkMapper workMapper;

    @BeforeEach
    public void setUp() {
        workMapper = new WorkMapperImpl();
    }
}
