package com.geodsea.pub.web.rest;

import com.geodsea.pub.domain.Participant;
import com.geodsea.pub.domain.Person;
import com.geodsea.pub.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.context.SpringWebContext;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Utility methods for a service that needs to send registration emails
 */
public class ParticipantResource {

    @Autowired
    protected ApplicationContext applicationContext;

    @Autowired
    protected ServletContext servletContext;

    @Inject
    protected MailService mailService;

    @Inject
    private SpringTemplateEngine templateEngine;


    protected String createHtmlContentFromTemplate(final Participant participant, final Locale locale, final HttpServletRequest request,
                                                 final HttpServletResponse response) {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("participant", participant);
        variables.put("baseUrl", createBaseUrl(request));
        IWebContext context = new SpringWebContext(request, response, servletContext,
                locale, variables, applicationContext);
        return templateEngine.process(MailService.EMAIL_ACTIVATION_PREFIX + MailService.TEMPLATE_SUFFIX, context);
    }


    protected String createBaseUrl(HttpServletRequest request) {
        return request.getScheme() + "://" +   // "http" + "://
                request.getServerName() +       // "myhost"
                ":" + request.getServerPort() +
                request.getContextPath(); // geodsea
    }

}
