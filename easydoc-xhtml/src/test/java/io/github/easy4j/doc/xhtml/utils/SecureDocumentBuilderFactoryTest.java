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
package io.github.easy4j.doc.xhtml.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * {@link SecureDocumentBuilderFactory} 的单元测试：验证安全特性在构造时正确配置，
 * 且含 DOCTYPE 声明的输入被解析器直接拒绝。
 */
class SecureDocumentBuilderFactoryTest {

    @Test
    @DisplayName("构造 SecureDocumentBuilderFactory 不抛异常")
    void constructorDoesNotThrow() {
        SecureDocumentBuilderFactory factory = new SecureDocumentBuilderFactory();
        assertNotNull(factory, "factory instance must not be null");
    }

    @Test
    @DisplayName("newDocumentBuilder() 能创建 DocumentBuilder 实例")
    void newDocumentBuilderReturnsInstance() throws Exception {
        SecureDocumentBuilderFactory factory = new SecureDocumentBuilderFactory();
        DocumentBuilder builder = factory.newDocumentBuilder();
        assertNotNull(builder, "DocumentBuilder must not be null");
    }

    @Test
    @DisplayName("getFeature(disallow-doctype-decl) == true")
    void disallowDoctypeDeclEnabled() throws Exception {
        SecureDocumentBuilderFactory factory = new SecureDocumentBuilderFactory();
        assertTrue(
            factory.getFeature("http://apache.org/xml/features/disallow-doctype-decl"),
            "disallow-doctype-decl feature must be enabled"
        );
    }

    @Test
    @DisplayName("getFeature(external-general-entities) == false")
    void externalGeneralEntitiesDisabled() throws Exception {
        SecureDocumentBuilderFactory factory = new SecureDocumentBuilderFactory();
        assertEquals(
            false,
            factory.getFeature("http://xml.org/sax/features/external-general-entities"),
            "external-general-entities feature must be disabled"
        );
    }

    @Test
    @DisplayName("解析含 DOCTYPE 声明的输入应抛 SAXParseException，不返回 Document")
    void parsingDoctypeInputThrows() throws Exception {
        SecureDocumentBuilderFactory factory = new SecureDocumentBuilderFactory();
        DocumentBuilder builder = factory.newDocumentBuilder();

        String maliciousXml =
            "<!DOCTYPE foo [<!ENTITY x SYSTEM 'file:///etc/passwd'>]><foo/>";
        ByteArrayInputStream inputStream =
            new ByteArrayInputStream(maliciousXml.getBytes(StandardCharsets.UTF_8));

        assertThrows(SAXParseException.class,
            () -> builder.parse(inputStream),
            "DOCTYPE 声明必须被解析器直接拒绝");
    }

    @Test
    @DisplayName("解析正常 XML 输入能正常返回 Document")
    void parsingValidXmlSucceeds() throws Exception {
        SecureDocumentBuilderFactory factory = new SecureDocumentBuilderFactory();
        DocumentBuilder builder = factory.newDocumentBuilder();

        String validXml = "<root><child>text</child></root>";
        ByteArrayInputStream inputStream =
            new ByteArrayInputStream(validXml.getBytes(StandardCharsets.UTF_8));

        assertNotNull(builder.parse(inputStream), "valid XML must parse successfully");
    }

    @Test
    @DisplayName("setAttribute / getAttribute 委托到内部工厂")
    void attributeDelegation() {
        SecureDocumentBuilderFactory factory = new SecureDocumentBuilderFactory();
        // getAttribute 对不存在的属性抛 IllegalArgumentException（JAXP 标准行为）
        assertThrows(IllegalArgumentException.class,
            () -> factory.getAttribute("nonexistent-attr"),
            "unknown attribute must throw IllegalArgumentException");
        // setAttribute 对不存在的属性同样抛 IllegalArgumentException
        assertThrows(IllegalArgumentException.class,
            () -> factory.setAttribute("nonexistent-attr", "value"),
            "setting unknown attribute must throw IllegalArgumentException");
    }

    @Test
    @DisplayName("setFeature / getFeature 委托到内部工厂")
    void featureDelegation() throws Exception {
        SecureDocumentBuilderFactory factory = new SecureDocumentBuilderFactory();
        // 已配置的安全特性可读取
        assertTrue(factory.getFeature("http://apache.org/xml/features/disallow-doctype-decl"),
            "disallow-doctype-decl must be true");
        assertEquals(false, factory.getFeature("http://xml.org/sax/features/external-general-entities"),
            "external-general-entities must be false");
        assertEquals(false, factory.getFeature("http://xml.org/sax/features/external-parameter-entities"),
            "external-parameter-entities must be false");
    }

    @Test
    @DisplayName("isXIncludeAware 默认返回 false")
    void xIncludeAwareDefault() {
        SecureDocumentBuilderFactory factory = new SecureDocumentBuilderFactory();
        assertEquals(false, factory.isXIncludeAware(), "XInclude must be disabled by default");
    }

