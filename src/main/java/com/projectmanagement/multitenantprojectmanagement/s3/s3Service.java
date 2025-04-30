package com.projectmanagement.multitenantprojectmanagement.s3;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class s3Service {
    private final S3Client s3Client;
    private final s3Config s3Config;
    private static final Logger logger = LoggerFactory.getLogger(s3Service.class);
    private final MaskingString maskingString;

    public String uploadFile(MultipartFile file, UUID id,String folder) throws IOException {
        logger.info("Started uploading file to s3: {} {} {} ", file.getOriginalFilename(), id, folder);
        try {
            String originalFilename = Paths.get(file.getOriginalFilename()).getFileName().toString();
            String fileName = folder + "/" + id.toString() + "/" + originalFilename;
            
            logger.debug("Filename for the given file: {}", maskingString.maskSensitive(fileName));
    
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                                                .bucket(s3Config.getBucketName())
                                                .key(fileName)
                                                .contentType(file.getContentType())
                                                .build();
    
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            
            logger.debug("File successfully uploaded");
    
            return "https://" + s3Config.getBucketName() + ".s3." + s3Config.getRegion() + ".amazonaws.com/" + fileName;
        }catch(Exception e) {
            logger.error("Error occurred while uploading file to S3", e);
            throw new RuntimeException(e.getMessage().length() > 0 ? e.getMessage() : "Something went wrong when trying to connect with aws");
        }
    }

}
