package com.geodsea.pub.config;

import com.codahale.metrics.servlets.MetricsServlet;
import com.geodsea.ws.LicenseRequest;
import com.geodsea.ws.ObjectFactory;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.util.ClassUtils;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.server.endpoint.adapter.AbstractMethodEndpointAdapter;
import org.springframework.ws.server.endpoint.adapter.DefaultMethodEndpointAdapter;
import org.springframework.ws.server.endpoint.adapter.method.MarshallingPayloadMethodProcessor;
import org.springframework.ws.server.endpoint.adapter.method.MethodArgumentResolver;
import org.springframework.ws.server.endpoint.adapter.method.MethodReturnValueHandler;
import org.springframework.ws.server.endpoint.interceptor.PayloadLoggingInterceptor;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.transport.http.WsdlDefinitionHandlerAdapter;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;
import org.springframework.ws.wsdl.wsdl11.Wsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;
import org.springframework.xml.xsd.XsdSchemaCollection;
import org.springframework.xml.xsd.commons.CommonsXsdSchemaCollection;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.transform.TransformerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Just a sample for testing.
 */
@EnableWs
@Configuration
@ComponentScan(basePackageClasses = { WSConfigurer.class })
public class WSConfigurer extends WsConfigurerAdapter {

    private final Logger log = LoggerFactory.getLogger(WSConfigurer.class);

    @Override
    public void addInterceptors(List<EndpointInterceptor> interceptors) {
        interceptors.add(new MyInterceptor());
    }


//    @Bean
//    public ServletRegistrationBean dispatcherServlet(ApplicationContext applicationContext) {
//        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
//        servlet.setApplicationContext(applicationContext);
//        servlet.setTransformWsdlLocations(true);
//        return new ServletRegistrationBean(servlet, "/ws/*");
//    }


    @Bean(name = "licenses")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema licenseSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("LicensePort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace("http://www.geodsea.com/License");
        wsdl11Definition.setSchema(licenseSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema licenseSchema() {
        return new SimpleXsdSchema(new ClassPathResource("license.xsd"));
    }


    @Bean(name = "messageSender")
    public HttpComponentsMessageSender messageSender() {
        HttpComponentsMessageSender messageSender = new HttpComponentsMessageSender();

        // TODO remove this line
        messageSender.setCredentials(new UsernamePasswordCredentials("username", "password"));
        return messageSender;
    }

    @Bean(name="webServiceTemplate")
    public WebServiceTemplate apacheHttpClientWS()
    {
        SaajSoapMessageFactory factory = new SaajSoapMessageFactory();
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("messageSender", messageSender());
        props.put("defaultUri", "http://localhost:8080/ws");
        factory.setMessageProperties(props);
        factory.afterPropertiesSet();
        WebServiceTemplate t = new WebServiceTemplate(factory);
        Jaxb2Marshaller marshaller = marshaller();
        t.setMarshaller(marshaller);
        t.setUnmarshaller(marshaller);
        return t;
    }

    private Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(LicenseRequest.class);
        return marshaller;
    }

    private class MyInterceptor extends PayloadLoggingInterceptor implements EndpointInterceptor {

        private MyInterceptor() {
            super();
        }
    }
}
