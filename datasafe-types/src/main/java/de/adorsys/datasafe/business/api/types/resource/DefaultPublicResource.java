package de.adorsys.datasafe.business.api.types.resource;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.net.URI;

@ToString
@RequiredArgsConstructor
public class DefaultPublicResource implements PublicResource {

    private final URI uri;

    public static AbsoluteResourceLocation<PublicResource> forAbsolutePublic(URI path) {
        return new AbsoluteResourceLocation<>(new DefaultPublicResource(path));
    }

    @Override
    public URI location() {
        return uri;
    }

    @Override
    public PublicResource resolve(ResourceLocation location) {
        return new DefaultPublicResource(location.location().resolve(uri));
    }
}