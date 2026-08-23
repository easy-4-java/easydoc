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
package io.github.easy4j.doc;

import java.io.File;  
import java.io.StringWriter;  
import java.util.ArrayList;  
import java.util.List;  
  
import jakarta.xml.bind.JAXBElement;  
  
import org.docx4j.TextUtils;  
import org.docx4j.TraversalUtil;  
import org.docx4j.TraversalUtil.CallbackImpl;  
import org.docx4j.XmlUtils;  
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;  
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;  
import org.docx4j.wml.Body;  
import org.docx4j.wml.CTLock;  
import org.docx4j.wml.CTSdtCell;  
import org.docx4j.wml.SdtContent;  
import org.docx4j.wml.Document;  
import org.docx4j.wml.Id;  
import org.docx4j.wml.RPr;  
import org.docx4j.wml.RStyle;  
import org.docx4j.wml.SdtBlock;  
import org.docx4j.wml.SdtPr;  
import org.docx4j.wml.SdtPr.Alias;  
import org.docx4j.wml.SdtRun;  
import org.docx4j.wml.Tag;  
import org.jvnet.jaxb.lang.Child;

//能区分纯文本和格式文本(格式文本能插入公式,纯文本不能)  
public class Docx4j_读取内容控件_S4Test {  
    public static void main(String[] args) throws Exception {  
        Docx4j_读取内容控件_S4Test t = new Docx4j_读取内容控件_S4Test();  
        t.printSdtContent("f:/saveFile/temp/kkk3.docx");  
    }  
  
