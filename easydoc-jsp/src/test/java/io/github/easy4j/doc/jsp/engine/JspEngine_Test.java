/*
 * Copyright (c) 2024, hiwepy (https://github.com/easy-4-java).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package io.github.easy4j.doc.jsp.engine;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link JspEngine}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
class JspEngine_Test {

    @Test
    void test_classIsAbstract() {
        assertThat(java.lang.reflect.Modifier.isAbstract(JspEngine.class.getModifiers())).isTrue();
    }
}
