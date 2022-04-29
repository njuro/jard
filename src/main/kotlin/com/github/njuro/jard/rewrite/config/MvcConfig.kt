package com.github.njuro.jard.rewrite.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.njuro.jard.rewrite.common.API_ROOT_USERCONTENT
import com.github.njuro.jard.rewrite.common.USER_CONTENT_PATH
import com.github.njuro.jard.utils.HttpUtils
import com.github.njuro.jard.utils.PathVariableArgumentResolver
import com.github.njuro.jard.utils.validation.ValidationMessageInterpolator
import com.jfilter.EnableJsonFilter
import com.jfilter.FilterConstantsHelper.MEDIA_SUB_TYPE_JSON
import com.jfilter.FilterConstantsHelper.MEDIA_SUB_TYPE_JSON2
import com.jfilter.FilterConstantsHelper.MEDIA_TYPE_APPLICATION
import com.jfilter.components.FilterConfiguration
import org.apache.catalina.Context
import org.apache.tomcat.util.http.CookieProcessor
import org.apache.tomcat.util.http.Rfc6265CookieProcessor
import org.apache.tomcat.util.http.SameSiteCookies.NONE
import org.apache.tomcat.util.http.SameSiteCookies.STRICT
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.trace.http.HttpTraceRepository
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.servlet.server.ServletWebServerFactory
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.web.config.EnableSpringDataWebSupport
import org.springframework.http.CacheControl.noCache
import org.springframework.http.HttpMethod.DELETE
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.HEAD
import org.springframework.http.HttpMethod.OPTIONS
import org.springframework.http.HttpMethod.PATCH
import org.springframework.http.HttpMethod.POST
import org.springframework.http.HttpMethod.PUT
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.nio.charset.Charset.defaultCharset

/** Configuration of Spring MVC.  */
@Configuration
@EnableJsonFilter
@EnableSpringDataWebSupport // for resolving of parameters in controllers, such as Pageable
class MvcConfig(
    private val pathVariableArgumentResolvers: List<PathVariableArgumentResolver>,
    private val messageSource: MessageSource,
    @Value("\${client.base.url:localhost}") private val clientBaseUrl: String,
    @Value("\${server.base.url:localhost}") private val serverBaseUrl: String,
    @Value("\${DISABLE_CSRF_PROTECTION:false}") private val disableCsrfProtection: Boolean
) : WebMvcConfigurer {

    /** Sets JSON [ObjectMapper] for jfilter filter classes.  */
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    fun configureJsonFilter(
        filterConfiguration: FilterConfiguration,
        objectMapper: ObjectMapper
    ) {
        with (filterConfiguration) {
            setMapper(APPLICATION_JSON, objectMapper)
            setMapper(MediaType(MEDIA_TYPE_APPLICATION, MEDIA_SUB_TYPE_JSON, defaultCharset()), objectMapper)
            setMapper(MediaType(MEDIA_TYPE_APPLICATION, MEDIA_SUB_TYPE_JSON2, defaultCharset()), objectMapper)
        }
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        // serve content of local user content folder on user content endpoint
        // useful mostly for local development
        registry
            .addResourceHandler("$API_ROOT_USERCONTENT/**")
            .addResourceLocations("file:${USER_CONTENT_PATH.toAbsolutePath()}/")
            .setCacheControl(noCache())
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry
            .addMapping("/**")
            .allowedOrigins(
                HttpUtils.getOriginUrl(clientBaseUrl),
                HttpUtils.getOriginUrl(serverBaseUrl),
                "http://localhost:3000",
                "http://192.168.0.80:3000",
                "http://192.168.0.106:3000"
            )
            .allowedMethods(GET.name, POST.name, PUT.name, PATCH.name, DELETE.name, HEAD.name, OPTIONS.name)
            .allowCredentials(true) // for CSRF protection
    }

    /** Parses URL and returns  */
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.addAll(pathVariableArgumentResolvers)
    }

    @Bean
    @Primary
    override fun getValidator(): LocalValidatorFactoryBean =
        // register custom bean validator which loads validation messages from messages.properties
        // resource file and optionally do custom interpolation of variables in them
        LocalValidatorFactoryBean().apply {
            setValidationMessageSource(messageSource)
            messageInterpolator = ValidationMessageInterpolator(messageSource)
        }


    /** Enables Spring Actuator /httptrace endpoint for seeing most recent requests  */
    @Bean
    fun httpTraceRepository(): HttpTraceRepository = InMemoryHttpTraceRepository()

    @Bean
    @Primary
    fun cookieProcessor(): CookieProcessor {
        val sameSitePolicy = if (disableCsrfProtection) NONE else STRICT
        return Rfc6265CookieProcessor().apply {
            setSameSiteCookies(sameSitePolicy.value)
        }
    }

    @Bean
    fun servletContainer(): ServletWebServerFactory =
        object : TomcatServletWebServerFactory() {
            override fun postProcessContext(context: Context) {
                context.cookieProcessor = cookieProcessor()
            }
        }
}
