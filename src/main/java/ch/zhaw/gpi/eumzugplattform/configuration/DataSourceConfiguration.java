package ch.zhaw.gpi.eumzugplattform.configuration;

import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Konfigurationsklasse für Datenquellen
 * 
 * Spring Boot nutzt standardmässig nur eine Datenbank, welche in application.properties
 * konfiguriert wird. Möchte man mehrere, müssen diese in einer separaten Konfigurations-
 * Klasse (diese) gebildet werden basierend auf separaten Konfigurationen, z.B. im
 * application.properties-File. Es wurde hier eine Kombination gewählt aus 
 * https://docs.camunda.org/manual/develop/user-guide/spring-boot-integration/configuration/#defaultdatasourceconfiguration
 * und https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#howto-two-datasources
 * 
 * @author scep basierend auf Inputs mehrerer Studierendengruppen (siehe README)
 */
@Configuration
public class DataSourceConfiguration {

    /**
     * Basierend auf den Einstellungen unter relocation.datasource in application.properties
     * wird eine DataSourceProperties-Bean erstellt, welche in der relocationDbDataSource-Bean genutzt wird
     * @Primary weist Spring an, dass dies die primäre DataSourceProperties-Bean ist (da unten noch eine zweite gebildet wird)
     * 
     * @return Konfigurationseinstellungen für eine Datenquelle
     */
    @Bean
    @Primary
    @ConfigurationProperties("relocation.datasource")
    public DataSourceProperties relocationDbDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * Basierend auf der relocationDbDataSourceProperties-Bean
     * wird diese Datenquelle erzeugt, abgesehen von den Konfigurationseinstellungen
     * mit Spring Boot-Standard-Einstellungen
     * 
     * @return Eine Datenquelle
     */
    @Bean
    @Primary
    public DataSource relocationDbDataSource() {
        return relocationDbDataSourceProperties().initializeDataSourceBuilder().build();
    }

    /**
     * Basierend auf den Einstellungen unter camunda.datasource in application.properties
     * wird eine DataSourceProperties-Bean erstellt, welche in der engineDbDataSourceProperties-Bean genutzt wird
     * 
     * @return Konfigurationseinstellungen für eine Datenquelle
     */
    @Bean
    @ConfigurationProperties("camunda.datasource")
    public DataSourceProperties engineDbDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * Basierend auf der engineDbDataSourceProperties-Bean
     * wird diese Datenquelle erzeugt, abgesehen von den Konfigurationseinstellungen
     * mit Spring Boot-Standard-Einstellungen
     * Der Name der Bean muss auf camundaBpmDataSource gesetzt werden, damit hier
     * die Process Engine-spezifischen Tabellen verwaltet werden
     * 
     * @return Eine Datenquelle
     */
    @Bean(name = "camundaBpmDataSource")
    public DataSource engineDbDataSource() {
        return engineDbDataSourceProperties().initializeDataSourceBuilder().build();
    }
}
