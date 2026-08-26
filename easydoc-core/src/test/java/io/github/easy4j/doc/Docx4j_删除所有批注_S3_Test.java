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


import java.io.FileOutputStream;  
import java.util.ArrayList;  
import java.util.HashMap;  
import java.util.List;  
  
import jakarta.xml.bind.JAXBElement;  
  
import org.docx4j.TraversalUtil;  
import org.docx4j.TraversalUtil.CallbackImpl;  
import org.docx4j.XmlUtils;  
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;  
import org.docx4j.openpackaging.parts.Part;  
import org.docx4j.openpackaging.parts.PartName;  
import org.docx4j.openpackaging.parts.Parts;  
import org.docx4j.openpackaging.parts.WordprocessingML.CommentsPart;  
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;  
import org.docx4j.wml.Body;  
import org.docx4j.wml.CommentRangeEnd;  
import org.docx4j.wml.CommentRangeStart;  
import org.docx4j.wml.Comments;  
import org.docx4j.wml.Comments.Comment;  
import org.docx4j.wml.ContentAccessor;  
import org.docx4j.wml.R.CommentReference;  
import org.jvnet.jaxb2_commons.ppp.Child; 
/**
 * http://53873039oycg.iteye.com/blog/2193312
 */
//原文见:http://stackoverflow.com/questions/14738446/how-to-remove-all-comments-from-docx-file-with-docx4j  
public class Docx4j_删除所有批注_S3_Test {  
    public static void main(String[] args) throws Exception {  
        Docx4j_删除所有批注_S3_Test t = new Docx4j_删除所有批注_S3_Test();  
        t.removeAllComment("f:/saveFile/temp/sys_07_comments - 副本.docx",  
                "f:/saveFile/temp/sys_07_comments - 副本.docx");  
    }  
  
    //这里2个路径可以一致  
    public void removeAllComment(String filePath, String savePath)  
            throws Exception {  
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage  
                .load(new java.io.File(filePath));  
        //清空comments.xml内容   
        Parts parts = wordMLPackage.getParts();  
        HashMap<PartName, Part> partMap = parts.getParts();  
        CommentsPart commentPart = (CommentsPart) partMap.get(new PartName(  
                "/word/comments.xml"));  
        Comments comments = commentPart.getContents();  
        List<Comment> commentList = comments.getComment();  
        for (int i = 0, len = commentList.size(); i < len; i++) {  
            commentList.remove(0);  
        }  
          
        //清空document.xml文件中批注  
        MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();  
        org.docx4j.wml.Document wmlDocumentEl = (org.docx4j.wml.Document) documentPart  
                .getContents();  
        Body body = wmlDocumentEl.getBody();  
        CommentFinder cf = new CommentFinder();  
        new TraversalUtil(body, cf);  
        for (Child commentElement : cf.commentElements) {  
            System.out.println(commentElement.getClass().getName());  
            Object parent = commentElement.getParent();  
            List<Object> theList = ((ContentAccessor) parent).getContent();  
            boolean removeResult = remove(theList, commentElement);  
            System.out.println(removeResult);  
        }  
        wordMLPackage.save(new FileOutputStream(savePath));  
    }  
  
    public  boolean remove(List<Object> theList, Object bm) {  
        // Can't just remove the object from the parent,  
        // since in the parent, it may be wrapped in a JAXBElement  
        for (Object ox : theList) {  
            if (XmlUtils.unwrap(ox).equals(bm)) {  
                return theList.remove(ox);  
            }  
        }  
        return false;  
    }  
  
    static class CommentFinder extends CallbackImpl {  
        List<Child> commentElements = new ArrayList<Child>();  
  
        public List<Object> apply(Object o) {  
            if (o instanceof jakarta.xml.bind.JAXBElement  
                    && (((JAXBElement) o).getName().getLocalPart()  
                            .equals("commentReference")  
                            || ((JAXBElement) o).getName().getLocalPart()  
                                    .equals("commentRangeStart") || ((JAXBElement) o)  
                            .getName().getLocalPart().equals("commentRangeEnd"))) {  
                System.out.println(((JAXBElement) o).getName().getLocalPart());  
                commentElements.add((Child) XmlUtils.unwrap(o));  
            } else if (o instanceof CommentReference  
                    || o instanceof CommentRangeStart  
                    || o instanceof CommentRangeEnd) {  
                System.out.println(o.getClass().getName());  
                commentElements.add((Child) o);  
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
                                    .equals("commentReference")  
                                    || ((JAXBElement) o).getName()  
                                            .getLocalPart()  
                                            .equals("commentRangeStart") || ((JAXBElement) o)  
                                    .getName().getLocalPart()  
                                    .equals("commentRangeEnd"))) {  
                        ((Child) ((JAXBElement) o).getValue())  
                                .setParent(XmlUtils.unwrap(parent));  
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
}  