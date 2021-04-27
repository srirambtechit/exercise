package net.cloud.imageprocessor.util;

import lombok.extern.log4j.Log4j2;
import net.cloud.imageprocessor.exception.ChecksumNotMatchException;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@Log4j2
@Component
public class RequestMessageVerifier {

    public void validateChecksum(String checksum, String content) {
        if (Objects.isNull(checksum) || Objects.isNull(content)) return;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(content.getBytes());
            String hash = DatatypeConverter.printHexBinary(digest);
            if (!hash.equals(checksum.toUpperCase()))
                throw new ChecksumNotMatchException("Checksum mismatch: MD5(input) != <generated md5 checksum>");
        } catch (NoSuchAlgorithmException e) {
            log.error("MD5 algorithm is missing, reason {}", e.getMessage());
            throw new ChecksumNotMatchException(e.getMessage());
        }
    }

}
