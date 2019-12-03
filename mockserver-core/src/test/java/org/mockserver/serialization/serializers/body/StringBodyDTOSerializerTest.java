package org.mockserver.serialization.serializers.body;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.mockserver.model.MediaType;
import org.mockserver.serialization.ObjectMapperFactory;
import org.mockserver.serialization.model.StringBodyDTO;
import org.mockserver.model.StringBody;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockserver.model.Not.not;

public class StringBodyDTOSerializerTest {

    @Test
    public void shouldSerializeStringBodyDTO() throws JsonProcessingException {
        assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new StringBodyDTO(new StringBody("string_body"))),
                is("\"string_body\""));
        assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new StringBodyDTO(new StringBody("string_body", false))),
            is("\"string_body\""));
    }

    @Test
    public void shouldSerializeStringBodyDTOWithSubString() throws JsonProcessingException {
        assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new StringBodyDTO(new StringBody("string_body", true))),
            is("{\"type\":\"STRING\",\"string\":\"string_body\",\"subString\":true}"));
    }

    @Test
    public void shouldSerializeStringBodyDTOWithCharset() throws JsonProcessingException {
        assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new StringBodyDTO(new StringBody("string_body", MediaType.PLAIN_TEXT_UTF_8))),
                is("{\"type\":\"STRING\",\"string\":\"string_body\",\"contentType\":\"text/plain; charset=utf-8\"}"));
    }

    @Test
    public void shouldSerializeStringBodyDTOWithNot() throws JsonProcessingException {
        assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new StringBodyDTO(not(new StringBody("string_body")))),
                is("{\"not\":true,\"type\":\"STRING\",\"string\":\"string_body\"}"));
    }

    @Test
    public void shouldSerializeStringBodyDTOWithCharsetAndNot() throws JsonProcessingException {
        assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new StringBodyDTO(not(new StringBody("string_body", MediaType.PLAIN_TEXT_UTF_8)))),
                is("{\"not\":true,\"type\":\"STRING\",\"string\":\"string_body\",\"contentType\":\"text/plain; charset=utf-8\"}"));
    }

}
