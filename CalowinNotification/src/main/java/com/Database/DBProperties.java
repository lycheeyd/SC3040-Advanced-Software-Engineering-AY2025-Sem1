package com.Database;

import java.util.Properties;

public abstract class DBProperties {
    private String xaDataSourceClass;
    private String serverName;
    private String portNumber;
    private String databaseName;
    private String user;
    private String password;
    private int minPoolSize;
    private int maxPoolSize;
    private boolean encrypt;
    private boolean trustServerCertificate;

    public Properties toXaProperties() {
        Properties properties = new Properties();
        properties.put("serverName", serverName);
        properties.put("portNumber", portNumber);
        properties.put("databaseName", databaseName);
        properties.put("user", user);
        properties.put("password", password);
        properties.put("encrypt", String.valueOf(encrypt));
        properties.put("trustServerCertificate", String.valueOf(trustServerCertificate));
        return properties;
    }

    public String getXaDataSourceClass() {
        return this.xaDataSourceClass;
    }

    public void setXaDataSourceClass(String xaDataSourceClass) {
        this.xaDataSourceClass = xaDataSourceClass;
    }

    public String getServerName() {
        return this.serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getPortNumber() {
        return this.portNumber;
    }

    public void setPortNumber(String portNumber) {
        this.portNumber = portNumber;
    }

    public String getDatabaseName() {
        return this.databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMinPoolSize() {
        return this.minPoolSize;
    }

    public void setMinPoolSize(int minPoolSize) {
        this.minPoolSize = minPoolSize;
    }

    public int getMaxPoolSize() {
        return this.maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public boolean isEncrypt() {
        return this.encrypt;
    }

    public boolean getEncrypt() {
        return this.encrypt;
    }

    public void setEncrypt(boolean encrypt) {
        this.encrypt = encrypt;
    }

    public boolean isTrustServerCertificate() {
        return this.trustServerCertificate;
    }

    public boolean getTrustServerCertificate() {
        return this.trustServerCertificate;
    }

    public void setTrustServerCertificate(boolean trustServerCertificate) {
        this.trustServerCertificate = trustServerCertificate;
    }
}
