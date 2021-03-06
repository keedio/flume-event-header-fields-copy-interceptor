package org.keedio.flume.interceptor.enrichment;

import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;
import org.keedio.flume.interceptor.enrichment.interceptor.EnrichedEventBody;
import org.keedio.flume.interceptor.enrichment.interceptor.EnrichmentInterceptor;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Extends Enrichment interceptor to add flume event header copy functionality.
 *
 * Created by Luca Rosellini lrosellini@keedio.com on 2/12/15.
 */
public class FieldCopyInterceptor extends EnrichmentInterceptor {

    private Map<String, String> fieldsToCopy;
    private boolean reverseMapping;

    /**
     * Default constructor.
     *
     * @param context
     */
    public FieldCopyInterceptor(Context context) {
        super(context);

        Map<String, String> ftoCopy = this.context.getSubProperties("header.fields.to.copy.");
        fieldsToCopy = ftoCopy != null && ftoCopy.values().size() > 0 ? ftoCopy : null;

        this.reverseMapping = this.context.getBoolean("reverse.copy", false);
    }

    @Override
    protected void addAdditionalFields(Event event, EnrichedEventBody enrichedBody) {
        if (fieldsToCopy == null) {
            return;
        }

        Map<String, String> data = enrichedBody.getExtraData();
        Map<String, String> headers = event.getHeaders();

        if (!reverseMapping) {
            // Default strategy
            for (Map.Entry<String, String> entry : fieldsToCopy.entrySet()) {
                if (headers.containsKey(entry.getKey()))
                    data.put(entry.getValue(), headers.get(entry.getKey()));
            }
        } else {
            // Reverse strategy
            for (Map.Entry<String, String> entry : fieldsToCopy.entrySet()) {
                if (data.containsKey(entry.getKey()))
                    headers.put(entry.getValue(), data.get(entry.getKey()));
            }
        }

    }

    public static class Builder implements Interceptor.Builder {
        private Context ctx;

        @Override
        public Interceptor build() {
            return new FieldCopyInterceptor(ctx);
        }

        @Override
        public void configure(Context context) {
            this.ctx = context;
        }
    }
}
