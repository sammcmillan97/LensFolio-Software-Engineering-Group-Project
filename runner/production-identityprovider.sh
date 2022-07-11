fuser -k 10500/tcp || true
fuser -k 10502/tcp || true
export $(cat production-identityprovider/.env | xargs -d '\n')
WEB_CONTEXT=/prod/identity java -jar production-identityprovider/libs/identityprovider-0.0.1-SNAPSHOT.jar \
    --server.port=10500 \
    --spring.application.name=identity-provider \
    --grpc.server.port=10502