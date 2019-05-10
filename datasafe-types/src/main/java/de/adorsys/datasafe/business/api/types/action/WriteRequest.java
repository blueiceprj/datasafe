package de.adorsys.datasafe.business.api.types.action;

import de.adorsys.datasafe.business.api.types.resource.*;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.net.URI;

@Value
@Builder(toBuilder = true)
public class WriteRequest<T, L extends ResourceLocation> {

    @NonNull
    private final T owner;

    @NonNull
    private final L location;

    public static <T> WriteRequest<T, PrivateResource> forDefaultPrivate(T owner, String path) {
        return new WriteRequest<>(owner, DefaultPrivateResource.forPrivate(URI.create(path)));
    }

    public static <T> WriteRequest<T, PublicResource> forDefaultPublic(T owner, String path) {
        return new WriteRequest<>(owner, new DefaultPublicResource(URI.create(path)));
    }

    public static <T> WriteRequest<T, PrivateResource> forDefaultPrivate(T owner, URI path) {
        return new WriteRequest<>(owner, DefaultPrivateResource.forPrivate(path));
    }

    public static <T> WriteRequest<T, PublicResource> forDefaultPublic(T owner, URI path) {
        return new WriteRequest<>(owner, new DefaultPublicResource(path));
    }
}
