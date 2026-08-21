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
package io.github.easy4j.doc.ognl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class DefaultMemberAccessTest {

	public static class Bean {
		public String pub = "p";
		@SuppressWarnings("unused")
		private String priv = "x";
	}

	@Test
	void finalFieldsAreFalseByDefault() {
		DefaultMemberAccess access = new DefaultMemberAccess(false);
		assertFalse(access.isAllowPrivateAccess());
		assertFalse(access.isAllowProtectedAccess());
		assertFalse(access.isAllowPackageProtectedAccess());
	}

	@Test
	void singleArgTrueSetsAllFlags() {
		DefaultMemberAccess access = new DefaultMemberAccess(true);
		assertTrue(access.isAllowPrivateAccess());
		assertTrue(access.isAllowProtectedAccess());
		assertTrue(access.isAllowPackageProtectedAccess());
	}

	@Test
	void threeArgConstructorRespected() {
		DefaultMemberAccess access = new DefaultMemberAccess(true, false, true);
		assertTrue(access.isAllowPrivateAccess());
		assertFalse(access.isAllowProtectedAccess());
		assertTrue(access.isAllowPackageProtectedAccess());
	}

	@Test
	void isAccessiblePublicFieldAlwaysTrue() throws Exception {
		Field pub = Bean.class.getDeclaredField("pub");
		Map<String, Object> ctx = new HashMap<>();
		assertTrue(new DefaultMemberAccess(false).isAccessible(ctx, new Bean(), pub, "pub"));
	}

	@Test
	void isAccessiblePrivateFieldFalseByDefault() throws Exception {
		Field priv = Bean.class.getDeclaredField("priv");
		Map<String, Object> ctx = new HashMap<>();
		assertFalse(new DefaultMemberAccess(false).isAccessible(ctx, new Bean(), priv, "priv"));
	}

	@Test
	void isAccessiblePrivateFieldTrueWhenAllowAll() throws Exception {
		Field priv = Bean.class.getDeclaredField("priv");
		Map<String, Object> ctx = new HashMap<>();
		assertTrue(new DefaultMemberAccess(true).isAccessible(ctx, new Bean(), priv, "priv"));
	}

	@Test
	void setupOnPublicFieldCapturesState() throws Exception {
		Field pub = Bean.class.getDeclaredField("pub");
		Map<String, Object> ctx = new HashMap<>();
		// The implementation always calls setAccessible(true) and stores the prior
		// accessibility state, so a public field whose isAccessible() is false initially
		// returns Boolean.TRUE (the captured prior state).
		Object state = new DefaultMemberAccess(false).setup(ctx, new Bean(), pub, "pub");
		assertSame(Boolean.TRUE, state);
	}

	@Test
	void setupOnAccessiblePrivateFieldReturnsBoolean() throws Exception {
		Field priv = Bean.class.getDeclaredField("priv");
		Map<String, Object> ctx = new HashMap<>();
		Object state = new DefaultMemberAccess(true).setup(ctx, new Bean(), priv, "priv");
		assertSame(Boolean.TRUE, state);
	}

	@Test
	void setupOnInaccessiblePrivateFieldReturnsNull() throws Exception {
		Field priv = Bean.class.getDeclaredField("priv");
		Map<String, Object> ctx = new HashMap<>();
		assertNull(new DefaultMemberAccess(false).setup(ctx, new Bean(), priv, "priv"));
	}

	@Test
	void restoreNullIsNoop() throws Exception {
		Field priv = Bean.class.getDeclaredField("priv");
		Map<String, Object> ctx = new HashMap<>();
		// Should not throw.
		new DefaultMemberAccess(true).restore(ctx, new Bean(), priv, "priv", null);
	}

	@Test
	void restoreBooleanResetsState() throws Exception {
		Field priv = Bean.class.getDeclaredField("priv");
		Map<String, Object> ctx = new HashMap<>();
		boolean initialAccessible = priv.isAccessible();
		Object state = new DefaultMemberAccess(true).setup(ctx, new Bean(), priv, "priv");
		assertSame(Boolean.TRUE, state);
		new DefaultMemberAccess(true).restore(ctx, new Bean(), priv, "priv", initialAccessible);
		assertEquals(initialAccessible, priv.isAccessible());
	}
}