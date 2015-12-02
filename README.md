# flume event header fields copy interceptor
This Flume interceptor extends [Keedio's enrichment interceptor](https://github.com/keedio/flume-enrichment-interceptor-skeleton) and adds the ability to copy Flume event header fieldsto the enriched extraData message.

Add the following properties to your agent configuration:

To configure your agent to use this interceptor, first configure all the properties documented in [Keedio's enrichment interceptor](https://github.com/keedio/flume-enrichment-interceptor-skeleton). Add the following properties:


	# interceptor
	a1.sources.r1.interceptors = i1
	a1.sources.r1.interceptors.i1.type = org.keedio.flume.interceptor.enrichment.FieldCopyInterceptor.Builder

	## other properties
	...

	# Full path to the properties file that contains the extra data to enrich the event with
	a1.sources.r1.interceptors.i1.properties.filename = /path/to/filename.properties
	# The format of incoming events ( DEFAULT | enriched )
	a1.sources.r1.interceptors.i1.event.type = DEFAULT