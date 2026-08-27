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

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * XXE 防护强化的 DocumentBuilderFactory：构造函数内统一应用 SAX 解析器的安全配置。
 *
 * <p>由 {@link XHTMLImporterUtils} 通过 javax.xml.parsers.DocumentBuilderFactory 系统属性
 * 指向本类，作为 docx4j 内部 DocumentBuilderFactory.newInstance() 的拦截器。</p>
 *
 * <p>防护特性：</p>
 * <ul>
 *   <li>禁用 DOCTYPE 声明（disallow-doctype-decl）</li>
 *   <li>禁用外部通用实体（external-general-entities）</li>
 *   <li>禁用外部参数实体（external-parameter-entities）</li>
 *   <li>启用 XMLConstants.FEATURE_SECURE_PROCESSING</li>
 *   <li>禁用 XInclude</li>
 *   <li>禁用实体引用展开</li>
 * </ul>
 *
 * <p>继承 DocumentBuilderFactory 而非包装，保证 docx4j 调用 {@code newInstance().newDocumentBuilder()}
 * 链路不被打断。</p>
 *
 * @author hiwepy
 */
public class SecureDocumentBuilderFactory extends DocumentBuilderFactory {

    /** 内部委托的真实工厂实例，承载所有安全特性配置。 */
    private final DocumentBuilderFactory delegate;

    /**
     * 创建安全工厂实例，构造时立即应用全部 XXE 防护配置。
     *
     * @throws IllegalStateException 若安全特性配置失败（配置失败即抛错，绝不做静默降级）
     */
    public SecureDocumentBuilderFactory() {
        super();
        // XHTMLImporterUtils.handle() 在调用前已将系统属性指向本类，
        // newInstance() 会读取该属性并递归创建本类 → 无限递归 → OOM。
        // 解法：暂清系统属性，newInstance() 走默认发现机制，然后恢复。
        String saved = System.getProperty("javax.xml.parsers.DocumentBuilderFactory");
        System.clearProperty("javax.xml.parsers.DocumentBuilderFactory");
        try {
            delegate = DocumentBuilderFactory.newInstance();
        } finally {
            if (saved != null) {
                System.setProperty("javax.xml.parsers.DocumentBuilderFactory", saved);
            }
        }
        try {
            // 禁用 DOCTYPE 声明（最高优先级：直接拒绝含 DOCTYPE 的输入）
            delegate.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            // 禁用外部通用实体
            delegate.setFeature("http://xml.org/sax/features/external-general-entities", false);
            // 禁用外部参数实体
            delegate.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            // 启用 JDK 通用安全处理
            delegate.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            // 禁用 XInclude（避免包含外部 XML）
            delegate.setXIncludeAware(false);
            // 禁用实体引用展开（防止内部实体递归炸弹）
            delegate.setExpandEntityReferences(false);
        } catch (ParserConfigurationException e) {
            // 配置失败即抛错——绝不做静默降级（降级等于无防护）
            throw new IllegalStateException(
                "Unable to configure XXE protection for DocumentBuilderFactory: " + e.getMessage(), e);
        }
    }

    @Override
    public DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
        return delegate.newDocumentBuilder();
    }

    @Override
    public void setAttribute(String name, Object value) throws IllegalArgumentException {
        delegate.setAttribute(name, value);
    }

    @Override
    public Object getAttribute(String name) throws IllegalArgumentException {
        return delegate.getAttribute(name);
    }

    @Override
    public void setFeature(String name, boolean value) throws ParserConfigurationException {
        delegate.setFeature(name, value);
    }

    @Override
    public boolean getFeature(String name) throws ParserConfigurationException {
        return delegate.getFeature(name);
    }

    @Override
    public void setXIncludeAware(boolean state) {
        delegate.setXIncludeAware(state);
    }

    @Override
    public boolean isXIncludeAware() {
        return delegate.isXIncludeAware();
    }

    @Override
    public void setExpandEntityReferences(boolean expandEntityRef) {
        delegate.setExpandEntityReferences(expandEntityRef);
    }

    @Override
    public boolean isExpandEntityReferences() {
        return delegate.isExpandEntityReferences();
    }

    @Override
    public void setNamespaceAware(boolean awareness) {
        delegate.setNamespaceAware(awareness);
    }

    @Override
    public boolean isNamespaceAware() {
        return delegate.isNamespaceAware();
    }

    @Override
    public void setValidating(boolean validating) {
        delegate.setValidating(validating);
    }

    @Override
    public boolean isValidating() {
        return delegate.isValidating();
    }

    @Override
    public void setCoalescing(boolean coalescing) {
        delegate.setCoalescing(coalescing);
    }

    @Override
    public boolean isCoalescing() {
        return delegate.isCoalescing();
    }

    @Override
    public void setIgnoringElementContentWhitespace(boolean whitespace) {
        delegate.setIgnoringElementContentWhitespace(whitespace);
    }

    @Override
    public boolean isIgnoringElementContentWhitespace() {
        return delegate.isIgnoringElementContentWhitespace();
    }

    @Override
    public void setIgnoringComments(boolean ignoreComments) {
        delegate.setIgnoringComments(ignoreComments);
    }

    @Override
    public boolean isIgnoringComments() {
        return delegate.isIgnoringComments();
    }
}
