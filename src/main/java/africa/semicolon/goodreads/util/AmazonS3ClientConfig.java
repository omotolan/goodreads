package africa.semicolon.goodreads.util;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonS3ClientConfig {
    private final String accessKeyId;
    private final String accessKeySecret;

    public AmazonS3ClientConfig() {
        this.accessKeyId = System.getenv("AWSAccessKeyId");
        this.accessKeySecret = System.getenv("AWSSecretKey");
    }

    @Bean
    public AmazonS3 getAmazonS3Client() {
        final BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKeyId, accessKeySecret);

        return AmazonS3ClientBuilder
                .standard()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
                .build();
    }
}

