/*
 * Copyright (c) 2024, hiwepy (https://github.com/easy-4-java).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package io.github.easy4j.doc.xhtml.handler;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link DocumentHandler}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
class DocumentHandler_Test {

    @Test
    void test_interfaceExists() {
        assertThat(DocumentHandler.class).isInterface();
    }
}
