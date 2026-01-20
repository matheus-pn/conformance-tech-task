package com.raidiam.api.oauth.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IntrospectionResponseTest {

    @Test
    void hasScope_withMatchingScope_shouldReturnTrue() {
        IntrospectionResponse response = new IntrospectionResponse();
        response.setScope("read write admin");

        assertThat(response.hasScope("read")).isTrue();
        assertThat(response.hasScope("write")).isTrue();
    }

    @Test
    void hasScope_withMissingOrNullScope_shouldReturnFalse() {
        IntrospectionResponse response = new IntrospectionResponse();

        assertThat(response.hasScope("read")).isFalse();

        response.setScope("");
        assertThat(response.hasScope("read")).isFalse();

        response.setScope("other");
        assertThat(response.hasScope("read")).isFalse();
    }

    @Test
    void hasScope_shouldNotMatchPartialScope() {
        IntrospectionResponse response = new IntrospectionResponse();
        response.setScope("readonly");

        assertThat(response.hasScope("read")).isFalse();
    }
}