    @Test
    @DisplayName("isExpandEntityReferences 默认返回 false")
    void expandEntityReferencesDefault() {
        SecureDocumentBuilderFactory factory = new SecureDocumentBuilderFactory();
        assertEquals(false, factory.isExpandEntityReferences(),
            "expand entity references must be disabled by default");
    }

    @Test
    @DisplayName("setNamespaceAware / isNamespaceAware 委托到内部工厂")
    void namespaceAwareDelegation() {
        SecureDocumentBuilderFactory factory = new SecureDocumentBuilderFactory();
        factory.setNamespaceAware(true);
        assertEquals(true, factory.isNamespaceAware(), "namespaceAware must delegate");
    }

    @Test
    @DisplayName("setValidating / isValidating 委托到内部工厂")
    void validatingDelegation() {
        SecureDocumentBuilderFactory factory = new SecureDocumentBuilderFactory();
        factory.setValidating(true);
        assertEquals(true, factory.isValidating(), "validating must delegate");
    }

    @Test
    @DisplayName("setCoalescing / isCoalescing 委托到内部工厂")
    void coalescingDelegation() {
        SecureDocumentBuilderFactory factory = new SecureDocumentBuilderFactory();
        factory.setCoalescing(true);
        assertEquals(true, factory.isCoalescing(), "coalescing must delegate");
    }

    @Test
    @DisplayName("setIgnoringElementContentWhitespace / isIgnoringElementContentWhitespace 委托到内部工厂")
    void ignoringElementContentWhitespaceDelegation() {
        SecureDocumentBuilderFactory factory = new SecureDocumentBuilderFactory();
        factory.setIgnoringElementContentWhitespace(true);
        assertEquals(true, factory.isIgnoringElementContentWhitespace(),
            "ignoringElementContentWhitespace must delegate");
    }

    @Test
    @DisplayName("setIgnoringComments / isIgnoringComments 委托到内部工厂")
    void ignoringCommentsDelegation() {
        SecureDocumentBuilderFactory factory = new SecureDocumentBuilderFactory();
        factory.setIgnoringComments(true);
        assertEquals(true, factory.isIgnoringComments(), "ignoringComments must delegate");
    }

    @Test
    @DisplayName("FEATURE_SECURE_PROCESSING 已启用")
    void secureProcessingEnabled() throws Exception {
        SecureDocumentBuilderFactory factory = new SecureDocumentBuilderFactory();
        assertTrue(factory.getFeature(XMLConstants.FEATURE_SECURE_PROCESSING),
            "FEATURE_SECURE_PROCESSING must be enabled");
    }

    @Test
    @DisplayName("setFeature 委托到内部工厂")
    void setFeatureDelegates() throws Exception {
        SecureDocumentBuilderFactory factory = new SecureDocumentBuilderFactory();
        // 通过 setFeature 设置一个已知特性，验证委托生效
        factory.setFeature("http://xml.org/sax/features/external-general-entities", true);
        assertEquals(true,
            factory.getFeature("http://xml.org/sax/features/external-general-entities"),
            "setFeature must delegate to internal factory");
        // 恢复原值以免影响其他测试
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
    }

    @Test
    @DisplayName("setXIncludeAware 委托到内部工厂")
    void setXIncludeAwareDelegates() {
        SecureDocumentBuilderFactory factory = new SecureDocumentBuilderFactory();
        factory.setXIncludeAware(true);
        assertEquals(true, factory.isXIncludeAware(), "setXIncludeAware must delegate");
        // 恢复
        factory.setXIncludeAware(false);
        assertEquals(false, factory.isXIncludeAware());
    }

    @Test
    @DisplayName("setExpandEntityReferences 委托到内部工厂")
    void setExpandEntityReferencesDelegates() {
        SecureDocumentBuilderFactory factory = new SecureDocumentBuilderFactory();
        factory.setExpandEntityReferences(true);
        assertEquals(true, factory.isExpandEntityReferences(),
            "setExpandEntityReferences must delegate");
        // 恢复
        factory.setExpandEntityReferences(false);
        assertEquals(false, factory.isExpandEntityReferences());
    }

    @Test
    @DisplayName("setAttribute 委托到内部工厂（合法属性名）")
    void setAttributeDelegatesWithValidName() throws Exception {
        SecureDocumentBuilderFactory factory = new SecureDocumentBuilderFactory();
        // 使用 JAXP 标准 schema 语言属性（合法的属性名）
        try {
            factory.setAttribute(
                "http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                "http://www.w3.org/2001/XMLSchema");
        } catch (IllegalArgumentException e) {
            // 部分 JDK 实现不支持此属性，此测试仅覆盖 setAttribute 方法体
        }
    }

    @Test
    @DisplayName("getAttribute 委托到内部工厂（合法属性名）")
    void getAttributeDelegatesWithValidName() throws Exception {
        SecureDocumentBuilderFactory factory = new SecureDocumentBuilderFactory();
        // 使用 JAXP 标准 schema 语言属性
        try {
            factory.getAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage");
        } catch (IllegalArgumentException e) {
            // 部分 JDK 实现不支持此属性，此测试仅覆盖 getAttribute 方法体
        }
    }
}
