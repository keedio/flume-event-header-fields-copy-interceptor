# flume event header fields copy interceptor
This Flume interceptor extends [Keedio's enrichment interceptor](https://github.com/keedio/flume-enrichment-interceptor-skeleton) and adds the ability to copy Flume event header fieldsto the enriched extraData message.

Add the following properties to your agent configuration:

To configure your agent to use this interceptor, first configure all the properties documented in [Keedio's enrichment interceptor](https://github.com/keedio/flume-enrichment-interceptor-skeleton). Add the following properties:


	# interceptor
	a1.sources.r1.interceptors = i1
	a1.sources.r1.interceptors.i1.type = org.keedio.flume.interceptor.enrichment.FieldCopyInterceptor.Builder

	## other properties
	...

	# Fields to copy from the Flume event header to the extraData field
    a1.sources.r1.interceptors.i1.header.fields.to.copy.1 = header1
    a1.sources.r1.interceptors.i1.header.fields.to.copy.2 = header2
    	
This will produce an event whose body contains an enriched message in which the extraData field contains the values for headers `header1` and `header2` coming in the input flume event. 

If the specified headers do not exist, the extraData field won't be modified.