# oneswap-worker
OneSwap worker back-end
### Project Introduction
**OneSwap** is a decentralized exchange (DEX) aggregator for cryptocurrency token transactions. It offers two primary features: **Swap** and **Limit Orders**, providing users with flexibility in trading. Currently, OneSwap supports both **Uniswap v2** and **Balancer v2**, with plans to integrate additional protocols in the future.
### Architecture
OneSwap consists of front-end, back-end and smart contracts.
![OneSwap-flow](https://github.com/user-attachments/assets/d943d23e-a703-454b-b9d1-d398694e96c7)
- The front-end is built with React, interacts with the blockchain using Web3.js, is hosted on S3, and uses CloudFront to optimize loading speed.
- Worker Back-End is responsible for processing front-end user demands, calculating and returning the best price to the user. Built on EC2 and using AWS Load Balancers and Auto Scaling to ensure service stability.
- Core Back-End is responsible for monitoring the liquidity changes and transaction records, which are stored in RDS and ElastiCache respectively. Use CloudWatch to monitor system exceptions and Lambda to switch backup EC2.
- There are two smart contracts: Aggregator and Limit Order, which handle spot transactions and limit transactions, respectively. The Limit Order contract is monitored and triggered by the Core Back-End for execution.
![OneSwap-architecture](https://github.com/user-attachments/assets/7975d183-be28-4dad-befd-4c166cb96cc7)
### **Local Deployment**

1. Make sure that the MySQL and Redis environments exist.
2. Pull the Worker Back-End image from Docker Hub:
    ```
    docker pull kai410705/oneswap:latest
    ```
3. Create the application.properties:
    ```
    # server setting
    spring.application.name=oneswap-worker
    server.port=8080
    
    # service option
    blockchain=Sepolia
    ONESWAP_FEE=0.2
    
    # API
    INFRA_ETHEREUM_WEBSOCKET_URL=yourInfuraWebSocketKey
    ALCHEMY_ETHEREUM_REST_URL=yourAlchemyKey
    ALCHEMY_SEPOLIA_REST_URL=yourInfuraRestKey
    ALCHEMY_ETHEREUM_WEBSOCKET_URL=yourAlchemyKey
    
    # MySQL
    spring.datasource.url=jdbc:mysql://localhost:3306/oneswap
    spring.datasource.username=yourUser
    spring.datasource.password=yourPassword
    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
    
    # JPA
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.database=mysql
    spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
    
    # Redis
    spring.data.redis.port=6379
    spring.data.redis.host=localhost
    spring.data.redis.password=yourPassword
    spring.data.redis.timeout=1000
    redis.ssl.enable=false
    
    # cache setting
    spring.cache.type=redis
    spring.cache.redis.time-to-live=1000s
    spring.cache.redis.use-key-prefix=false
    
    # Log setting
    logging.file.name=application.log
    logging.logback.rollingpolicy.max-history=0
    ```
4. Run a container:
    ```bash
    docker run --rm --name oneswap-core-container -p 8080:8080 -v /your/payh/application.properties:/app/application.properties -v /your/payh/application.log:/app/application.log kai410705/oneswap:latest
    ```
5. Setup the Front-End and Core Back-End server.
    - Front-End: https://github.com/Drinkaiii/oneswap-interface
    - Core Back-End: https://github.com/Drinkaiii/oneswap-core


