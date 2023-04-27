package com.umjari.server.global.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Component
@Configuration
class AwsS3Config {
    @Bean
    fun assetS3Client(
        @Value("\${cloud.aws.credentials.access-key}") accessKey: String,
        @Value("\${cloud.aws.credentials.secret-key}") secretKey: String,
        @Value("\${cloud.aws.region.static}") region: String,
    ): AmazonS3 {
        return AmazonS3ClientBuilder
            .standard()
            .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(accessKey, secretKey)))
            .withRegion(region)
            .build()
    }
}
