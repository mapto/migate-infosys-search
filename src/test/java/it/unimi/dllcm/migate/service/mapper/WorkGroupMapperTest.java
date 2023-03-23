package it.unimi.dllcm.migate.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WorkGroupMapperTest {

    private WorkGroupMapper workGroupMapper;

    @BeforeEach
    public void setUp() {
        workGroupMapper = new WorkGroupMapperImpl();
    }
}
