/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package io.github.easy4j.doc.ognl;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

import ognl.MemberAccess;
import ognl.OgnlContext;

/**
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * https://cloud.tencent.com/developer/article/1554323
 */
public class DefaultMemberAccess implements MemberAccess {
	
    private final boolean allowPrivateAccess;
    private final boolean allowProtectedAccess;
    private final boolean allowPackageProtectedAccess;

    public DefaultMemberAccess(boolean allowAllAccess) {
        this(allowAllAccess, allowAllAccess, allowAllAccess);
    }

    public DefaultMemberAccess(boolean allowPrivateAccess, boolean allowProtectedAccess,
            boolean allowPackageProtectedAccess) {
        super();
        this.allowPrivateAccess = allowPrivateAccess;
        this.allowProtectedAccess = allowProtectedAccess;
        this.allowPackageProtectedAccess = allowPackageProtectedAccess;
    }

    @Override
    public Object setup(OgnlContext context, Object target, Member member, String propertyName) {
        Object result = null;

        if (isAccessible(context, target, member, propertyName)) {
            AccessibleObject accessible = (AccessibleObject) member;

            if (!accessible.isAccessible()) {
                result = Boolean.TRUE;
                accessible.setAccessible(true);
            }
        }
        return result;
    }

    @Override
    public void restore(OgnlContext context, Object target, Member member, String propertyName, Object state) {
        if (state != null) {
            ((AccessibleObject) member).setAccessible((Boolean) state);
        }
    }

    /**
     * Returns true if the given member is accessible or can be made accessible by this object.
     */
    @Override
    public boolean isAccessible(OgnlContext context, Object target, Member member, String propertyName) {
        int modifiers = member.getModifiers();
        if (Modifier.isPublic(modifiers)) {
            return true;
        } else if (Modifier.isPrivate(modifiers)) {
            return this.allowPrivateAccess;
        } else if (Modifier.isProtected(modifiers)) {
            return this.allowProtectedAccess;
        } else {
            return this.allowPackageProtectedAccess;
        }
    }

	public boolean isAllowPrivateAccess() {
		return allowPrivateAccess;
	}

	public boolean isAllowProtectedAccess() {
		return allowProtectedAccess;
	}

	public boolean isAllowPackageProtectedAccess() {
		return allowPackageProtectedAccess;
	}
    
}