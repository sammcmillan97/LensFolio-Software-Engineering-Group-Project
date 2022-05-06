fuser -k 9501/tcp || true
export $(cat staging-portfolio/.env | xargs -d '\n')
java -jar staging-portfolio/libs/portfolio-0.0.1-SNAPSHOT.jar \
    --spring.mvc.servlet.path=/test/portfolio \
    --server.port=9501 \
    --spring.application.name=portfolio \
    --grpc.client.identity-provider-grpc-server.address=static://127.0.0.1:9500 \
    --grpc.client.identity-provider-grpc-server.enableKeepAlive=true \
    --grpc.client.identity-provider-grpc-server.keepAliveWithoutCalls=true \
    --grpc.client.identity-provider-grpc-server.negotiationType=plaintext