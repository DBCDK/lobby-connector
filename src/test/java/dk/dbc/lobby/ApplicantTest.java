package dk.dbc.lobby;

import dk.dbc.jsonb.JSONBContext;
import dk.dbc.jsonb.JSONBException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class ApplicantTest {
    private final JSONBContext jsonbContext = new JSONBContext();
    final String expectedJson =
            "{\"id\":\"42\",\"category\":\"bpf\",\"mimetype\":\"application/xml\",\"state\":\"PENDING\",\"body\":\"aGVsbG8gd29ybGQ=\",\"additionalInfo\":{\"localId\":\"bibID\",\"errors\":[\"err1\",\"err2\"]}}";

    @Test
    void jsonMarshalling() throws JSONBException {
        final AdditionalInfo additionalInfo = new AdditionalInfo();
        additionalInfo.localId = "bibID";
        additionalInfo.errors = Arrays.asList("err1", "err2");

        final Applicant entity = new Applicant();
        entity.setId("42");
        entity.setState(ApplicantState.PENDING);
        entity.setMimetype("application/xml");
        entity.setCategory("bpf");
        entity.setAdditionalInfo(additionalInfo);
        entity.setBody("hello world".getBytes(StandardCharsets.UTF_8));
        assertThat(jsonbContext.marshall(entity), is(expectedJson));
    }
    
    @Test
    void jsonUnmarshalling() throws JSONBException {
        final Applicant unmarshalled = jsonbContext.unmarshall(expectedJson, Applicant.class);
        assertThat(jsonbContext.marshall(unmarshalled), is(expectedJson));
    }

    private static class AdditionalInfo {
        public String localId;
        public List<String> errors;
    }
}
