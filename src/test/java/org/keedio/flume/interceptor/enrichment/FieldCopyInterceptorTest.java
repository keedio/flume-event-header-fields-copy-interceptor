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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Luca Rosellini lrosellini@keedio.com on 2/12/15.
 */
public class FieldCopyInterceptorTest extends EnrichmentInterceptorAbstractTest{
    private Logger logger = Logger.getLogger(FieldCopyInterceptorTest.class);

    //----------------- TESTS ------------------//
    //------------ DEFAULT STRATEGY ------------//

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
    public void testSingleInterceptionDifferentName() {
        String sourceName = "filename";
        String targetName = "filenamefinal";
        String testValue = "/Users/tmp/hola.txt";
        try {
            Event event = createEvent("hello");
            event.getHeaders().put(sourceName, testValue);
            EnrichmentInterceptor interceptor = createInterceptorDifferentName(sourceName, targetName, "DEFAULT");

            Event intercepted = interceptor.intercept(event);

            EnrichedEventBody enrichedEventBody = EnrichedEventBody.createFromEventBody(intercepted.getBody(), true);

            assertTrue(enrichedEventBody.getExtraData().containsKey(targetName));
            assertEquals(enrichedEventBody.getExtraData().get(targetName), testValue);

        } catch (IOException e) {
            e.printStackTrace();
            junit.framework.Assert.fail();
        }
    }

