/*
 * Copyright (c) 2018, hiwepy (https://github.com/easy-4-java).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.easy4j.doc.freemarker;

import java.io.IOException;
import java.util.Properties;

import freemarker.template.Configuration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Verifies the engine-initialization alignment contract of
 * {@link WordprocessingMLFreemarkerTemplate}: {@code getInternalEngine()} follows the
 * same shape as the other engine modules (the factory caches the engine and creates
 * the shared Renderer exactly once inside its double-checked lock), the Renderer
 * instance never churns across calls, and the deprecated setters invalidate ALL
 * cached state (factory + engine + renderer) instead of leaving stale instances.
 */
class WordprocessingMLFreemarkerAlignmentTest {

    /**
     * Repeated {@code getInternalEngine()} calls must return the SAME Configuration,
     * and the resolved Renderer must be one stable instance (no churn).
     */
    @Test
    void repeatedInternalEngineCallsReturnSameEngineAndRenderer() throws IOException {
        WordprocessingMLFreemarkerTemplate t = new WordprocessingMLFreemarkerTemplate();

        for (int i = 0; i < 3; i++) {
            Configuration engine = t.getInternalEngine();
            Renderer renderer = t.resolveRenderer();
            assertNotNull(engine);
            assertNotNull(renderer);
            if (i == 0) {
                continue;
            }
            // 覆盖首次调用之后的所有重复调用：引擎与渲染器都不得被重建
            assertSame(engine, t.getInternalEngine(), "Configuration must stay cached");
            assertSame(renderer, t.resolveRenderer(), "Renderer must not churn");
        }
    }

    /**
     * A deprecated setter must invalidate the cached Configuration AND the cached
     * Renderer: subsequent calls rebuild them under the new configuration.
     */
    @SuppressWarnings("deprecation")
    @Test
    void deprecatedSetterInvalidatesCachedEngineAndRenderer() throws IOException {
        WordprocessingMLFreemarkerTemplate t = new WordprocessingMLFreemarkerTemplate();

        Renderer firstRenderer = t.resolveRenderer();
        Configuration firstEngine = t.getInternalEngine();
        assertNotNull(firstRenderer);

        t.setDefaultEncoding("UTF-8");

        Renderer secondRenderer = t.resolveRenderer();
        Configuration secondEngine = t.getInternalEngine();

        assertNotSame(firstEngine, secondEngine, "stale engine must be rebuilt with the new settings");
        assertNotSame(firstRenderer, secondRenderer, "stale renderer bound to old factory must be rebuilt");
        assertEquals("UTF-8", secondEngine.getDefaultEncoding(), "new settings must be honored");

        // 重置后的新实例同样保持稳定（不抖动）
        assertSame(secondRenderer, t.resolveRenderer());
        assertSame(secondEngine, t.getInternalEngine());
    }

    /**
     * The rendered output of a warm template stays consistent with the factory-managed
     * Renderer: no additional Renderer instances are created per render call.
     */
    @SuppressWarnings("deprecation")
    @Test
    void rendererSurvivesSettingsRoundTrip() throws Exception {
        WordprocessingMLFreemarkerTemplate t = new WordprocessingMLFreemarkerTemplate();
        Properties settings = new Properties();
        t.setFreemarkerSettings(settings);

        Renderer before = t.resolveRenderer();
        Configuration cfg = t.getInternalEngine();
        assertNotNull(cfg);
        assertSame(before, t.resolveRenderer());
    }
}
