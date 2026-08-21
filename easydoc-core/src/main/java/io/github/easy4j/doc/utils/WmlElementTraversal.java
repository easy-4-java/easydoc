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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.JAXBElement;

import org.docx4j.TextUtils;
import org.docx4j.XmlUtils;
import org.docx4j.wml.ContentAccessor;

/**
 * Generic XML/element traversal helpers for docx4j content tree.
 * <p>
 * Extracted from {@code WmlElementUtils} (chunk-6 refactor). Independent of
 * table / paragraph / run / section / document concerns.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public final class WmlElementTraversal {

    private WmlElementTraversal() {
    }

    public static <T> List<T> getChildrenElements(Object source, Class<T> targetClass) {
        List<T> result = new ArrayList<T>();
        //获取真实的对象
        Object target = XmlUtils.unwrap(source);
        //if (target.getClass().equals(targetClass)) {
        if (targetClass.isAssignableFrom(target.getClass())) {
            result.add((T)target);
        } else if (target instanceof ContentAccessor) {
            List<?> children = ((ContentAccessor) target).getContent();
            //if (children.getClass().equals(targetClass)) {
            if (targetClass.isAssignableFrom(children.getClass())) {
                result.add((T)children);
            }
        }
        return result;
    }

    /*
     * 这样会返回一个表示完整的空白（在此时）文档Java对象。现在我们可以使用Docx4J API添加、删除以及更新这个word文档的内容，Docx4J有一些你可以用于遍历该文档的工具类。
     * 我自己写了几个助手方法使查找指定占位符并用真实内容进行替换的操作变地很简单。让我们来看一下其中的一个，这个计算是几个JAXB计算的包装器，
     * 允许你针对一个特定的类来搜索指定元素以及它所有的孩子，例如，你可以用它获取文档中所有的表格、表格中所有的行以及其它类似的操作。
     */
    public static <T> List<T> getTargetElements(Object source, Class<T> targetClass) {
        List<T> result = new ArrayList<T>();
        //获取真实的对象
        Object target = XmlUtils.unwrap(source);
        //if (target.getClass().equals(targetClass)) {
        if (targetClass.isAssignableFrom(target.getClass())) {
            result.add((T) target);
        } else if (target instanceof ContentAccessor) {
            List<?> children = ((ContentAccessor) target).getContent();
            for (Object child : children) {
                result.addAll(getTargetElements(child, targetClass));
            }
        }
        return result;
    }

    /**
     * @Description:得到指定类型的元素
     */
    public static List<Object> getAllElementFromObject(Object obj, Class<?> toSearch) {
        List<Object> result = new ArrayList<Object>();
        if (obj instanceof JAXBElement)
            obj = ((JAXBElement<?>) obj).getValue();
        if (obj.getClass().equals(toSearch))
            result.add(obj);
        else if (obj instanceof ContentAccessor) {
            List<?> children = ((ContentAccessor) obj).getContent();
            for (Object child : children) {
                result.addAll(getAllElementFromObject(child, toSearch));
            }
        }
        return result;
    }

    public static String getElementContent(Object obj) throws Exception {
        StringWriter stringWriter = new StringWriter();
        TextUtils.extractText(obj, stringWriter);
        return stringWriter.toString();
    }
}
