fuser -k 9500/tcp || true
fuser -k 9080/tcp || true
export $(cat staging-identityprovider/.env | xargs -d '\n')
java -jar staging-identityprovider/libs/identityprovider-0.0.1-SNAPSHOT.jar \
    --server.port=9080 \
    --spring.application.name=identity-provider \
    --grpc.server.port=9500