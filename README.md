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
    a1.sources.r1.interceptors.i1.header.fields.to.copy.header1 = header1
    a1.sources.r1.interceptors.i1.header.fields.to.copy.header2 = header2
    a1.sources.r1.interceptors.i1.header.fields.to.copy.header3 = differentName3
    # Optional reverse.copy strategy (false by default)
    a1.sources.r1.interceptors.i1.reverse.copy = false
    
    	
This will produce an event whose body contains an enriched message in which the extraData field contains the values for headers `header1` and `header2` coming in the input flume event. Also, it will contain the value for header `header3` but with the name `differentName3` in the extraData field. 

If the specified headers do not exist, the extraData field won't be modified.

If the parameter `reverse.copy` contains true (false by default) the copy strategy will be reversed: The property keys `header1`, `header2` and `header3` will refer to extraData fields and values `header1`, `header2` and `differentName3` will refer to header fields where the values should be copied to.