package com.senorpez.projectcars;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.hal.CurieProvider;
import org.springframework.hateoas.hal.DefaultCurieProvider;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class Application {
    static final Set<CarClass> CAR_CLASSES = Collections.unmodifiableSet(getProjectCarsData(CarClass.class, "classes"));
    static final Set<Car> CARS = Collections.unmodifiableSet(getProjectCarsData(Car.class, "cars"));
    static final Set<Track> TRACKS = Collections.unmodifiableSet(getProjectCarsData(Track.class, "tracks"));
    static final Set<Event> EVENTS = Collections.unmodifiableSet(getProjectCarsData(Event.class, "events"));

    static final Set<Car2> CARS2 = Collections.unmodifiableSet(getProjectCars2Data(Car2.class, "cars"));
    static final Set<Track2> TRACKS2 = Collections.unmodifiableSet(getProjectCars2Data(Track2.class, "tracks"));

    private static final String HAL_OBJECT_MAPPER_BEAN_NAME = "_halObjectMapper";

    @Autowired
    private BeanFactory beanFactory;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    private static <T> Set<T> getProjectCarsData(Class objectClass, String field) {
        String resourceFileName = "projectcars.json";
        return getData(resourceFileName, objectClass, field);
    }

    private static <T> Set<T> getProjectCars2Data(Class objectClass, String field) {
        String resourceFileName = "projectcars2.json";
        return getData(resourceFileName, objectClass, field);
    }

    private static <T> Set<T> getData(String resourceFileName, Class objectClass, String field) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ClassLoader classLoader = Application.class.getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(resourceFileName);
            ObjectNode jsonData = mapper.readValue(inputStream, ObjectNode.class);
            return mapper.readValue(jsonData.get(field).toString(), mapper.getTypeFactory().constructCollectionType(Set.class, objectClass));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HashSet<>();
    }

    static <T> Set<T> getProjectCarsData(Class objectClass, JsonNode jsonData) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonData.toString(), mapper.getTypeFactory().constructCollectionType(Set.class, objectClass));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HashSet<>();
    }

    @Bean
    public HttpMessageConverters customConverters() {
        return new HttpMessageConverters(new Application.HalMappingJackson2HttpMessageConverter());
    }

    private class HalMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
        HalMappingJackson2HttpMessageConverter() {
            setSupportedMediaTypes(Collections.singletonList(
                    new MediaType("application", "doesntmatter") {
                        @Override
                        public boolean isCompatibleWith(MediaType other) {
                            if (other == null) {
                                return false;
                            } else if (other.getSubtype().startsWith("vnd.senorpez") && other.getSubtype().endsWith("+json")) {
                                return true;
                            }
                            return super.isCompatibleWith(other);
                        }
                    }
            ));

            ObjectMapper halObjectMapper = beanFactory.getBean(HAL_OBJECT_MAPPER_BEAN_NAME, ObjectMapper.class);
            setObjectMapper(halObjectMapper);
        }
    }

    @Bean
    public CurieProvider curieProvider() {
        return new DefaultCurieProvider("pcars", new UriTemplate("/{rel}"));
    }
}
