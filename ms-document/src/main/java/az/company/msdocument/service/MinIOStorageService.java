package az.company.msdocument.service;

import az.company.msdocument.model.dto.response.DownloadResult;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinIOStorageService {

    private final MinioClient minioClient;

    public void uploadFile(String bucketName, String objectName, MultipartFile file) {
        try {
            ensureBucketExists(bucketName);
            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );
                log.info("File [{}] uploaded to bucket [{}]", objectName, bucketName);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to MinIO: " + objectName, e);
        }
    }

    public void uploadFile(String bucketName, String objectName, byte[] data, String contentType) {
        try {
            ensureBucketExists(bucketName);
            try (InputStream inputStream = new java.io.ByteArrayInputStream(data)) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .stream(inputStream, data.length, -1)
                                .contentType(contentType)
                                .build()
                );
                log.info("File [{}] uploaded (bytes) to bucket [{}]", objectName, bucketName);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload byte[] to MinIO: " + objectName, e);
        }
    }

    public DownloadResult downloadFile(String bucketName, String objectName) {
        try {
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build();

            try (InputStream stream = minioClient.getObject(getObjectArgs);
                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = stream.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }
                byte[] content = baos.toByteArray();

                String contentType = java.net.URLConnection.guessContentTypeFromName(objectName);
                if (contentType == null) contentType = "application/octet-stream";

                return new DownloadResult(content, objectName, contentType);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file from MinIO: " + objectName, e);
        }
    }

    public void deleteFile(String bucketName, String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            log.info("File [{}] deleted from bucket [{}]", objectName, bucketName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from MinIO: " + objectName, e);
        }
    }

    private void ensureBucketExists(String bucketName) {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!exists) {
                try {
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                    log.info("Bucket [{}] created", bucketName);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to create bucket: " + bucketName, e);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Bucket check/creation failed for: " + bucketName, e);
        }
    }
}
