### AI Task: Mapping Nested JSON Objects to Java Classes

I asked an AI tool how Gson maps nested JSON objects to Java classes.

The AI explained that a nested JSON object can be represented by creating a separate Java class and using that class as a field in the parent class.

For example, the schedule object inside a chain can be represented using a `ScheduleJson` class containing `days` and `time` fields. The parent `ChainJson` class then contains a `ScheduleJson` field.

Gson can automatically deserialize the nested JSON object into the corresponding Java object. This reduces the need to manually extract every nested property.

In my implementation, I used Gson's `JsonParser` and manually extracted the nested schedule properties. This allowed me to reuse my existing `Chain` and `Schedule` classes without changing their structure.

The AI's suggestion helped me understand how nested JSON objects can be mapped directly to Java classes. I retained my existing implementation because it was working and preserved compatibility with the XML loader.