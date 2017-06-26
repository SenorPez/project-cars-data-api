package com.senorpez.projectcars;

import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.mvc.IdentifiableResourceAssemblerSupport;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

class IdentifiableResourceAssembler<T extends Identifiable<?>, U extends ResourceSupport> extends IdentifiableResourceAssemblerSupport<T, U> {
    private final Class<U> resourceType;

    IdentifiableResourceAssembler(Class<?> controllerClass, Class<U> resourceType) {
        super(controllerClass, resourceType);
        this.resourceType = resourceType;
    }

    @Override
    public U toResource(T entity) {
        return createResource(entity);
    }

    @Override
    protected U instantiateResource(T entity) {
        try {
            Constructor<U> con = resourceType.getConstructor(Object.class, Link[].class);
            return con.newInstance(entity, new Link[]{});
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }
}