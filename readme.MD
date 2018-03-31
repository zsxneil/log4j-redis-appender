This is an appender used for pushing log4j logs to redis.

you can use it in a log4j.xml like this:


    <appender name="logstash" class="com.github.zsxneil.log4j.RedisAppender">
		<param name="source" value="spring-application"/>
		<param name="type" value="redis-input"/>
		<param name="host" value="localhost"/>
		<param name="password" value="password"/>
		<param name="port" value="6379"/>
		<param name="key" value="logstash:redis"/>
		<param name="tags" value="test"/>
		<param name="mdc" value="true"/>
		<param name="location" value="true"/>
		<param name="appname" value="log4j-redis-appender"/>
    </appender>
