package com.Database;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.Database.CalowinDB.CalowinDBProperties;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class DataSourceConfig {

    @Autowired
    private CalowinDBProperties calowinDBProperties;

    @Bean(name = "calowinDBDataSource")
    public DataSource calowinDBDataSource() {
        AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
        ds.setUniqueResourceName("calowinDBDataSource");
        ds.setXaDataSourceClassName(calowinDBProperties.getXaDataSourceClass());
        ds.setXaProperties(calowinDBProperties.toXaProperties());
        ds.setMinPoolSize(calowinDBProperties.getMinPoolSize());
        ds.setMaxPoolSize(calowinDBProperties.getMaxPoolSize());
        return ds;
    }
    
}