    public void printSdtContent(String filePath) throws Exception {  
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new File(filePath));  
        MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();  
        Document wmlDocumentEl = (Document) mdp.getContents();  
        Body body = wmlDocumentEl.getBody();  
        SdtFinder_2 sdtFinder = new SdtFinder_2();  
        new TraversalUtil(body, sdtFinder);  
        for (Child sdtChild : sdtFinder.sdtList) {  
            if (sdtChild instanceof SdtRun) {  
                SdtRun sdtRun = (SdtRun) sdtChild;  
                SdtPr sdtPr = sdtRun.getSdtPr();  
                printSdtPrContent(sdtPr);  
                SdtContent sdtContent = sdtRun.getSdtContent();  
                System.out.println("-----------p content="+ getSdtContentContent(sdtContent));  
            } else if (sdtChild instanceof CTSdtCell) {  
                CTSdtCell sdtCell = (CTSdtCell) sdtChild;  
                SdtPr sdtPr = sdtCell.getSdtPr();  
                printSdtPrContent(sdtPr);  
                SdtContent sdtContent = sdtCell.getSdtContent();  
                System.out.println("-----------table content="+ getSdtContentContent(sdtContent));  
            } else if (sdtChild instanceof SdtBlock) {  
                SdtBlock sdtBlock = (SdtBlock) sdtChild;  
                SdtPr sdtPr = sdtBlock.getSdtPr();  
                printSdtPrContent(sdtPr);  
                SdtContent sdtContent = sdtBlock.getSdtContent();  
                System.out.println("-----------sdtblock content="+ getSdtContentContent(sdtContent));  
            }  
        }  
    }  
  
    // 解析样式,区分纯文本和格式文本  
    public void printSdtPrContent(SdtPr sdtPr) {  
        StringBuffer sb = new StringBuffer();  
        List<Object> rprList = sdtPr.getRPrOrAliasOrLock();  
        boolean flag=false;  
        for (Object obj : rprList) {  
            if (obj instanceof JAXBElement) {  
                String eName = ((JAXBElement) obj).getName().getLocalPart();  
                // System.out.println("---------=" + eName);  
                // 布尔类型特殊处理  
                if ("temporary".equals(eName)) {  
                    sb.append(" 替换后是否删除内容控件:").append("是");  
                } else if ("text".equals(eName)) {  
                    // 纯文本是否允许回车特殊处理  
                    // CTSdtText判断是否回车代码不准确  
                    // if (this.multiLine == null) {  
                    // return true;  
                    // }  
                    flag=true;  
                    String textXml = XmlUtils.marshaltoString(obj, true, true);  
                    if (textXml.indexOf("w:multiLine") != -1) {  
                        sb.append(" 是否允许回车:").append("是");  
                    }  
                }  
                obj = XmlUtils.unwrap(obj);  
                if (obj instanceof Alias) {  
                    Alias alias = (Alias) obj;  
                    if (alias != null) {  
                        sb.append(" 标题:").append(alias.getVal());  
                    }  
                } else if (obj instanceof CTLock) {  
                    CTLock lock = (CTLock) obj;  
                    if (lock != null) {  
                        if (lock.getVal().value().toUpperCase().equals("CONTENTLOCKED")) {  
                            sb.append(" 锁定方式:").append("无法编辑内容");  
                        } else if (lock.getVal().value().toUpperCase().equals("SDTLOCKED")) {  
                            sb.append(" 锁定方式:").append("无法删除内容控件");  
                        }else if (lock.getVal().value().toUpperCase().equals("SDTCONTENTLOCKED")) {  
                            sb.append(" 锁定方式:").append("无法删除内容控件，无法编辑内容");  
                        } else {  
                            sb.append(" 锁定方式:").append(lock.getVal());  
                        }  
                    }  
                }else if(obj instanceof RPr){  
                    RPr rpr = (RPr) obj;  
                    if(rpr!=null){  
                        RStyle rprStyle = rpr.getRStyle();  
                        if(rprStyle!=null){  
                            sb.append(" 样式名称:").append(rprStyle.getVal());  
                        }  
                    }  
                }  
            } else if (obj instanceof Tag) {  
                Tag tag = (Tag) obj;  
                if (tag != null) {  
                    sb.append(" tag标记:").append(tag.getVal());  
                }  
            } else if (obj instanceof Id) {  
                Id id = (Id) obj;  
                if (id != null) {  
                    sb.append(" id:").append(id.getVal());  
                }  
            }  
        }  
        if(flag){  
            sb.append(" 内容控件类型:").append("纯文本");  
        }else{  
            sb.append(" 内容控件类型:").append("格式文本");  
        }  
        System.out.println(sb.toString());  
    }  
  
    public String getSdtContentContent(SdtContent contentAcc)  
            throws Exception {  
        StringWriter stringWriter = new StringWriter();  
        TextUtils.extractText(contentAcc, stringWriter);  
        return stringWriter.toString();  
    }  
}  
  
class SdtFinder_2 extends CallbackImpl {  
    List<Child> sdtList = new ArrayList<Child>();  
  
    public List<Object> apply(Object o) {  
        if (o instanceof jakarta.xml.bind.JAXBElement  
                && (((JAXBElement) o).getName().getLocalPart().equals("sdt"))) {  
            sdtList.add((Child) XmlUtils.unwrap(o));  
        } else if (o instanceof SdtBlock) {  
            sdtList.add((Child) o);  
        }  
        return null;  
    }  
  
    // to setParent  
    public void walkJAXBElements(Object parent) {  
        List children = getChildren(parent);  
        if (children != null) {  
            for (Object o : children) {  
                if (o instanceof jakarta.xml.bind.JAXBElement  
                        && (((JAXBElement) o).getName().getLocalPart()  
                                .equals("sdt"))) {  
                    ((Child) ((JAXBElement) o).getValue()).setParent(XmlUtils  
                            .unwrap(parent));  
                } else {  
                    o = XmlUtils.unwrap(o);  
                    if (o instanceof Child) {  
                        ((Child) o).setParent(XmlUtils.unwrap(parent));  
                    }  
                }  
                this.apply(o);  
                if (this.shouldTraverse(o)) {  
                    walkJAXBElements(o);  
                }  
            }  
        }  
    }  
}  
