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
package io.github.easy4j.doc.thymeleaf;

import java.io.File;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class WordprocessingMLThymeleafTemplateTest extends WordprocessingMLTemplateTest {

	protected WordprocessingMLThymeleafTemplate thymeleafTemplate = null;

	@Before
	public void Before() {

		//准备参数
        variables();

        thymeleafTemplate = new WordprocessingMLThymeleafTemplate();

	}

	@Test
	public void test() throws Exception {

		variables.put("title", "变量替换测试");
		variables.put("content", "测试效果不错");

		WordprocessingMLPackage wordMLPackage = thymeleafTemplate.process("/tpl/thymeleaf.tpl", variables);

		assertNotNull(wordMLPackage);

		File outputDocx = new java.io.File("src/test/resources/output/thymeleafTemplate_output.docx");
		wordMLPackage.save(outputDocx);

	}

	@After
	public void after() {
		thymeleafTemplate = null;
	}

}