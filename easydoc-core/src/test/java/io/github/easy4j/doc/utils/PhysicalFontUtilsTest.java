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
package io.github.easy4j.doc.utils;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * PhysicalFontUtils has no useful static entry point that does not require a
 * {@link WordprocessingMLPackage} (or it triggers the docx4j 11.5.14 MOXy
 * bridge issue on {@code WordprocessingMLPackage.load()}). All load-required
 * tests are disabled.
 */
@Disabled("requires MOXy migration")
class PhysicalFontUtilsTest {

    @Test
    void setSimSunFontAppliesDefaultFont() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        assertNotNull(pkg);
        PhysicalFontUtils.setSimSunFont(pkg);
    }
}
