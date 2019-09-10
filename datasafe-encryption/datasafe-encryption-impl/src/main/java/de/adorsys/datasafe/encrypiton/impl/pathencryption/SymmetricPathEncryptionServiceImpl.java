package de.adorsys.datasafe.encrypiton.impl.pathencryption;

import de.adorsys.datasafe.encrypiton.api.pathencryption.encryption.SymmetricPathEncryptionService;
import de.adorsys.datasafe.encrypiton.api.types.keystore.PathEncryptionSecretKey;
import de.adorsys.datasafe.encrypiton.impl.pathencryption.dto.PathSecretKeyWithData;
import de.adorsys.datasafe.types.api.context.annotations.RuntimeDelegate;
import de.adorsys.datasafe.types.api.resource.Uri;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.net.URI;
import java.util.Arrays;
import java.util.Base64;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Path encryption service that maintains URI segments integrity.
 * It means that path/to/file is encrypted to cipher(path)/cipher(to)/cipher(file) and each invocation of example:
 * cipher(path) will yield same string.
 */
@Slf4j
@RuntimeDelegate
public class SymmetricPathEncryptionServiceImpl implements SymmetricPathEncryptionService {

    private static final int DOT_SLASH_PREFIX_LENGTH = 2;
    private static final String DOT_SLASH_PREFIX = "./";
    private static final String PATH_SEPARATOR = "/";

    private final Function<PathSecretKeyWithData, byte[]> encryptAndEncode;
    private final Function<PathSecretKeyWithData, byte[]> decryptAndDecode;

    @Inject
    public SymmetricPathEncryptionServiceImpl(PathEncryptorDecryptor pathEncryptorDecryptor) {
        encryptAndEncode = keyEntryEncryptedDataPair -> encode(pathEncryptorDecryptor.encrypt(
                keyEntryEncryptedDataPair.getPathEncryptionSecretKey(), keyEntryEncryptedDataPair.getData()
        ));

        decryptAndDecode = keyEntryDecryptedDataPair -> pathEncryptorDecryptor.decrypt(
                keyEntryDecryptedDataPair.getPathEncryptionSecretKey(), decode(keyEntryDecryptedDataPair.getData())
        );
    }

    /**
     * Encrypts each URI segment separately and composes them back in same order.
     */
    @Override
    @SneakyThrows
    public Uri encrypt(PathEncryptionSecretKey pathEncryptionSecretKey, Uri bucketPath) {
        validateArgs(pathEncryptionSecretKey, bucketPath);
        validateUriIsRelative(bucketPath);

        return processURIparts(
                pathEncryptionSecretKey,
                bucketPath,
                encryptAndEncode
        );
    }

    /**
     * Decrypts each URI segment separately and composes them back in same order.
     */
    @Override
    @SneakyThrows
    public Uri decrypt(PathEncryptionSecretKey pathEncryptionSecretKey, Uri bucketPath) {
        validateArgs(pathEncryptionSecretKey, bucketPath);
        validateUriIsRelative(bucketPath);

        return processURIparts(
                pathEncryptionSecretKey,
                bucketPath,
                decryptAndDecode
        );
    }

    @SneakyThrows
    private static byte[] decode(byte[] encryptedData) {
        if (null == encryptedData || encryptedData.length == 0) {
            return encryptedData;
        }

        return Base64.getUrlDecoder().decode(encryptedData);
    }

    @SneakyThrows
    private static byte[] encode(byte[] encryptedData) {
        if (null == encryptedData) {
            return null;
        }

        return Base64.getUrlEncoder().encodeToString(encryptedData).getBytes(UTF_8);
    }

    private static Uri processURIparts(
            PathEncryptionSecretKey secretKeyEntry,
            Uri bucketPath,
            Function<PathSecretKeyWithData, byte[]> process) {
        StringBuilder result = new StringBuilder();

        String path = bucketPath.getRawPath();
        if (bucketPath.getRawPath().startsWith(DOT_SLASH_PREFIX)) {
            result.append(DOT_SLASH_PREFIX);
            path = bucketPath.getRawPath().substring(DOT_SLASH_PREFIX_LENGTH);
        }

        if (path.isEmpty()) {
            return new Uri(result.toString());
        }

        // Resulting value of `path` is URL-safe
        return new Uri(
                URI.create(
                        Arrays.stream(path.split(PATH_SEPARATOR))
                                .map(uriPart -> process.apply(
                                        new PathSecretKeyWithData(secretKeyEntry, uriPart.getBytes(UTF_8))))
                                .map(String::new) // byte[] -> string
                                .collect(Collectors.joining(PATH_SEPARATOR)))
        );
    }

    private static void validateArgs(PathEncryptionSecretKey secretKeyEntry, Uri bucketPath) {
        if (null == secretKeyEntry || null == secretKeyEntry.getSecretKey()) {
            throw new IllegalArgumentException("Secret key should not be null");
        }

        if (null == bucketPath) {
            throw new IllegalArgumentException("Bucket path should not be null");
        }
    }

    private static void validateUriIsRelative(Uri uri) {
        if (uri.isAbsolute()) {
            throw new IllegalArgumentException("URI should be relative");
        }
    }
}