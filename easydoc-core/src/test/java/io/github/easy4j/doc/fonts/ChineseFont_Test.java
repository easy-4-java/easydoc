/*
 * Copyright (c) 2024, hiwepy (https://github.com/easy-4-java).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package io.github.easy4j.doc.fonts;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link ChineseFont}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
class ChineseFont_Test {

    @Test
    void test_values() {
        ChineseFont[] values = ChineseFont.values();
        assertThat(values).isNotEmpty();
    }

    @Test
    void test_valueOf() {
        String name = ChineseFont.values()[0].name();
        assertThat(ChineseFont.valueOf(name)).isNotNull();
    }
}
