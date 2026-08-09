/*
 * Copyright (c) 2024, hiwepy (https://github.com/easy-4-java).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package io.github.easy4j.doc.wml;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link WMLType}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
class WMLType_Test {

    @Test
    void test_values() {
        WMLType[] values = WMLType.values();
        assertThat(values).isNotEmpty();
    }

    @Test
    void test_valueOf() {
        String name = WMLType.values()[0].name();
        assertThat(WMLType.valueOf(name)).isNotNull();
    }
}
