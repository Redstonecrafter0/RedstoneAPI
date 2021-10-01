package net.redstonecraft.redstoneapi.webserver;

import net.redstonecraft.redstoneapi.webserver.annotations.methods.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public enum HttpMethod {

    GET(false, true, Get.class),
    POST(true, true, Post.class),
    HEAD(false, false, Head.class),
    PUT(true, false, Put.class),
    DELETE(false, true, Delete.class),
    OPTIONS(false, true, Options.class),
    PATCH(true, true, Patch.class);

    private final boolean hasBody;
    private final boolean hasBodyResponse;
    private final Class<? extends Annotation> annotationClass;

    HttpMethod(boolean hasBody, boolean hasBodyResponse, Class<? extends Annotation> annotationClass) {
        this.hasBody = hasBody;
        this.hasBodyResponse = hasBodyResponse;
        this.annotationClass = annotationClass;
    }

    public boolean hasBody() {
        return hasBody;
    }

    public boolean hasBodyResponse() {
        return hasBodyResponse;
    }

    public Class<? extends Annotation> getAnnotationClass() {
        return annotationClass;
    }

    public static List<HttpMethod> getFromMethod(Method method) {
        List<HttpMethod> methods = new ArrayList<>();
        for (HttpMethod i : values()) {
            if (method.isAnnotationPresent(i.getAnnotationClass())) {
                methods.add(i);
            }
        }
        if (methods.isEmpty()) {
            methods.add(GET);
        }
        return methods;
    }

    public static boolean isMethodAvailable(String method) {
        try {
            HttpMethod.valueOf(method);
            return true;
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

}
