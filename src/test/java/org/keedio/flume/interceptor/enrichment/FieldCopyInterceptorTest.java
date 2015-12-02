package org.keedio.flume.interceptor.enrichment;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.log4j.Logger;
import org.keedio.flume.interceptor.enrichment.interceptor.EnrichedEventBody;
import org.keedio.flume.interceptor.enrichment.interceptor.EnrichmentInterceptor;
import org.keedio.flume.interceptor.enrichment.interceptor.EnrichmentInterceptorAbstractTest;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by Luca Rosellini lrosellini@keedio.com on 2/12/15.
 */
public class FieldCopyInterceptorTest extends EnrichmentInterceptorAbstractTest{
    private Logger logger = Logger.getLogger(FieldCopyInterceptorTest.class);

    @Test
    public void testSingleInterception() {
        try {
            Event event = createEvent("hello");
            event.getHeaders().put("filename","/Users/tmp/hola.txt");
            EnrichmentInterceptor interceptor = createInterceptor("", "DEFAULT");

            Event intercepted = interceptor.intercept(event);

            EnrichedEventBody enrichedEventBody = EnrichedEventBody.createFromEventBody(intercepted.getBody(), true);

            assertTrue(enrichedEventBody.getExtraData().containsKey("filename"));
            assertEquals(enrichedEventBody.getExtraData().get("filename"),"/Users/tmp/hola.txt");

        } catch (IOException e) {
            e.printStackTrace();
            junit.framework.Assert.fail();
        }
    }

    @Test
    public void testSingleInterceptionNoFieldInHeader() {
        try {
            Event event = createEvent("hello");

            EnrichmentInterceptor interceptor = createInterceptor("", "DEFAULT");

            Event intercepted = interceptor.intercept(event);

            EnrichedEventBody enrichedEventBody = EnrichedEventBody.createFromEventBody(intercepted.getBody(), true);

            assertFalse(enrichedEventBody.getExtraData().containsKey("filename"));

        } catch (IOException e) {
            e.printStackTrace();
            junit.framework.Assert.fail();
        }
    }

    @Test
    public void testSingleInterceptionEmptyFieldList() {
        try {
            Event event = createEvent("hello");
            event.getHeaders().put("filename","/Users/tmp/hola.txt");
            String originalMessage = new String(event.getBody());

            EnrichmentInterceptor interceptor = createInterceptorNoFields("", "DEFAULT");

            Event intercepted = interceptor.intercept(event);

            EnrichedEventBody enrichedEventBody = EnrichedEventBody.createFromEventBody(intercepted.getBody(), true);

            assertFalse(enrichedEventBody.getExtraData().containsKey("filename"));

        } catch (IOException e) {
            e.printStackTrace();
            junit.framework.Assert.fail();
        }
    }

    protected FieldCopyInterceptor createInterceptorNoFields(String filename, String eventType) {
        Context context = new Context();
        context.put(EnrichmentInterceptor.EVENT_TYPE, eventType);

        FieldCopyInterceptor.Builder builder = new FieldCopyInterceptor.Builder();
        builder.configure(context);
        FieldCopyInterceptor interceptor = (FieldCopyInterceptor) builder.build();
        interceptor.initialize();
        return interceptor;
    }

    @Override
    protected FieldCopyInterceptor createInterceptor(String filename, String eventType) {
        Context context = new Context();
        context.put(EnrichmentInterceptor.EVENT_TYPE, eventType);
        context.put("header.fields.to.copy.1","filename");

        FieldCopyInterceptor.Builder builder = new FieldCopyInterceptor.Builder();
        builder.configure(context);
        FieldCopyInterceptor interceptor = (FieldCopyInterceptor) builder.build();
        interceptor.initialize();
        return interceptor;
    }
}
