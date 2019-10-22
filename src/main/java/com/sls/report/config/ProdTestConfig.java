package com.sls.report.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class ProdTestConfig {

	@Bean(name = "prodtest")
	@ConfigurationProperties(prefix = "test.datasource")
	public DataSource prodTestDataSource() {
		return DataSourceBuilder.create().build();
	}
	
	@Bean(name = "prodtestJdbcTemplate")
	public JdbcTemplate jdbcTemplate(@Qualifier("prodtest") DataSource dsProdTest) {
		return new JdbcTemplate(dsProdTest);
	}
	
	/*@Primary
	@Bean(name="entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			EntityManagerFactoryBuilder builder, @Qualifier("dataSource") DataSource dataSource) {
		return builder.dataSource(dataSource).packages("com.sls.report.prodtest.entity").persistenceUnit("prodtest").build();
	}
	
	@Primary
	@Bean(name="transactionManager")
	public PlatformTransactionManager transactionManager(
		      @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}*/
	
	@Bean(name = "prod")
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource prodDataSource() {
		return DataSourceBuilder.create().build();
	}
	
	@Bean(name = "prodJdbcTemplate")
	public JdbcTemplate prodJdbcTemplate(@Qualifier("prod") DataSource dsProd) {
		return new JdbcTemplate(dsProd);
	}
	
}
