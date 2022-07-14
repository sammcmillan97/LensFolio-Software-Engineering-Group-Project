fuser -k 9500/tcp || true
fuser -k 9502/tcp || true
export $(cat staging-identityprovider/.env | xargs -d '\n')
WEB_CONTEXT=/test/identity java -jar staging-identityprovider/libs/identityprovider-0.0.1-SNAPSHOT.jar \
    --server.port=9500 \
    --spring.application.name=identity-provider \
    --grpc.server.port=9502