package com.github.zsxneil.log4j;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import java.util.Arrays;
import java.util.Iterator;

public class RedisAppender extends AppenderSkeleton {


    private String host = "localhost";
    private int port = 6379;
    private String password;
    private String key = "log4j-log";
    private int database = Protocol.DEFAULT_DATABASE;

    // 连接超时
    private int timeout = Protocol.DEFAULT_TIMEOUT;
    private long minEvictableIdleTimeMillis = 60000L;
    private long timeBetweenEvictionRunsMillis = 30000L;
    private int numTestsPerEvictionRun = -1;
    private int maxTotal = 8;
    private int maxIdle = 0;
    private int minIdle = 0;
    private boolean blockWhenExhaused = false;
    private String evictionPolicyClassName;
    private boolean lifo = false;
    private boolean testOnBorrow = false;
    private boolean testWhileIdle = false;
    private boolean testOnReturn = false;

    private String dateFormatter = "yyyy-MM-dd'T'HH:mm:ss.SSS'+0800'";

    static private boolean jedisHeath = true;

    static private JedisPool jedisPool;

    // keep this for config compatibility for now
    JSONEventLayout jsonlayout;

    public RedisAppender() {
        jsonlayout = new JSONEventLayout();
    }

    @Override
    public void activateOptions() {
        super.activateOptions();

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        if (lifo) {
            poolConfig.setLifo(lifo);
        }
        if (testOnBorrow) {
            poolConfig.setTestOnBorrow(testOnBorrow);
        }
        if (isTestWhileIdle()) {
            poolConfig.setTestWhileIdle(isTestWhileIdle());
        }
        if (testOnReturn) {
            poolConfig.setTestOnReturn(testOnReturn);
        }
        if (timeBetweenEvictionRunsMillis > 0) {
            poolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        }
        if (evictionPolicyClassName != null && evictionPolicyClassName.length() > 0) {
            poolConfig.setEvictionPolicyClassName(evictionPolicyClassName);
        }
        if (blockWhenExhaused) {
            poolConfig.setBlockWhenExhausted(blockWhenExhaused);
        }
        if (minIdle > 0) {
            poolConfig.setMinIdle(minIdle);
        }
        if (maxIdle > 0) {
            poolConfig.setMaxIdle(maxIdle);
        }
        if (numTestsPerEvictionRun > 0) {
            poolConfig.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
        }
        if (maxTotal != 8) {
            poolConfig.setMaxTotal(maxTotal);
        }
        if (minEvictableIdleTimeMillis > 0) {
            poolConfig.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        }

        if (password != null && password.length() > 0) {
            jedisPool = new JedisPool(poolConfig, host, port, timeout, password);
        } else {
            jedisPool = new JedisPool(poolConfig, host, port, timeout);
        }

        // 配置连接实验
        try {
            Jedis jedis = jedisPool.getResource();
           jedis.select(database);
        } catch (Exception e) {
            jedisHeath = false;
            LogLog.error("Redis is can not connected", e);
        }

    }

    protected void append(LoggingEvent event) {

        Jedis client = jedisPool.getResource();
        try {
            String json = jsonlayout.format(event);
            //System.out.println("format:" + json);
            client.rpush(key, json);
        } catch (Exception e) {
            e.printStackTrace();
            client.close();
            client = null;
        } finally {
            if (client != null) {
                client.close();
            }
        }

    }

    public void close() {
        jedisPool.destroy();
    }

    public boolean requiresLayout() {
        return false;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }



    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public long getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public long getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public int getNumTestsPerEvictionRun() {
        return numTestsPerEvictionRun;
    }

    public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
        this.numTestsPerEvictionRun = numTestsPerEvictionRun;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public boolean isBlockWhenExhaused() {
        return blockWhenExhaused;
    }

    public void setBlockWhenExhaused(boolean blockWhenExhaused) {
        this.blockWhenExhaused = blockWhenExhaused;
    }

    public String getEvictionPolicyClassName() {
        return evictionPolicyClassName;
    }

    public void setEvictionPolicyClassName(String evictionPolicyClassName) {
        this.evictionPolicyClassName = evictionPolicyClassName;
    }

    public boolean isLifo() {
        return lifo;
    }

    public void setLifo(boolean lifo) {
        this.lifo = lifo;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public String getDateFormatter() {
        return dateFormatter;
    }

    public void setDateFormatter(String dateFormatter) {
        this.dateFormatter = dateFormatter;
    }

    @Deprecated
    public String getSource() {
        return jsonlayout.getSource();
    }

    @Deprecated
    public void setSource(String source) {
        jsonlayout.setSource(source);
    }

    @Deprecated
    public String getSourceHost() {
        return jsonlayout.getSourceHost();
    }

    @Deprecated
    public void setSourceHost(String sourceHost) {
        jsonlayout.setSourceHost(sourceHost);
    }

    @Deprecated
    public String getSourcePath() {
        return jsonlayout.getSourcePath();
    }

    @Deprecated
    public void setSourcePath(String sourcePath) {
        jsonlayout.setSourcePath(sourcePath);
    }

    @Deprecated
    public String getTags() {
        if (jsonlayout.getTags() != null) {
            Iterator<String> i = jsonlayout.getTags().iterator();
            StringBuilder sb = new StringBuilder();
            while (i.hasNext()) {
                sb.append(i.next());
                if (i.hasNext()) {
                    sb.append(',');
                }
            }
            return sb.toString();
        }
        return null;
    }

    @Deprecated
    public void setTags(String tags) {
        if (tags != null) {
            String[] atags = tags.split(",");
            jsonlayout.setTags(Arrays.asList(atags));
        }
    }

    @Deprecated
    public String getType() {
        return jsonlayout.getType();
    }

    @Deprecated
    public void setType(String type) {
        jsonlayout.setType(type);
    }

    @Deprecated
    public void setMdc(boolean flag) {
        jsonlayout.setProperties(flag);
    }

    @Deprecated
    public boolean getMdc() {
        return jsonlayout.getProperties();
    }

    @Deprecated
    public void setLocation(boolean flag) {
        jsonlayout.setLocationInfo(flag);
    }

    @Deprecated
    public boolean getLocation() {
        return jsonlayout.getLocationInfo();
    }

    @Deprecated
    public String getAppname() {
        return jsonlayout.getAppname();
    }

    public void setAppname(String appname) {
        jsonlayout.setAppname(appname);
    }




}
