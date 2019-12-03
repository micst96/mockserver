package org.mockserver.serialization.serializers.body;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.mockserver.model.MediaType;
import org.mockserver.serialization.ObjectMapperFactory;
import org.mockserver.serialization.model.XmlBodyDTO;
import org.mockserver.model.XmlBody;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class XmlBodyDTOSerializerTest {

    @Test
    public void shouldSerializeXmlBodyDTO() throws JsonProcessingException {
        assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new XmlBodyDTO(new XmlBody("<some><xml></xml></some>"))),
                is("{\"type\":\"XML\",\"xml\":\"<some><xml></xml></some>\"}"));
    }

    @Test
    public void shouldSerializeXmlBodyDTOWithContentType() throws JsonProcessingException {
        assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new XmlBodyDTO(new XmlBody("<some><xml></xml></some>", MediaType.XML_UTF_8))),
                is("{\"type\":\"XML\",\"xml\":\"<some><xml></xml></some>\",\"contentType\":\"text/xml; charset=utf-8\"}"));
    }

    @Test
    public void shouldSerializeXmlBodyDTOWithNot() throws JsonProcessingException {
        assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new XmlBodyDTO(new XmlBody("<some><xml></xml></some>"), true)),
                is("{\"not\":true,\"type\":\"XML\",\"xml\":\"<some><xml></xml></some>\"}"));
    }

    @Test
    public void shouldSerializeXmlBodyDTOWithNotWithContentType() throws JsonProcessingException {
        assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new XmlBodyDTO(new XmlBody("<some><xml></xml></some>", MediaType.XML_UTF_8), true)),
                is("{\"not\":true,\"type\":\"XML\",\"xml\":\"<some><xml></xml></some>\",\"contentType\":\"text/xml; charset=utf-8\"}"));
    }

}