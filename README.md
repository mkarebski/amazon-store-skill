# amazon-store-skill
Amazon online store skill for Amazon Echo developed during Alexa Hackathon '16 

Official documentation links:

    https://developer.amazon.com/public/solutions/alexa/alexa-skills-kit/getting-started-guide

Skill will be deployed as a web service on AWS Elastin Beanstalk. For more information plese visit below site:

  https://developer.amazon.com/public/solutions/alexa/alexa-skills-kit/docs/deploying-a-sample-skill-as-a-web-service

## Configuration

Class com.antoniaklja.helper.ProductAdvertisingConstants contains following properties:

    AWS_ACCESS_KEY_ID - Your AWS Access Key ID, as taken from the AWS Your Account page
    AWS_SECRET_KEY -  Your AWS Secret Key corresponding to the above ID
    ENDPOINT - web store endpoind url according to the region you are interested in
    AWS_ASSOCIATES_KEY - Your AWS Associate key, as taken from the AWS Your Account page

## Building

Build simple by maven

    mvn clean install

## Testing

    todo
