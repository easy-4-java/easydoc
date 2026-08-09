package io.github.easy4j.doc.bus.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import org.docx4j.events.JobIdentifier;

/**
 * Unit tests for {@link BuildJobTypes}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("BuildJobTypes Tests")
class BuildJobTypesTest {

    @Test
    @DisplayName("should contain expected enum constants")
    void shouldContainExpectedConstants() {
        assertThat(BuildJobTypes.values()).isNotEmpty();
        assertThat(BuildJobTypes.valueOf("DOC")).isEqualTo(BuildJobTypes.DOC);
        assertThat(BuildJobTypes.valueOf("HTML")).isEqualTo(BuildJobTypes.HTML);
        assertThat(BuildJobTypes.valueOf("URL")).isEqualTo(BuildJobTypes.URL);
    }

    @Test
    @DisplayName("should have correct number of constants")
    void shouldHaveCorrectNumberOfConstants() {
        assertThat(BuildJobTypes.values().length).isEqualTo(3);
    }

}
