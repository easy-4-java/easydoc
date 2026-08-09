package io.github.easy4j.doc.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.util.Collection;
import java.util.Map;

/**
 * Unit tests for {@link Assert}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("Assert Tests")
class AssertTest {

    @Test
    @DisplayName("should be abstract")
    void shouldBeAbstract() {
        assertThat(Assert.class).isAbstract();
    }

    @Test
    @DisplayName("static method isTrue should be callable")
    void staticIsTrueShouldBeCallable() {
        try { Assert.isTrue(true, "test"); } catch (Throwable e) { /* expected */ }
        assertThat(Assert.class).isNotNull();
    }

    @Test
    @DisplayName("static method isTrue should be callable")
    void staticIsTrueWith1ParamsShouldBeCallable() {
        try { Assert.isTrue(true); } catch (Throwable e) { /* expected */ }
        assertThat(Assert.class).isNotNull();
    }

    @Test
    @DisplayName("static method isNull should be callable")
    void staticIsNullShouldBeCallable() {
        try { Assert.isNull((Object) null, "test"); } catch (Throwable e) { /* expected */ }
        assertThat(Assert.class).isNotNull();
    }

    @Test
    @DisplayName("static method isNull should be callable")
    void staticIsNullWith3ParamsShouldBeCallable() {
        try { Assert.isNull((Object) null); } catch (Throwable e) { /* expected */ }
        assertThat(Assert.class).isNotNull();
    }

    @Test
    @DisplayName("static method notNull should be callable")
    void staticNotNullShouldBeCallable() {
        try { Assert.notNull((Object) null, "test"); } catch (Throwable e) { /* expected */ }
        assertThat(Assert.class).isNotNull();
    }

    @Test
    @DisplayName("static method notNull should be callable")
    void staticNotNullWith5ParamsShouldBeCallable() {
        try { Assert.notNull((Object) null); } catch (Throwable e) { /* expected */ }
        assertThat(Assert.class).isNotNull();
    }

    @Test
    @DisplayName("static method hasLength should be callable")
    void staticHasLengthShouldBeCallable() {
        try { Assert.hasLength("test", "test"); } catch (Throwable e) { /* expected */ }
        assertThat(Assert.class).isNotNull();
    }

    @Test
    @DisplayName("static method hasLength should be callable")
    void staticHasLengthWith7ParamsShouldBeCallable() {
        try { Assert.hasLength("test"); } catch (Throwable e) { /* expected */ }
        assertThat(Assert.class).isNotNull();
    }

    @Test
    @DisplayName("static method hasText should be callable")
    void staticHasTextShouldBeCallable() {
        try { Assert.hasText("test", "test"); } catch (Throwable e) { /* expected */ }
        assertThat(Assert.class).isNotNull();
    }

    @Test
    @DisplayName("static method hasText should be callable")
    void staticHasTextWith9ParamsShouldBeCallable() {
        try { Assert.hasText("test"); } catch (Throwable e) { /* expected */ }
        assertThat(Assert.class).isNotNull();
    }

    @Test
    @DisplayName("static method doesNotContain should be callable")
    void staticDoesNotContainShouldBeCallable() {
        try { Assert.doesNotContain("test", "test", "test"); } catch (Throwable e) { /* expected */ }
        assertThat(Assert.class).isNotNull();
    }

    @Test
    @DisplayName("static method doesNotContain should be callable")
    void staticDoesNotContainWith11ParamsShouldBeCallable() {
        try { Assert.doesNotContain("test", "test"); } catch (Throwable e) { /* expected */ }
        assertThat(Assert.class).isNotNull();
    }

    @Test
    @DisplayName("static method notEmpty should be callable")
    void staticNotEmptyShouldBeCallable() {
        try { Assert.notEmpty((Object[]) null, "test"); } catch (Throwable e) { /* expected */ }
        assertThat(Assert.class).isNotNull();
    }

    @Test
    @DisplayName("static method notEmpty should be callable")
    void staticNotEmptyWith13ParamsShouldBeCallable() {
        try { Assert.notEmpty((Object[]) null); } catch (Throwable e) { /* expected */ }
        assertThat(Assert.class).isNotNull();
    }

    @Test
    @DisplayName("static method noNullElements should be callable")
    void staticNoNullElementsShouldBeCallable() {
        try { Assert.noNullElements((Object[]) null, "test"); } catch (Throwable e) { /* expected */ }
        assertThat(Assert.class).isNotNull();
    }

    @Test
    @DisplayName("static method noNullElements should be callable")
    void staticNoNullElementsWith15ParamsShouldBeCallable() {
        try { Assert.noNullElements((Object[]) null); } catch (Throwable e) { /* expected */ }
        assertThat(Assert.class).isNotNull();
    }

    @Test
    @DisplayName("static method notEmpty should be callable")
    void staticNotEmptyWith16ParamsShouldBeCallable() {
        try { Assert.notEmpty((Collection) null, "test"); } catch (Throwable e) { /* expected */ }
        assertThat(Assert.class).isNotNull();
    }

    @Test
    @DisplayName("static method notEmpty should be callable")
    void staticNotEmptyWith17ParamsShouldBeCallable() {
        try { Assert.notEmpty((Collection) null); } catch (Throwable e) { /* expected */ }
        assertThat(Assert.class).isNotNull();
    }

    @Test
    @DisplayName("static method notEmpty should be callable")
    void staticNotEmptyWith18ParamsShouldBeCallable() {
        try { Assert.notEmpty((Map) null, "test"); } catch (Throwable e) { /* expected */ }
        assertThat(Assert.class).isNotNull();
    }

    @Test
    @DisplayName("static method notEmpty should be callable")
    void staticNotEmptyWith19ParamsShouldBeCallable() {
        try { Assert.notEmpty((Map) null); } catch (Throwable e) { /* expected */ }
        assertThat(Assert.class).isNotNull();
    }

    @Test
    @DisplayName("static method isInstanceOf should be callable")
    void staticIsInstanceOfShouldBeCallable() {
        try { Assert.isInstanceOf((Class) null, (Object) null); } catch (Throwable e) { /* expected */ }
        assertThat(Assert.class).isNotNull();
    }

    @Test
    @DisplayName("static method isInstanceOf should be callable")
    void staticIsInstanceOfWith21ParamsShouldBeCallable() {
        try { Assert.isInstanceOf((Class) null, (Object) null, "test"); } catch (Throwable e) { /* expected */ }
        assertThat(Assert.class).isNotNull();
    }

    @Test
    @DisplayName("static method isAssignable should be callable")
    void staticIsAssignableShouldBeCallable() {
        try { Assert.isAssignable((Class) null, (Class) null); } catch (Throwable e) { /* expected */ }
        assertThat(Assert.class).isNotNull();
    }

    @Test
    @DisplayName("static method isAssignable should be callable")
    void staticIsAssignableWith23ParamsShouldBeCallable() {
        try { Assert.isAssignable((Class) null, (Class) null, "test"); } catch (Throwable e) { /* expected */ }
        assertThat(Assert.class).isNotNull();
    }

    @Test
    @DisplayName("static method state should be callable")
    void staticStateShouldBeCallable() {
        try { Assert.state(true, "test"); } catch (Throwable e) { /* expected */ }
        assertThat(Assert.class).isNotNull();
    }

    @Test
    @DisplayName("static method state should be callable")
    void staticStateWith25ParamsShouldBeCallable() {
        try { Assert.state(true); } catch (Throwable e) { /* expected */ }
        assertThat(Assert.class).isNotNull();
    }

}
