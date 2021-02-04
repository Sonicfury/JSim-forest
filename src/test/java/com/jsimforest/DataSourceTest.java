package com.jsimforest;

import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class DataSourceTest extends AbstractTest {

    @Test
    public void testConnect() {
        assertDoesNotThrow(DataSource::connect);
    }
}