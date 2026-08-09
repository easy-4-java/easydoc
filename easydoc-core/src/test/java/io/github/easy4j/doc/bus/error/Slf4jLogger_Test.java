/*
 * Copyright (c) 2024, hiwepy (https://github.com/easy-4-java).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package io.github.easy4j.doc.bus.error;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link Slf4jLogger}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
class Slf4jLogger_Test {

    @Test
    void test_constructor() {
        Slf4jLogger instance = new Slf4jLogger();
        assertThat(instance).isNotNull();
    }
}