    @Test
    public void testMultipleInterceptionDifferentName() {
        Map<String, String> headerToExtraData = new HashMap<>();
        headerToExtraData.put("filename", "filename");  // First with same name
        headerToExtraData.put("filename", "filenamefinal");  // Second with different name
        String testValue = "/Users/tmp/hola.txt";  // Same value for both extraData fields
        try {
            Event event = createEvent("hello");

            for (Map.Entry<String, String> entry : headerToExtraData.entrySet()) {
                event.getHeaders().put(entry.getKey(), testValue);
            }

            EnrichmentInterceptor interceptor = createInterceptorMultipleDifferentName(headerToExtraData, "DEFAULT");

            Event intercepted = interceptor.intercept(event);

            EnrichedEventBody enrichedEventBody = EnrichedEventBody.createFromEventBody(intercepted.getBody(), true);

            // Test all headerToExtraData mappings
            for (Map.Entry<String, String> entry : headerToExtraData.entrySet()) {
                assertTrue(enrichedEventBody.getExtraData().containsKey(entry.getValue()));
                assertEquals(enrichedEventBody.getExtraData().get(entry.getValue()), testValue);
            }

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

    //------------ REVERSE STRATEGY ------------//

    @Test
    public void testSingleReverseInterception() {
        Event event = createEvent("hello");
        Map<String, String> extraData = new HashMap<>();
        extraData.put("filename", "/Users/tmp/hola.txt");
        EnrichmentInterceptor interceptor = createSingleReverseInterceptor(event, "DEFAULT", extraData);

        Event intercepted = interceptor.intercept(event);

        assertTrue(intercepted.getHeaders().containsKey("filename"));
        assertEquals(intercepted.getHeaders().get("filename"),"/Users/tmp/hola.txt");
    }

    @Test
    public void testSingleReverseInterceptionDifferentName() {
        Event event = createEvent("hello");
        String sourceName = "filename";
        String targetName = "filenamefinal";
        Map<String, String> extraData = new HashMap<>();
        extraData.put("filename", "/Users/tmp/hola.txt");
        EnrichmentInterceptor interceptor = createReverseInterceptorDifferentName(event, "DEFAULT", extraData, sourceName, targetName);

        Event intercepted = interceptor.intercept(event);

        assertTrue(intercepted.getHeaders().containsKey("filenamefinal"));
        assertEquals(intercepted.getHeaders().get("filenamefinal"),"/Users/tmp/hola.txt");
    }


    @Test
    public void testMultipleReverseInterceptionDifferentName() {
        Event event = createEvent("hello");
        Map<String, String> extraData = new HashMap<>();
        extraData.put("filename", "/Users/tmp/hola.txt");
        Map<String, String> extraDataToHeader = new HashMap<>();
        extraDataToHeader.put("filename", "filename");  // First with same name
        extraDataToHeader.put("filename", "filenamefinal");  // Second with different name

        EnrichmentInterceptor interceptor = createReverseInterceptorMultipleDifferentName(event, "DEFAULT", extraData, extraDataToHeader);

        Event intercepted = interceptor.intercept(event);

        // Test all extraDataToHeader mappings
        for (Map.Entry<String, String> entry : extraDataToHeader.entrySet()) {
            assertTrue(intercepted.getHeaders().containsKey(entry.getValue()));
            assertEquals(intercepted.getHeaders().get(entry.getValue()), "/Users/tmp/hola.txt");
        }
    }

    @Test
    public void testSingleReverseInterceptionNoFieldInHeader() {
        Event event = createEvent("hello");
        Map<String, String> extraData = new HashMap<>();
        EnrichmentInterceptor interceptor = createSingleReverseInterceptor(event, "DEFAULT", extraData);

        Event intercepted = interceptor.intercept(event);

        assertFalse(intercepted.getHeaders().containsKey("filename"));
    }

    @Test
    public void testSingleReverseInterceptionEmptyFieldList() {
        Event event = createEvent("hello");
        Map<String, String> extraData = new HashMap<>();
        extraData.put("filename", "/Users/tmp/hola.txt");
        EnrichmentInterceptor interceptor = createSingleReverseInterceptorNoFields(event, "DEFAULT", extraData);

        Event intercepted = interceptor.intercept(event);

        assertFalse(intercepted.getHeaders().containsKey("filename"));
    }

    //--------------- CREATORS -----------------//
    //------------ DEFAULT STRATEGY ------------//

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
        context.put("header.fields.to.copy.filename","filename");

        FieldCopyInterceptor.Builder builder = new FieldCopyInterceptor.Builder();
        builder.configure(context);
        FieldCopyInterceptor interceptor = (FieldCopyInterceptor) builder.build();
        interceptor.initialize();
        return interceptor;
    }

    protected FieldCopyInterceptor createInterceptorDifferentName(String sourceName, String targetName, String eventType) {
        Context context = new Context();
        context.put(EnrichmentInterceptor.EVENT_TYPE, eventType);
        context.put("header.fields.to.copy.".concat(sourceName), targetName);

        FieldCopyInterceptor.Builder builder = new FieldCopyInterceptor.Builder();
        builder.configure(context);
        FieldCopyInterceptor interceptor = (FieldCopyInterceptor) builder.build();
        interceptor.initialize();
        return interceptor;
    }


    protected EnrichmentInterceptor createInterceptorMultipleDifferentName(Map<String, String> headerToExtraData, String eventType) {
        Context context = new Context();
        context.put(EnrichmentInterceptor.EVENT_TYPE, eventType);

        for (Map.Entry<String, String> entry : headerToExtraData.entrySet()) {
            context.put("header.fields.to.copy.".concat(entry.getKey()), entry.getValue());
        }

        FieldCopyInterceptor.Builder builder = new FieldCopyInterceptor.Builder();
        builder.configure(context);
        FieldCopyInterceptor interceptor = (FieldCopyInterceptor) builder.build();
        interceptor.initialize();
        return interceptor;
    }

    //------------ REVERSE STRATEGY ------------//

    protected FieldCopyInterceptor createSingleReverseInterceptorNoFields(Event event, String eventType, Map<String, String> extraData) {
        Context context = new Context();
        context.put(EnrichmentInterceptor.EVENT_TYPE, eventType);
        context.put("reverse.copy","true");

        FieldCopyInterceptor.Builder builder = new FieldCopyInterceptor.Builder();
        builder.configure(context);
        FieldCopyInterceptor interceptor = (FieldCopyInterceptor) builder.build();
        interceptor.initialize();
        interceptor.addAdditionalFields(event, new EnrichedEventBody(extraData, event.getBody().toString()));

        return interceptor;
    }

    protected FieldCopyInterceptor createSingleReverseInterceptor(Event event, String eventType, Map<String, String> extraData) {
        Context context = new Context();
        context.put(EnrichmentInterceptor.EVENT_TYPE, eventType);
        context.put("header.fields.to.copy.filename","filename");
        context.put("reverse.copy","true");

        FieldCopyInterceptor.Builder builder = new FieldCopyInterceptor.Builder();
        builder.configure(context);
        FieldCopyInterceptor interceptor = (FieldCopyInterceptor) builder.build();
        interceptor.initialize();
        interceptor.addAdditionalFields(event, new EnrichedEventBody(extraData, event.getBody().toString()));

        return interceptor;
    }

    protected FieldCopyInterceptor createReverseInterceptorDifferentName(Event event, String eventType, Map<String, String> extraData, String sourceName, String targetName) {
        Context context = new Context();
        context.put(EnrichmentInterceptor.EVENT_TYPE, eventType);
        context.put("header.fields.to.copy.".concat(sourceName), targetName);
        context.put("reverse.copy","true");

        FieldCopyInterceptor.Builder builder = new FieldCopyInterceptor.Builder();
        builder.configure(context);
        FieldCopyInterceptor interceptor = (FieldCopyInterceptor) builder.build();
        interceptor.initialize();
        interceptor.addAdditionalFields(event, new EnrichedEventBody(extraData, event.getBody().toString()));

        return interceptor;
    }

    protected FieldCopyInterceptor createReverseInterceptorMultipleDifferentName(Event event, String eventType, Map<String, String> extraData, Map<String, String> extraDataToHeader) {
        Context context = new Context();
        context.put(EnrichmentInterceptor.EVENT_TYPE, eventType);
        for (Map.Entry<String, String> entry : extraDataToHeader.entrySet()) {
            context.put("header.fields.to.copy.".concat(entry.getKey()), entry.getValue());
        }
        context.put("reverse.copy","true");

        FieldCopyInterceptor.Builder builder = new FieldCopyInterceptor.Builder();
        builder.configure(context);
        FieldCopyInterceptor interceptor = (FieldCopyInterceptor) builder.build();
        interceptor.initialize();
        interceptor.addAdditionalFields(event, new EnrichedEventBody(extraData, event.getBody().toString()));

        return interceptor;
    }

}
